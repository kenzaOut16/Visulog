package up.visulog.analyzer;

import java.util.ArrayList; 
import java.util.List;
import up.visulog.webgen.PluginOutput;

public class AnalyzerResult {
  private final List<AnalyzerPlugin.Result> subResults;

  public List<AnalyzerPlugin.Result> getSubResults() {
    return subResults;
  }

  public AnalyzerResult(List<AnalyzerPlugin.Result> subResults) {
    this.subResults = subResults;
  }

  @Override
  public String toString() {
    return subResults.stream()
        .map(AnalyzerPlugin.Result::getResultAsString)
        .reduce("", (acc, cur) -> acc + "\n" + cur);
  }

  public String toHTML() {
    return "<html><body>"
        + subResults.stream()
            .map(AnalyzerPlugin.Result::getResultAsHtmlDiv)
            .reduce("", (acc, cur) -> acc + cur)
        + "</body></html>";
  }

  /** Returns a List of PluginOutputs, to be used by a webgen.Generator */
  public ArrayList<PluginOutput> toPluginOutputs() {
    var res = new ArrayList<PluginOutput>();
    for (var s : subResults) {
      res.add(new PluginOutput(s.getResultAsHtmlDiv()));
    }
    return res;
  }
}
