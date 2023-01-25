package up.visulog.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Configuration {

  private final Path gitPath;
  private final Map<String, PluginConfig> plugins;
  /** The path to the generator template, this is an optionnal argument. */
  private final Path generatorTemplatePath;

  public Configuration(
      Path gitPath, Path generatorTemplatePath, Map<String, PluginConfig> plugins) {

    this.gitPath = Paths.get(System.getProperty("user.dir"));

    this.generatorTemplatePath = generatorTemplatePath;
    this.plugins = Map.copyOf(plugins);
  }

  public Path getGitPath() {
    return gitPath;
  }

  public Map<String, PluginConfig> getPluginConfigs() {
    return plugins;
  }

  public Path getGeneratorTemplatePath() {
    return generatorTemplatePath;
  }

  /*
    gitpath: .
    template: null
  <<<<<<< HEAD
    pluginlist: countCommitsPerAuthor countCommitsPerDay
  =======
    pluginlist:
      - countCommitsPerAuthor
      - countCommitsPerDay
  >>>>>>> 450ebfdf7745e1cf1288c46a5dd986905875fc41
    */

  public String toString() {
    String r =
        String.format("gitpath: %s\ntemplate: %s\npluginlist: ", gitPath, generatorTemplatePath);
    for (Map.Entry<String, PluginConfig> p : plugins.entrySet()) {
      r += String.format("%s:", p.getKey());
    }
    r += "\n";
    return r;
  }
}
