package up.visulog.gitrawdata;

public class CommitBuilder {
  private final String id;
  private String author;
  private String date;
  private String description;
  private String mergedFrom;
  public boolean isMergeCommit;
  private int linesAdded;
  private int linesRemoved;
  private boolean signed;

  public CommitBuilder(String id) {
    this.id = id;
    isMergeCommit = false;
    signed = false;
  }

  public CommitBuilder setLinesAdded(int a) {
    this.linesAdded = a;
    return this;
  }

  public CommitBuilder setLinesRemoved(int r) {
    this.linesRemoved = r;
    return this;
  }

  public CommitBuilder setAuthor(String author) {
    this.author = author;
    return this;
  }

  public CommitBuilder setDate(String date) {
    this.date = date;
    return this;
  }

  public CommitBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public CommitBuilder setMergedFrom(String mergedFrom) {
    this.mergedFrom = mergedFrom;
    isMergeCommit = true;
    return this;
  }

  public CommitBuilder setSignedTrue() {
    signed = true;
    return this;
  }

  public Commit createCommit() {
    return new Commit(id, author, date, description, mergedFrom, linesRemoved, linesAdded, signed);
  }
}
