package org.foo.leaky;

import org.osgi.framework.*;

/**
 * To run this example, first build it with ant then type:
 * 
 *   java -verbose:gc -jar launcher.jar bundles
 * 
 * You should see the example start with GC trace enabled:
 * 
 *   [GC 896K->136K(5056K), 0.0029125 secs]
 *   ...
 *   ->
 *   
 * If you try to update the bundle you should see a leak:
 *
 *   [Full GC 320K->320K(5056K), 0.0280867 secs]
 *   -> update 3
 *   [GC 16979K->16717K(21444K), 0.0014941 secs]
 *   [Full GC 16717K->16714K(21444K), 0.0256443 secs]
 *   -> update 3
 *   [GC 33378K->33118K(46424K), 0.0009662 secs]
 *   [Full GC 33118K->33118K(46424K), 0.0268457 secs]
 *   -> update 3
 *   [GC 49777K->49514K(59424K), 0.0009663 secs]
 *   [Full GC 49514K->49463K(59424K), 0.0929226 secs]
 *   [Full GC 49463K->49452K(65088K), 0.0452441 secs]
 *   java.lang.OutOfMemoryError: Java heap space
 *   ->
 * 
 * Uncomment the call to remove(), rebuild and try again:
 * 
 *   [Full GC 320K->320K(5056K), 0.0238252 secs]
 *   -> update 3
 *   [GC 16973K->16717K(21444K), 0.0021946 secs]
 *   [Full GC 16717K->16717K(21444K), 0.0296025 secs]
 *   -> update 3
 *   [GC 33373K->33114K(46428K), 0.0010646 secs]
 *   [Full GC[Unloading class org.foo.leaky.Activator$Data]
 *   [Unloading class org.foo.leaky.Activator$1]
 *   [Unloading class org.foo.leaky.Activator]
 *    33114K->16721K(46428K), 0.0557759 secs]
 *    
 * The leak is now gone and the classes are unloaded.
 */
public class Activator implements BundleActivator {

  /*
   * 8Mb data object
   */
  static class Data {
    StringBuffer data = new StringBuffer(8 * 1024 * 1024);
  }

  /*
   * This example relies on a "feature" of the Java5 ThreadLocal implementation
   * where stale map entries are only cleared if set() or remove() is called on
   * another ThreadLocal for the same thread - and in the worst case even this
   * is not guaranteed to purge all stale map entries.
   * 
   * As we shall soon see, missing out the remove() call in stop means that the
   * data object will be kept alive indefinitely because we don't use any other
   * ThreadLocal in our example. This in turn keeps our ClassLoader alive.
   * 
   * Calling remove() in stop forces the underlying map entry to be cleared and
   * means the bundle's ClassLoader can now be collected on each update/refresh.
   */
  static final ThreadLocal leak = new ThreadLocal() {
    protected Object initialValue() {
      return new Data();
    };
  };

  public void start(BundleContext ctx) {
    leak.get();
  }

  public void stop(BundleContext ctx) {
    // leak.remove();
  }
}
