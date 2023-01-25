package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountCommitsPerAuthorPlugin implements AnalyzerPlugin {
  private final Configuration configuration;
  private Result result;

  public CountCommitsPerAuthorPlugin(Configuration generalConfiguration) {
    this.configuration = generalConfiguration;
    // ajouter a mymap un plugin de type countcommits par author

    // this.myMap.put("countcommits", this);
  }

  static Result processLog(List<Commit> gitLog) {
    var result = new Result();
    for (var commit : gitLog) {
      var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
      result.commitsPerAuthor.put(commit.author, nb + 1);
    }
    return result;
  }

  @Override
  public void run() {
    result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
  }

  @Override
  public Result getResult() {
    if (result == null) run();
    return result;
  }

  static class Result implements AnalyzerPlugin.Result {
    private final Map<String, Integer> commitsPerAuthor = new HashMap<>();

    Map<String, Integer> getCommitsPerAuthor() {
      return commitsPerAuthor;
    }

    @Override
    public String getResultAsString() {
      return commitsPerAuthor.toString();
    }

    @Override
    public String getResultAsHtmlDiv() {
      StringBuilder html =
          new StringBuilder(
              "<div class=\"plugin\"><span class=\"plugintitle\">Commits per author</span><ul>");
      for (var item : commitsPerAuthor.entrySet()) {
        html.append("<li>")
            .append(item.getKey())
            .append(": ")
            .append(item.getValue())
            .append("</li>");
      }
      html.append("</ul></div>");
      return html.toString();
    }
  }
}
