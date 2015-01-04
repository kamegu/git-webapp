package gw.dto.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import gw.model.pk.RepositoryPK;

@AllArgsConstructor
@Getter
public class PullRequestEndpoint {
  private String accountName;
  private String repositoryName;
  private String branchName;

  public PullRequestEndpoint(RepositoryPK repositoryPK, String branchName) {
    this(repositoryPK.getAccountName(), repositoryPK.getRepositoryName(), branchName);
  }

  @JsonIgnore
  public RepositoryPK getRepositoryPK() {
    return new RepositoryPK(accountName, repositoryName);
  }
}
