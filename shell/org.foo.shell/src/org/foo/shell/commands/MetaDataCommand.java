package org.foo.shell.commands;

import java.io.PrintStream;

import org.foo.shell.BasicCommand;
import org.osgi.framework.Bundle;
import org.osgi.service.metatype.*;

public class MetaDataCommand extends BasicCommand {

  public void exec(String args, PrintStream out, PrintStream err)
    throws Exception {
    MetaTypeService mts = getMetaTypeService();
    Bundle b = getBundle(args);
    MetaTypeInformation mti = mts.getMetaTypeInformation(b);
    String[] pids = mti.getPids();
    for (int i = 0; i < pids.length; i++) {
      out.println(pids[i]);
      ObjectClassDefinition ocd = mti.getObjectClassDefinition(
        pids[i], null);
      AttributeDefinition[] ads = ocd
        .getAttributeDefinitions(ObjectClassDefinition.ALL);
      for (int j = 0; j < ads.length; j++) {
        out.println("\tOCD=" + ocd.getName());
        out.println("\t\tAD=" + ads[j].getName() + " - " +
          ads[j].getDescription());
      }
    }
  }

  private MetaTypeService getMetaTypeService() {
    return (MetaTypeService) m_context.getService(m_context
      .getServiceReference(MetaTypeService.class.getName()));
  }

}
