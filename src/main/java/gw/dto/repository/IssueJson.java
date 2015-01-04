package gw.dto.repository;

import gw.model.Issue;

import java.sql.Timestamp;
import java.util.List;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class IssueJson {
  private long issueId;
  private boolean pull;
  private String title;
  private boolean closed;
  private String openedBy;
  private String assignedTo;
  private List<LabelJson> labels;
  private Timestamp registeredTs;
  private Timestamp updatedTs;
  private PullRequestJson pullRequest;

  public static IssueJson newInstance(Issue issue) {
    List<LabelJson> labels = Lists.transform(issue.getIssueLabels(), l -> new LabelJson(l.getLabel()));
    return new IssueJson(issue.getPk().getIssueId(), issue.getPullRequest() != null, issue.getTitle(), issue.isClosed(),
        issue.getOpenedAccountName(), issue.getAssignedAccountName(), labels,
        issue.getUpdateInfo().getRegisteredTimestamp(), issue.getUpdateInfo().getUpdatedTimestamp(), null);
  }

  public static IssueJson newInstance(Issue issue, boolean withPullRequest) {
    IssueJson issueJson = newInstance(issue);
    if (withPullRequest && issue.getPullRequest() != null) {
      issueJson.pullRequest = new PullRequestJson(issue.getPullRequest());
    }
    return issueJson;
  }
}
