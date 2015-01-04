package gw.dto.repository;

import gw.model.PullRequest;
import gw.model.pk.IssuePK;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PullRequestJson {
  private IssuePK issuePK;
  private PullRequestEndpoint base;
  private PullRequestEndpoint request;
  private String commitId;
  private String baseCommitId;
  private String requestCommitId;

  public PullRequestJson(PullRequest pullRequest) {
    this.issuePK = pullRequest.getPk();
    this.base = new PullRequestEndpoint(issuePK.getRepositoryPK(), pullRequest.getBaseBranchName());
    this.request = new PullRequestEndpoint(pullRequest.getRequestRepositoryPK(), pullRequest.getRequestBranchName());
    this.commitId = pullRequest.getCommitId();
    this.baseCommitId = pullRequest.getBaseCommitId();
    this.requestCommitId = pullRequest.getRequestCommitId();
  }

  public String getMergeMessage() {
    return PullRequest.getMergeMessage(issuePK, request);
  }
}
