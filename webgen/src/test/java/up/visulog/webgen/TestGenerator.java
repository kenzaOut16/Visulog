package up.visulog.webgen;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

public class TestGenerator {
  PluginOutput a;

  ArrayList<PluginOutput> pluginOutputList;

  @Before
  public void setUp() {
    a = new PluginOutput("zbeul");

    pluginOutputList = new ArrayList<PluginOutput>();
    pluginOutputList.add(a);
  }

  @Test
  public void testDefaultPageGenerationEmpty() throws IOException {
    String expected =
        "<!DOCTYPE html><html><head><link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/chartist.js/latest/chartist.min.css\"><script src=\"https://cdn.jsdelivr.net/chartist.js/latest/chartist.min.js\"></script><title>VISULOG</title><!--Inline CSS ftw--><style>body {--style-color: #007799;margin: auto;max-width:80%;}.titlebar {width:100%;display: grid;place-items: center;}.titlebar h1 {text-decoration: underline overline;text-decoration-color: var(--style-color);}.plugins {display: flex;flex-wrap: wrap;}.plugins div.plugin{width: auto;flex-grow: 1;margin: .5%;padding: 2.5%;border-style: solid;border-color: var(--style-color);border-width: .1%;}</style></head><body><span class=\"titlebar\"><h1>VISULOG</h1></span><div class=\"plugins\"></div></body></html>";
    Generator g = new Generator();
    ArrayList<PluginOutput> pluginOutputListEmpty = new ArrayList<PluginOutput>();
    assertEquals(expected, g.generateFull(pluginOutputListEmpty));
  }

  @Test
  public void testGeneratorWithCustomTemplate() throws IOException {
    String expected = "AAA\nzbeul\n";

    Generator g = new Generator("AAA\n:PLUGINS:\n");
    assertEquals(expected, g.generateFull(pluginOutputList));
  }
}
