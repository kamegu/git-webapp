package gw.service;

import gw.model.pk.RepositoryPK;

import java.util.List;

import javax.inject.Inject;

public class RepositoryUpdateService {
  @Inject private PullRequestService pullRequestService;

  public void onPost(RepositoryPK repositoryPK, List<String> branchNames) {
    pullRequestService.updateFromPush(repositoryPK, branchNames);
  }
}
