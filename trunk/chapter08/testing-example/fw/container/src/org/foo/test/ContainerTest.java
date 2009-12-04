package org.foo.test;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.osgi.framework.Constants.*;

import org.junit.runner.RunWith;
import org.junit.Test;

import org.ops4j.pax.exam.junit.*;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.*;

@RunWith(JUnit4TestRunner.class)
public class ContainerTest {

  @Configuration
  public static Option[] configure() {
    return options(
      mavenBundle("org.osgi", "org.osgi.compendium", "4.2.0")
    );
  }

  @Test
  public void testContainer(BundleContext ctx) {
    System.out.println(
      format(ctx, FRAMEWORK_VENDOR) +
      format(ctx, FRAMEWORK_VERSION) +
      format(ctx, FRAMEWORK_LANGUAGE) +
      format(ctx, FRAMEWORK_OS_NAME) +
      format(ctx, FRAMEWORK_OS_VERSION) +
      format(ctx, FRAMEWORK_PROCESSOR) +
      "\nTest Bundle is " +
      ctx.getBundle().getSymbolicName());
  }

  private static String format(
      BundleContext ctx, String key) {

    return String.format("%-32s = %s\n",
        key, ctx.getProperty(key));
  }
}

