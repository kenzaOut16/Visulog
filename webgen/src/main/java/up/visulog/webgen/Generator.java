package up.visulog.webgen;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A class that generates full HTML pages from plugins output and given mustache templates.
 *
 * @author Olivier Moreau
 */
public class Generator {
  String template;

  /** The default CTOR, with default templates. */
  public Generator() {
    template =
        "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/chartist.js/latest/chartist.min.css\"><script src=\"https://cdn.jsdelivr.net/chartist.js/latest/chartist.min.js\"></script><title>VISULOG</title><!--Inline CSS ftw--><style>body {--style-color: #007799;margin: auto;max-width:80%;}.titlebar {width:100%;display: grid;place-items: center;}.titlebar h1 {text-decoration: underline overline;text-decoration-color: var(--style-color);}.plugins {display: flex;flex-wrap: wrap;}.plugins div.plugin{width: auto;flex-grow: 1;margin: .5%;padding: 2.5%;border-style: solid;border-color: var(--style-color);border-width: .1%;}</style></head><body><span class=\"titlebar\"><h1>VISULOG</h1></span><div class=\"plugins\">:PLUGINS:</div></body></html>";
  }

  /**
   * The CTOR with the Template as an argument, read from configuration as a String.
   *
   * @param tmpl The template to be used.
   */
  public Generator(String tmpl) {
    this.template = tmpl;
  }

  /**
   * The function used to generate a full HTML page from templates. ':PLUGINS:' will be replaced by
   * PluginsOutput put end to end.
   *
   * @param plugins A list of PluginOutput to be displayed into the page.
   * @return String containing the full webpage generated from templates.
   * @throws IOException if the template fails to execute.
   */
  public String generateFull(ArrayList<PluginOutput> plugins) throws IOException {
    String pluginsAsHTML = "";
    for (PluginOutput p : plugins) {
      pluginsAsHTML += p.toString();
    }

    String res = template.replaceAll(":PLUGINS:", pluginsAsHTML);
    return res;
  }
}
