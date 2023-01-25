package up.visulog.cli;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestCLILauncher {
  /*
  TODO: one can also add integration tests here:
  - run the whole program with some valid options and look whether the output has a valid format
  - run the whole program with bad command and see whether something that looks like help is printed
   */
  @Test
  public void testArgumentParser() {
    // Short option
    var config1 = CLILauncher.makeConfigFromCommandLineArgs(new String[] {"-a", "countCommits"});
    assertTrue(config1 != null);
    // Long option
    var config3 =
        CLILauncher.makeConfigFromCommandLineArgs(new String[] {"--addPlugin", "commitsPerDay"});
    assertTrue(config3 != null);

    // Wrong option
    var config2 = CLILauncher.makeConfigFromCommandLineArgs(new String[] {"--nonExistingOption"});
    assertTrue(config2 != null);
  }
}
