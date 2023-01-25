package up.visulog.cli;

import java.io.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;
import up.visulog.webgen.Generator;

public class CLILauncher {
  static CommandLineParser parser = new BasicParser();
  static Options options = new Options();
  static String writeToFile;

  static {
    Option addPlugin =
        new Option(
            "a",
            "addPlugin",
            true,
            "parse argument and make an instance of PluginConfig, accept only these arguments : 'countCommits','commitsPerDay'");
    addPlugin.setArgs(Option.UNLIMITED_VALUES);
    options.addOption(addPlugin);
    options.addOption("l", "loadConfigFile", true, "load options from a file");
    options.addOption(
        "j",
        "justSaveConfigFile",
        true,
        "Save command line options to a file intead of running the analysis");
    Option pathRepo =
        new Option("r", "repositoryPath", true, "Give the path to the repository we are analyzing");
    pathRepo.setRequired(true);
    options.addOption(pathRepo);
    options.addOption("t", "templatePath", true, "Give the template path");
    options.addOption("h", "help", false, "Shows this help");
  }

  public static void main(String[] args) throws Exception {
    var config = makeConfigFromCommandLineArgs(args);
    var analyzer = new Analyzer(config);
    var results = analyzer.computeResults();

    if (writeToFile != null) {
      FileWriter fw = new FileWriter(new File(writeToFile));
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(config.toString());
      bw.close();
      return;
    }

    Generator webgen;
    Path tPath = config.getGeneratorTemplatePath();
    if (tPath != null) {
      String template = Files.readString(tPath, StandardCharsets.UTF_8);
      webgen = new Generator(template);
    } else {
      webgen = new Generator();
    }
    try {
      System.out.println(
          webgen.generateFull(results.toPluginOutputs())); // TODO configure where to output
    } catch (IOException e) {
      System.err.println("Couldn't output results");
      e.printStackTrace();
    }
  }

  static Configuration makeConfigFromCommandLineArgs(String[] args) {
    Path gitPath = null;
    Path templatePath = null;
    var plugins = new HashMap<String, PluginConfig>();

    try {
      CommandLine commandLine = parser.parse(options, args);
      if (commandLine == null) {
        displayHelpAndExit();
      }
      // The following statement checks if the argument is correct, so if you have a
      // pluginConfig, make sure to add it to the isValid()
      if (commandLine.hasOption("a")) {
        String[] optionValues = commandLine.getOptionValues("a");
        for (String s : optionValues) {
          if (!isValidPlugin(s)) {
            System.err.println("WRONG COMMAND...\nThe argument of --addPlugin isn't correct\n");
            displayHelpAndExit();
          } else {
            plugins.put(s, new PluginConfig() {});
          }
        }
      }
      if (commandLine.hasOption("l")) {
        try {
          String argsFromFile =
              new String(Files.readAllBytes(Paths.get(commandLine.getOptionValue("l"))));

          String[] optionSplit;
          ArrayList<String> fakeArgs = new ArrayList<String>();
          for (String option : argsFromFile.split("\n")) {
            optionSplit = option.split(": ");
            switch (optionSplit[0]) {
              case "gitpath":
                fakeArgs.add("-r");
                fakeArgs.add(optionSplit[1]);
                break;
              case "template": 
                if (!optionSplit[1].equals("null")) {
                  fakeArgs.add("-t");
                  fakeArgs.add(optionSplit[1]);
                }
                break;
              case "pluginlist":
                fakeArgs.add("-a");
                for (String plug : optionSplit[1].split(":")) fakeArgs.add(plug);
                break;
              default:
                System.out.println("Unknown config option met.");
                break;
            }
          }
          String[] fakeArgsUsable = new String[fakeArgs.size()];
          fakeArgsUsable = fakeArgs.toArray(fakeArgsUsable);
          return makeConfigFromCommandLineArgs(fakeArgsUsable);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (commandLine.hasOption("j")) {
        writeToFile = commandLine.getOptionValue("j");
      }
      if (commandLine.hasOption("t")) {
        templatePath = Paths.get(commandLine.getOptionValue("t"));
      }
      if (commandLine.hasOption("h")) {
        displayHelpAndExit();
      }
      if (commandLine.hasOption("r")) {
        gitPath = FileSystems.getDefault().getPath(commandLine.getOptionValue("r"));
      }
    } catch (ParseException e) {
      displayHelpAndExit();
    }
    return new Configuration(gitPath, templatePath, plugins);
  }

  private static boolean isValidPlugin(String s) {
    if (s.equals("countCommits")
        || s.equals("commitsPerDay")
        || s.equals("mergesPerAuthor")
        || s.equals("commitsPerAuthorChart")
        || s.equals("signedCommitsProportion")
        || s.equals("linesPerAuthor")) return true;
    return false;
  }
  // TODO 
  private static boolean isValidPath(String s) {
    return true;
  }

  private static void displayHelpAndExit() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("'-option argument', -r is mandatory", options);
    System.exit(0);
  }
}
