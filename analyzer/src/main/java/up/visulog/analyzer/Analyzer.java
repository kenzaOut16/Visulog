package up.visulog.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

public class Analyzer {

  private final Configuration config;
  private AnalyzerResult result;

  public Analyzer(Configuration config) {
    this.config = config;
  }
  // counts the results
  public AnalyzerResult computeResults() {
    List<AnalyzerPlugin> plugins = new ArrayList<>(); // liste
    for (var pluginConfigEntry : config.getPluginConfigs().entrySet()) {
      var pluginName = pluginConfigEntry.getKey();
      var pluginConfig = pluginConfigEntry.getValue();
      var plugin = makePlugin(pluginName, pluginConfig); // we create a new plugin
      plugin.ifPresent(plugins::add); // we add it to the list
    }

    plugins.parallelStream().forEach(analyzerPlugin -> analyzerPlugin.run());

    // store the results together in an AnalyzerResult instance and return it
    return new AnalyzerResult(
        plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
  }

  private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
    switch (pluginName) {
      case "commitsPerAuthorChart":
        return Optional.of(new CountCommitsPerAuthorChart(config));
      case "countCommits":
        return Optional.of(new CountCommitsPerAuthorPlugin(config));
      case "commitsPerDay":
        return Optional.of(new CountCommitsPerDayPlugin(config));
      case "mergesPerAuthor":
        return Optional.of(new CountMergesPerAuthor(config));
      case "signedCommitsProportion":
        return Optional.of(new SignedCommitProportionChart(config));
      case "linesPerAuthor":
        return Optional.of(new LinesPerAuthorPlugin(config));
      default:
        return Optional.empty();
    }
  }
}
