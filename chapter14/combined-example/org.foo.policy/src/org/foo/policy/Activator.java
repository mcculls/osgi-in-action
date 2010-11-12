package org.foo.policy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;

public class Activator implements BundleActivator
{

    public void start(BundleContext context) throws Exception
    {
      File policyFile = getPolicyFile(context);                             
      List<String> encodedInfos = readPolicyFile(policyFile);
	  encodedInfos.add(0, "ALLOW {"                                          
        + "[org.osgi.service.condpermadmin.BundleLocationCondition \""
        + context.getBundle().getLocation() + "\"]"
        + "(java.security.AllPermission \"*\" \"*\")"
        + "} \"Management Agent Policy\"");
      ConditionalPermissionAdmin cpa = getConditionalPermissionAdmin(context);      
      ConditionalPermissionUpdate u = cpa.newConditionalPermissionUpdate();  
      List infos = u.getConditionalPermissionInfos();                        
      infos.clear();                                                         
      for (String encodedInfo : encodedInfos) {                              
        infos.add(cpa.newConditionalPermissionInfo(encodedInfo));
      }
      if (!u.commit()) {                                                     
        throw new ConcurrentModificationException(                          
		  "Permissions changed during update");
      }
    }

    private File getPolicyFile(BundleContext context) throws BundleException {
        String policyFilePath = context.getProperty("org.foo.policy.file");
        if (policyFilePath == null) {
            policyFilePath = "security.policy";
        }
        File policyFile = new File(policyFilePath);
        if (!policyFile.isFile()) {
            throw new BundleException("No policy file at: " + policyFile.getAbsolutePath());
        }
		return policyFile;
	}
	
	private List<String> readPolicyFile(File policyFile) throws Exception {
        BufferedReader policyReader = null;
        Exception org = null;
        try
        {
            policyReader = new BufferedReader(new FileReader(policyFile));
            List policy = new ArrayList();
            StringBuffer buffer = new StringBuffer();
            for (String input = policyReader.readLine(); input != null; input = policyReader.readLine()) {
                if (!input.trim().startsWith("#")) {
                  buffer.append(input);
                  if (input.contains("}")) {
                    policy.add(buffer.toString());
                    buffer = new StringBuffer();
                  }
				}
            }
			return policy;
        }
        catch (Exception ex) {
            org = ex;
            throw ex;
        }
        finally {
            if (policyReader != null) {
                try
                {
                    policyReader.close();
                }
                catch (Exception ex) {
                    if (org == null) {
                        throw ex;
                    }
                }
            }
        }
    }

    public void stop(BundleContext context) throws Exception
    {
    }

    private ConditionalPermissionAdmin getConditionalPermissionAdmin(BundleContext context) throws BundleException
    {
        ServiceReference ref = context.getServiceReference(ConditionalPermissionAdmin.class.getName());
        ConditionalPermissionAdmin result = null;
        if (ref != null) {
            result = (ConditionalPermissionAdmin) context.getService(ref);
        }
        return result;
    }

}
