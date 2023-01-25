package up.visulog.analyzer;

import java.util.HashMap;
import java.util.List;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountCommitsPerAuthorChart implements AnalyzerPlugin {
  private Configuration configuration;
  private static Result result;

  public CountCommitsPerAuthorChart(Configuration c) {
    configuration = c;
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

  static Result processLog(List<Commit> gitLog) {
    result = new Result();
    for (Commit c : gitLog) {
      result.addCommitToAuthor(c.author);
    }
    return result;
  }

  static class Result implements AnalyzerPlugin.Result {
    HashMap<String, Integer> content =
        new HashMap<String, Integer>(); // "Author name" -> number of commit

    public void addCommitToAuthor(String name) {
      if (content.containsKey(name)) {
        content.put(name, content.get(name) + 1);
      } else {
        content.put(name, 1);
      }
    }

    @Override
    public String getResultAsString() {
      return "JS charts in text?";
    }

    /*
     * new Chartist.Bar('.ct-chart', {
     * 	labels: ['XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL'],
     * 	series: [20, 60, 120, 200, 180, 20, 10]
     * 	}, {
     * 	distributeSeries: true
     * 	});
     */

    @Override
    public String getResultAsHtmlDiv() {
      String labArray = "['";
      for (String a : content.keySet()) {
        labArray += String.format("%s', '", a);
      }
      labArray = labArray.substring(0, labArray.length() - 3) + "]";
      String valArray = content.values().toString();

      String output =
          "<div class=\"plugin\"><span class=\"plugintitle\">Commits per Author</span><div class=\"ct-chart\" id=\"commitsPerAuthor\"></div>";
      output +=
          String.format(
              "<script>new Chartist.Bar('#commitsPerAuthor', { labels: %s, series: [%s] }, { distributedSeries: true });</script>",
              labArray, valArray);
      output += "</div>";
      return output;
    }
  }
}
