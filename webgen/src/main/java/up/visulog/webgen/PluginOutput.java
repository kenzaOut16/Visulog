package up.visulog.webgen;

/**
 * Class representing the output of a plugin that is to be integrated into a webpage.
 *
 * @author Olivier Moreau
 */
public class PluginOutput {
  // TODO We could use some more precise/specific attributes.
  public String content;

  public PluginOutput(String c) {
    content = c;
  }

  public String toString() {
    return content;
  }
}
