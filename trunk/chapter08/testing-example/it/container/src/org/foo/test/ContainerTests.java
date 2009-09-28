package org.foo.test;

import org.junit.runner.RunWith;
import org.junit.Test;

import org.ops4j.pax.exam.junit.*;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.osgi.framework.Constants.*;
import org.osgi.framework.*;

@RunWith(JUnit4TestRunner.class)
public class ContainerTests {

  @Configuration
  public Option[] configure() {
    return options(
      frameworks(
        felix(),
        equinox(),
        knopflerfish()
      ));
  }

  @Test
  public void testContainer( BundleContext context ) {
    System.out.println( new StringBuilder()
      .append( format( context, FRAMEWORK_VENDOR ) )
      .append( format( context, FRAMEWORK_VERSION ) )
      .append( format( context, FRAMEWORK_LANGUAGE ) )
      .append( format( context, FRAMEWORK_OS_NAME ) )
      .append( format( context, FRAMEWORK_OS_VERSION ) )
      .append( format( context, FRAMEWORK_PROCESSOR ) )
      .append( "\nTest Bundle is " )
      .append( context.getBundle().getSymbolicName() ) );
  }


  private static String format( BundleContext context, String key ) {
    return String.format( "%-32s = %s\n", key, context.getProperty( key ) );
  }
}

