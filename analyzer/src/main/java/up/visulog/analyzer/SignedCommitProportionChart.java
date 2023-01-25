package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class SignedCommitProportionChart implements AnalyzerPlugin {
  private final Configuration configuration;
  private Result result;

  public SignedCommitProportionChart(Configuration c) {
    configuration = c;
  }

  @Override
  public void run() {
    result = new Result();
    for (Commit c : Commit.parseLogFromCommand(configuration.getGitPath())) {
      if (c.signed) {
        result.incrementSignedCommits();
      }
      result.incrementCommits();
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
    private int nbOfCommits = 0;
    private int nbOfSignedCommits = 0;

    public void incrementSignedCommits() {
      nbOfSignedCommits++;
    }

    public void incrementCommits() {
      nbOfCommits++;
    }

    public String getResultAsString() {
      return String.format("%d commits signés sur un total de %d", nbOfSignedCommits, nbOfCommits);
    }

    public String getResultAsHtmlDiv() {
      String r =
          "<div class=\"plugin\"><span class=\"plugintitle\">Proportion of signed commits</span><div id=\"signedCommitProportion\"></div>";
      r +=
          String.format(
              "<script>new Chartist.Pie(\"#signedCommitProportion\", { labels:['signed', 'unsigned'], series: [%d, %d]}, {donut: true, donutSolid: true, startAngle: 270, showLabel: true});</script>",
              nbOfSignedCommits, nbOfCommits - nbOfSignedCommits);
      r += "</div>";
      return r;
    }
  }
}
