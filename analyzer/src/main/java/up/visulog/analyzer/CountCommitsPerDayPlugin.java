package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountCommitsPerDayPlugin implements AnalyzerPlugin {
  private final Configuration configuration;
  private Result result;

  public CountCommitsPerDayPlugin(Configuration generalConfiguration) {
    this.configuration = generalConfiguration;
    // ajouter a mymap un plugin de type countcommits par jour
    //  this.myMap.put("commitsPerDay", this);
  }

  static Result processLog(List<Commit> gitLog) {
    var result = new Result();
    for (var commit : gitLog) {
      var nb = result.commitsPerDay.getOrDefault(commit.date, 0);
      result.commitsPerDay.put(commit.date, nb + 1);
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
    private final Map<String, Integer> commitsPerDay = new HashMap<>();

    Map<String, Integer> getcommitsPerDay() {
      return commitsPerDay;
    }

    @Override
    public String getResultAsString() {
      return commitsPerDay.toString();
    }

    @Override
    public String getResultAsHtmlDiv() {
      StringBuilder html =
          new StringBuilder(
              "<div class=\"plugin\"><span class=\"plugintitle\">Commits per Day</span><ul>");
      for (var item : commitsPerDay.entrySet()) {
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
