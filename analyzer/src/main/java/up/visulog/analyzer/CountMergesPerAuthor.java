package up.visulog.analyzer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountMergesPerAuthor implements AnalyzerPlugin {
  private final Configuration configuration;
  private Result result;

  public CountMergesPerAuthor(Configuration generalConfiguration) {
    this.configuration = generalConfiguration;
  }

  static Result processLog(List<Commit> gitLog) {
    var result = new Result();
    for (Commit commit : gitLog) {
      if (commit.mergedFrom != null) {
        result.addToAuthor(commit.author);
      }
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
    private final Map<String, Integer> mergedPerAuther = new HashMap<>();

    public void addToAuthor(String author) {
      if (mergedPerAuther.containsKey(author)) {
        mergedPerAuther.put(author, mergedPerAuther.get(author) + 1);
      } else {
        mergedPerAuther.put(author, 1);
      }
    }

    @Override
    public String getResultAsString() {
      System.out.println("Merged per auther:");
      Set listKeys = mergedPerAuther.keySet();
      Iterator iterateur = listKeys.iterator();

      while (iterateur.hasNext()) {
        Object key = iterateur.next();
        System.out.println(key + "->" + mergedPerAuther.get(key));
      }
      return "";
    }

    @Override
    public String getResultAsHtmlDiv() {
      StringBuilder html =
          new StringBuilder(
              "<div class=\"plugin\"><span class=\"plugintitle\">Merges per author</span><ul>");
      for (var item : mergedPerAuther.entrySet()) {
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
