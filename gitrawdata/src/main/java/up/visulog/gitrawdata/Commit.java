package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Commit {
  // FIXME: (some of) these fields could have more specialized types than String
  public final String id;
  public final String date;
  public final String author;
  public final String email;
  public final String description;
  public final String mergedFrom;
  public final int linesAdded;
  public final int linesRemoved;
  public final boolean signed;

  public Commit(
      String id,
      String author,
      String date,
      String description,
      String mergedFrom,
      int linesRemoved,
      int linesAdded,
      boolean signed) {
    this.id = id;
    this.author = author.substring(0, author.indexOf("<") - 1);
    this.email = author.substring(author.indexOf("<") + 1, author.lastIndexOf(">"));
    this.date = date;
    this.description = description;
    this.mergedFrom = mergedFrom;
    this.linesRemoved = linesRemoved;
    this.linesAdded = linesAdded;
    this.signed = signed;
  }

  // TODO: factor this out (similar code will have to be used for all git commands)
  public static List<Commit> parseLogFromCommand(Path gitPath) {
    ProcessBuilder builder =
        new ProcessBuilder(
                "git", "log", "--show-signature", "--date=format:'%Y-%m-%d'", "--shortstat")
            .directory(gitPath.toFile());
    Process process;
    try {
      process = builder.start();
    } catch (IOException e) {
      throw new RuntimeException("Error running \"git log\".", e);
    }
    InputStream is = process.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    return parseLog(reader);
  }

  public static List<Commit> parseLog(BufferedReader reader) {
    var result = new ArrayList<Commit>();
    Optional<Commit> commit = parseCommit(reader);
    while (commit.isPresent()) {
      result.add(commit.get());
      commit = parseCommit(reader);
    }
    return result;
  }

  /**
   * Parses a log item and outputs a commit object. Exceptions will be thrown in case the input does
   * not have the proper format. Returns an empty optional if there is nothing to parse anymore.
   */
  public static Optional<Commit> parseCommit(BufferedReader input) {
    try {
      var line = input.readLine();
      if (line == null)
        return Optional.empty(); // if no line can be read, we are done reading the buffer
      var idChunks = line.split(" ");
      if (!idChunks[0].equals("commit")) parseError();
      var builder = new CommitBuilder(idChunks[1]);

      line = input.readLine();
      while (!line.isEmpty()) {
        var colonPos = line.indexOf(":");
        var fieldName = line.substring(0, colonPos);
        var fieldContent = line.substring(colonPos + 1).trim();
        switch (fieldName) {
          case "Author":
            builder.setAuthor(fieldContent);
            break;
          case "Merge":
            builder.setMergedFrom(fieldContent);
            break;
          case "Date":
            builder.setDate(fieldContent);
            break;
          case "gpg": // If GPG appears, we have a signature
            builder.setSignedTrue();
            break;
          case "Primary key fingerprint": // TODO in case we want to save the key ?
            break;
          default:
            System.err.format("Couldn't parse unknown field %s:%s", fieldName, fieldContent);
        }
        line = input.readLine(); // prepare next iteration
        if (line == null)
          parseError(); // end of stream is not supposed to happen now (commit data incomplete)
      }
      // now read the commit message per se
      var description =
          input
              .lines() // get a stream of lines to work with
              .takeWhile(
                  currentLine ->
                      !currentLine
                          .isEmpty()) // take all lines until the first empty one (commits are
              // separated by empty lines). Remark: commit messages are
              // indented with spaces, so any blank line in the message
              // contains at least a couple of spaces.
              .map(String::trim) // remove indentation
              .reduce(
                  "",
                  (accumulator, currentLine) ->
                      accumulator + currentLine); // concatenate everything
      builder.setDescription(description);

      // Let's read the number of line.
      // FIXME: This will crash in case of an empty commit, however, if you have empty commits in
      // your repo, your repo is the problem.
      // Read this https://stackoverflow.com/questions/28313664/remove-empty-commits-in-git
      if (!builder.isMergeCommit) {
        String[] lineChunks = input.readLine().split(",");
        for (String stat : lineChunks) {
          if (stat.contains("+"))
            builder.setLinesAdded(Integer.parseInt(stat.trim().split(" ")[0]));
          if (stat.contains("-")) {
            builder.setLinesRemoved(Integer.parseInt(stat.trim().split(" ")[0]));
          }
        }
        input.readLine(); // reading an empty line to clear stuff
      }

      return Optional.of(builder.createCommit());
    } catch (IOException e) {
      parseError();
    }
    return Optional
        .empty(); // this is supposed to be unreachable, as parseError should never return
  }

  // Helper function for generating parsing exceptions. This function *always* quits on an
  // exception. It *never* returns.
  private static void parseError() {
    throw new RuntimeException("Wrong commit format.");
  }

  @Override
  public String toString() {
    return "Commit{"
        + "id='"
        + id
        + '\''
        + (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "")
        + // TODO: find out if this is the only optional field
        ", date='"
        + date
        + '\''
        + ", author='"
        + author
        + '\''
        + ", description='"
        + description
        + '\''
        + '}';
  }
  // git dif --numstat "@{1day ago}" show the number of lines inserted and removed
  public static int diffFromCommand(Path gitPath) {
    ProcessBuilder builder =
        new ProcessBuilder("git", "diff", "--numstat").directory(gitPath.toFile());
    Process process;
    try {
      process = builder.start();
    } catch (IOException e) {
      throw new RuntimeException("Error running \"git diff --numstat \".", e);
    }
    InputStream d = process.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(d));
    return lineTotal(reader);
  }

  public static int lineTotal(BufferedReader reader) {
    Scanner input = new Scanner(reader);
    input.useDelimiter("\n");
    int sum = 0;
    while (input.hasNext()) {
      String line = input.next();
      sum = sum + recupLines(line);
    }
    return sum;
  }

  public static int recupLines(String line) {
    Scanner s = new Scanner(line);
    String st = s.next();
    int a = Integer.parseInt(st);
    return a;
  }
}
