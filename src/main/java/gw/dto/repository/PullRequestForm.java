package gw.dto.repository;

import gw.model.PullRequest;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter @Setter
public class PullRequestForm {
  @NotEmpty
  private String title;
  private String content;
  private String baseBranch;
  private String requestUserName;
  private String requestRepoName;
  private String requestBranch;

  public void applyTo(PullRequest pullRequest) {
    pullRequest.setBaseBranchName(baseBranch);
    pullRequest.setRequestAccountName(requestUserName);
    pullRequest.setRequestRepositoryName(requestRepoName);
    pullRequest.setRequestBranchName(requestBranch);
  }
}
