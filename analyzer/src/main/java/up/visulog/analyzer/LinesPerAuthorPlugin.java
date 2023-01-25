package up.visulog.analyzer;

import java.util.HashMap;
import java.util.Map;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class LinesPerAuthorPlugin implements AnalyzerPlugin {
  private final Configuration configuration;
  private Result result;

  public LinesPerAuthorPlugin(Configuration c) {
    configuration = c;
  }

  @Override
  public void run() {
    result = new Result();
    for (Commit c : Commit.parseLogFromCommand(configuration.getGitPath())) {
      result.addToAuthor(c.author, c.linesAdded, c.linesRemoved);
    }
  }

  @Override
  public Result getResult() {
    if (result == null) {
      run();
    }
    return result;
  }

  static class Result implements AnalyzerPlugin.Result {
    HashMap<String, int[]> values =
        new HashMap<String, int[]>(); // <Author name : {linesAdded,linesRemoved}>

    public void addToAuthor(String a, int p, int r) {
      if (values.containsKey(a)) {
        int[] x = values.get(a);
        x[0] += p;
        x[1] += r;
      } else {
        int[] x = {p, r};
        values.put(a, x);
      }
    }

    public String getResultAsString() {
      String r = "";
      for (Map.Entry<String, int[]> entry : values.entrySet()) {
        r += entry.getKey() + " : " + entry.getValue()[0] + "+, " + entry.getValue()[1] + "-;";
      }
      return r;
    }

    /*
     * new Chartist.Bar('#linesPerAuthor', {
     *     labels: ['Olivier', 'Maël', 'Kenza'],
     *     series: [
     *       [200, 100, 50],
     *       [-50, -100, -200],
     *     ]
     *   }, {
     *     seriesBarDistance: 0
     *   });
     */

    public String getResultAsHtmlDiv() {
      String authorLabel = "";
      String addedLinesSeries = "";
      String removedLinesSeries = "";
      for (Map.Entry<String, int[]> entry : values.entrySet()) {
        authorLabel += String.format("'%s', ", entry.getKey());
        addedLinesSeries += String.format("%d, ", entry.getValue()[0]);
        removedLinesSeries += String.format("-%d, ", entry.getValue()[1]);
      }
      authorLabel =
          "["
              + authorLabel.substring(0, authorLabel.length() - 1)
              + "]"; // on supprime la dernière virgule
      addedLinesSeries = "[" + addedLinesSeries.substring(0, addedLinesSeries.length() - 1) + "]";
      removedLinesSeries =
          "[" + removedLinesSeries.substring(0, removedLinesSeries.length() - 1) + "]";

      String r =
          "<div class=\"plugin\"><span class=\"plugintitle\">Lines per Author</span><div class=\"ct-chart\" id=\"linesPerAuthor\"></div></div>";
      r +=
          String.format(
              "<script>new Chartist.Bar('#linesPerAuthor', {labels:%s,series:[%s,%s]},{seriesBarDistance: 0});</script>",
              authorLabel, addedLinesSeries, removedLinesSeries);
      return r;
    }
  }
}
