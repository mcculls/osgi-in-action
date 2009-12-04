package org.apache.felix.cm.integration.mgmt;

import static org.ops4j.pax.exam.CoreOptions.*;

import java.io.*;
import java.net.URL;
import java.util.Dictionary;
import junit.framework.TestCase;
import org.apache.felix.cm.integration.ConfigurationTestBase;
import org.apache.felix.cm.integration.helper.ManagedServiceTestActivator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.*;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;

@RunWith(JUnit4TestRunner.class)
public class ConfigAdminUpgradeTest extends ConfigurationTestBase {

  private static String toFileURI(String path) {
    return new File(path).toURI().toString();
  }

  @org.ops4j.pax.exam.junit.Configuration
  public static Option[] configuration() {
    return options(
      provision(
        bundle(toFileURI("bundles/integration_tests-1.0.jar")),
        bundle(toFileURI("bundles/old.configadmin.jar")),
        mavenBundle("org.osgi", "org.osgi.compendium", "4.2.0"),
        mavenBundle("org.ops4j.pax.swissbox", "pax-swissbox-tinybundles",
                    "1.0.0")
      ),
      systemProperty("new.configadmin.uri").
        value(toFileURI("bundles/configadmin.jar"))
    );
  }

  @Test
  public void test_upgradeConfigAdmin() throws BundleException, IOException {

    Dictionary headers = getCmBundle().getHeaders();
    TestCase.assertEquals("org.apache.felix.configadmin", headers.get(Constants.BUNDLE_SYMBOLICNAME));
    TestCase.assertEquals("1.0.0", headers.get(Constants.BUNDLE_VERSION));

    // 1. create a new Conf1 with pid1 and null location.
    // 2. Conf1#update(props) is called.
    final String pid = "test_listConfiguration";
    final Configuration config = configure(pid, null, true);

    // 3. bundleA will locationA registers ManagedServiceA with pid1.
    bundle = installBundle(pid);
    bundle.start();
    delay();

    // ==> ManagedServiceA is called back.
    final ManagedServiceTestActivator tester = ManagedServiceTestActivator.INSTANCE;
    TestCase.assertNotNull(tester);
    TestCase.assertNotNull(tester.props);
    TestCase.assertEquals(1, tester.numManagedServiceUpdatedCalls);

    // 4. bundleA is stopped but *NOT uninstalled*.
    bundle.stop();
    delay();

    // 5. test bundle calls cm.listConfigurations(null).
    final Configuration listed = getConfiguration(pid);

    // ==> Conf1 is included in the returned list and
    // it has locationA.
    // (In debug mode, dynamicBundleLocation==locationA
    // and staticBundleLocation==null)
    TestCase.assertNotNull(listed);
    TestCase.assertEquals(bundle.getLocation(), listed.getBundleLocation());

    // 6. test bundle calls cm.getConfiguration(pid1)
    final Configuration get = getConfigurationAdmin().getConfiguration(pid);
    TestCase.assertEquals(bundle.getLocation(), get.getBundleLocation());

    final Bundle cmBundle = getCmBundle();
    cmBundle.stop();
    delay();

    // 7. in-place upgrade of the configadmin bundle
    cmBundle.update(new URL(System.getProperty("new.configadmin.uri")).openStream());

    cmBundle.start();
    delay();

    headers = cmBundle.getHeaders();
    TestCase.assertEquals("org.apache.felix.configadmin", headers.get(Constants.BUNDLE_SYMBOLICNAME));
    TestCase.assertEquals("1.2.7.SNAPSHOT", headers.get(Constants.BUNDLE_VERSION));

    // 8. test bundle calls cm.listConfigurations(null).
    final Configuration listed2 = getConfiguration(pid);

    // ==> Conf1 is included in the returned list and
    // it has locationA.
    // (In debug mode, dynamicBundleLocation==locationA
    // and staticBundleLocation==null)
    TestCase.assertNotNull(listed2);
    TestCase.assertEquals(bundle.getLocation(), listed2.getBundleLocation());

    // 9. test bundle calls cm.getConfiguration(pid1)
    final Configuration get2 = getConfigurationAdmin().getConfiguration(pid);
    TestCase.assertEquals(bundle.getLocation(), get2.getBundleLocation());
  }
}
