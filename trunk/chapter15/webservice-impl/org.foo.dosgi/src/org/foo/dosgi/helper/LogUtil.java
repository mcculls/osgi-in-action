package org.foo.dosgi.helper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class LogUtil {
  public static BundleContext ctx;
  private final String id;

  private LogUtil(String id) {
    this.id = id;    
  }

  public void debug(String msg) {
    debug(msg, null);
  }

  public void debug(String msg, Throwable t) {
    log(ctx, LogService.LOG_DEBUG, id, msg, t);
  }

  public void info(String msg) {
    info(msg, null);
  }

  public void info(String msg, Throwable t) {
    log(ctx, LogService.LOG_INFO, id, msg, t);
  }

  public void warn(String msg) {
    warn(msg, null);
  }

  public void warn(String msg, Throwable t) {
    log(ctx, LogService.LOG_WARNING, id, msg, t);
  }
  
  public static LogUtil getLog(String id) {
    return new LogUtil(id);
  }
  
  private static void log(BundleContext ctx, int level, String id, String msg,
      Throwable t) {
    String out = id + ": " + msg;
    ServiceReference ref = ctx.getServiceReference(LogService.class
        .getName());
    if (ref != null) {
      LogService log = (LogService) ctx.getService(ref);
      if (log != null) {
        try {
          log.log(level, out, t);
          return;
        } finally {
          ctx.ungetService(ref);
        }
      }
    }
        
//    // ok not found so
//    System.err.println( out );
//    if ( t != null ) {
//      t.printStackTrace(System.err);
//    }
  }

}
