package org.foo.dosgi.helper;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class LogUtil {
  public static BundleContext ctx;

  public static void debug(String msg) {
    debug(msg, null);
  }

  public static void debug(String msg, Throwable t) {
    log(ctx, LogService.LOG_DEBUG, msg, t);
  }

  public static void info(String msg) {
    info(msg, null);
  }

  public static void info(String msg, Throwable t) {
    log(ctx, LogService.LOG_INFO, msg, t);
  }

  public static void warn(String msg) {
    warn(msg, null);
  }

  public static void warn(String msg, Throwable t) {
    log(ctx, LogService.LOG_WARNING, msg, t);
  }

  private static void log(BundleContext ctx, int level, String msg) {
    log(ctx, level, msg, null);
  }

  private static void log(BundleContext ctx, int level, String msg,
      Throwable t) {
    ServiceReference ref = ctx.getServiceReference(LogService.class
        .getName());
    if (ref != null) {
      LogService log = (LogService) ctx.getService(ref);
      if (log != null) {
        try {
          log.log(level, msg, t);
          return;
        } finally {
          ctx.ungetService(ref);
        }
      }
    }
    //    
    // // ok not found so
    // System.err.println( msg );
    // if ( t != null ) {
    // t.printStackTrace(System.err);
    // }
  }

}
