package up.visulog.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import up.visulog.gitrawdata.Commit;

public class TestCountCommitsPerDayPlugin {
  private ArrayList<Commit> dummyLog;

  /* Here, we are building a dummy gitLog so to say, that we know exactly, so we can predict the function
   * we are testing's output.
   * Notice the @Before tag, it means that this will be executed before method marked @Test, even
   * though tests are run in parallel.
   * The point of having a set up function is that we could reuse the same data in different tests without copy pasting.
   */
  @Before
  public void setUpDummyLog() {
    dummyLog = new ArrayList<Commit>();

    // Commit constructor is Commit(String id, String author, String date, String description,
    // String mergedFrom)

    // Let's create a bunch of fake commits
    // a and b have the same date, willingly.
    Commit a = new Commit("aa", "author1 <mail@mail>", "27/01", "a dummy commit", "", 0, false);
    dummyLog.add(a);

    Commit b =
        new Commit("bb", "author2 <mail2@mail>", "27/01", "another dummy commit", "", 0, false);
    dummyLog.add(b);

    Commit c =
        new Commit("cc", "author3 <mail3@mail>", "30/02", "yet another dummy commit", "", 0, false);
    dummyLog.add(c);
  }

  /* This is a test. What it does is quite simple:
   * We define what the output should look like, generate it using the plugin, and compare the two.
   * This is a pretty simple test, assert on two String objects.
   */
  @Test
  public void TestGetResultAsHtmlDiv() {
    String expected =
        "<div class=\"plugin\"><span class=\"plugintitle\">Commits per Day</span><ul><li>27/01: 2</li><li>30/02: 1</li></ul></div>";
    /*
     * Let's take a better look at that string, here is is beautified:
     * <div>Commit per Day:  //This message is outputed by the plugin no matter what.
     *  <ul>                 //ul means it's an _u_nordered _l_ist, li means _l_ist _i_tem
     *   <li>27/01: 2</li>   //This is the number of commit at date 27/01, so a and b in the dummyLog
     *   <li>30/02: 1</li>   //Can you guess what this line is ?
     *  </ul>                // Closing the list and div
     * </div>
     */

    // Now, let's get the output of the plugin: We process the log with the plugin and get a Result
    // object.
    CountCommitsPerDayPlugin.Result testResult = CountCommitsPerDayPlugin.processLog(dummyLog);

    // Now, we generate HTML from the result, and store it as a String.
    String res = testResult.getResultAsHtmlDiv();

    // And finally, we verify that we got what we expected, and signal it if we don't.
    assertEquals(res, expected);
  }
}
