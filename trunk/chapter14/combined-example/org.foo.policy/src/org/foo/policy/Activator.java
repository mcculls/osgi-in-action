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

    private volatile List m_previous;

    public void start(BundleContext context) throws Exception
    {
        String policyFilePath = context.getProperty("org.foo.policy.file");
        if (policyFilePath == null) {
            policyFilePath = "security.policy";
        }
        File policyFile = new File(policyFilePath);
        if (!policyFile.isFile()) {
            throw new BundleException("No policy file at: " + policyFile.getAbsolutePath());
        }
        ConditionalPermissionAdmin cpa = getCPA(context);
        
        if (cpa == null) {
            throw new BundleException("No ConditionalPermissionAdmin found.");
        }
        BufferedReader policyReader = null;
        Exception org = null;
        try
        {
            policyReader = new BufferedReader(new FileReader(policyFile));
            List policy = new ArrayList();
            policy.add("ALLOW {" +
                "[org.osgi.service.condpermadmin.BundleLocationCondition \"" + context.getBundle().getLocation() + "\"]" +
                "(java.security.AllPermission \"*\" \"*\")" +
                "} \"Management Agent Policy\"");
            StringBuffer buffer = new StringBuffer();
            for (String input = policyReader.readLine(); input != null; input = policyReader.readLine()) {
                if (input.trim().startsWith("#")) {continue;}
                buffer.append(input);
                if (input.contains("}")) {
                    policy.add(buffer.toString());
                    buffer = new StringBuffer();
                }
            }
            m_previous = setUpPolicy(cpa, policy);
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

    private List setUpPolicy(ConditionalPermissionAdmin cpa,
        List policy) throws ConcurrentModificationException
    {
        ConditionalPermissionUpdate update = cpa.newConditionalPermissionUpdate();
        List list = update.getConditionalPermissionInfos();
        List previous = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            previous.add(((ConditionalPermissionInfo) iter.next()).getEncoded());
        }
        list.clear();
        for (Iterator iter = policy.iterator();iter.hasNext();) {
            list.add(cpa.newConditionalPermissionInfo((String) iter.next()));
        }
        if (!update.commit()) {
            throw new ConcurrentModificationException("Conditional Permission Admin was updated concurrently");
        }
        return previous;
    }

    public void stop(BundleContext context) throws Exception
    {
        ConditionalPermissionAdmin cpa = getCPA(context);
        if (cpa == null) {
            throw new BundleException("No ConditionalPermissionAdmin found.");
        }
        if (m_previous != null) {
            setUpPolicy(cpa, m_previous);
        }
    }

    private ConditionalPermissionAdmin getCPA(BundleContext context) throws BundleException
    {
        ServiceReference ref = context.getServiceReference(ConditionalPermissionAdmin.class.getName());
        ConditionalPermissionAdmin result = null;
        if (ref != null) {
            result = (ConditionalPermissionAdmin) context.getService(ref);
        }
        return result;
    }

}
