package gw.model;

import gw.dto.repository.PullRequestEndpoint;
import gw.model.embed.UpdateInfo;
import gw.model.pk.IssuePK;
import gw.model.pk.RepositoryPK;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PULL_REQUEST")
@Getter @Setter
@ToString(of = { "pk" })
public class PullRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private IssuePK pk;

  @NotNull
  @Column(name = "BASE_BRANCH_NAME")
  @Size(max = 100)
  private String baseBranchName;

  @NotNull
  @Column(name = "REQUEST_ACCOUNT_NAME")
  @Size(max = 100)
  private String requestAccountName;
  @NotNull
  @Column(name = "REQUEST_REPOSITORY_NAME")
  @Size(max = 100)
  private String requestRepositoryName;
  @NotNull
  @Column(name = "REQUEST_BRANCH_NAME")
  @Size(max = 100)
  private String requestBranchName;

  @NotNull
  @Column(name = "COMMIT_ID")
  @Size(min = 40, max = 40)
  private String commitId;
  @NotNull
  @Column(name = "BASE_COMMIT_ID")
  @Size(min = 40, max = 40)
  private String baseCommitId; // merge-base
  @NotNull
  @Column(name = "REQUEST_COMMIT_ID")
  @Size(min = 40, max = 40)
  private String requestCommitId;

  @Embedded
  private UpdateInfo updateInfo;

  public RepositoryPK getRequestRepositoryPK() {
    return new RepositoryPK(requestAccountName, requestRepositoryName);
  }

  public static String getMergeMessage(IssuePK issuePK, PullRequestEndpoint requestEndpoint) {
    return "Merge pull request #" + issuePK.getIssueId() + " from "
        + requestEndpoint.getRepositoryName() + "/" + requestEndpoint.getBranchName();
  }

  @OneToOne
  @JoinColumns({
      @JoinColumn(name = "ACCOUNT_NAME", referencedColumnName = "ACCOUNT_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "REPOSITORY_NAME", referencedColumnName = "REPOSITORY_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "ISSUE_ID", referencedColumnName = "ISSUE_ID", updatable = false, insertable = false)
  })
  private Issue issue;
}
