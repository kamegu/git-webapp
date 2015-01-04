package gw.service;

import gw.core.LoginContext;
import gw.core.action.Result;
import gw.dto.repository.CommentForm;
import gw.dto.repository.PullRequestForm;
import gw.git.GitOperation;
import gw.model.Issue;
import gw.model.PullRequest;
import gw.model.Repository;
import gw.model.pk.IssuePK;
import gw.model.pk.RepositoryPK;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jgit.lib.ObjectId;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class PullRequestService {
  @Inject private Provider<EntityManager> emProvider;
  @Inject private Provider<HttpServletRequest> reqProvider;
  @Inject private IssueService issueService;
  @Inject private GitOperation gitOperation;

  @Transactional
  public Issue create(RepositoryPK repositoryPK, PullRequestForm form) {
    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    Issue issue = issueService.createIssueAndComment(repository, form.getTitle(), form.getContent(), null);

    PullRequest pullRequest = new PullRequest();
    pullRequest.setPk(issue.getPk());
    form.applyTo(pullRequest);
    pullRequest.setCommitId(ObjectId.zeroId().name());
    pullRequest.setBaseCommitId(ObjectId.zeroId().name());
    pullRequest.setRequestCommitId(ObjectId.zeroId().name());
    emProvider.get().persist(pullRequest);

    RepositoryPK reqRepoPK = new RepositoryPK(form.getRequestUserName(), form.getRequestRepoName());
    gitOperation.updatePullRequestRefSpec(issue.getPk(), reqRepoPK, form.getRequestBranch());

    updateCommitIds(pullRequest);

    return issue;
  }

  @Transactional
  public void updateFromPush(RepositoryPK repositoryPK, List<String> branchNames) {
    List<PullRequest> pullRequests = branchNames.stream()
        .flatMap(branchName -> {
          return emProvider.get()
          .createQuery(
              "SELECT pr FROM PullRequest pr INNER JOIN FETCH pr.issue i"
              + " WHERE i.closed = false AND "
              + " ((pr.requestAccountName = :accountName AND pr.requestRepositoryName = :reposName AND pr.requestBranchName = :branchName)"
              + " OR (pr.pk.accountName = :accountName AND pr.pk.repositoryName = :reposName AND pr.baseBranchName = :branchName))",
              PullRequest.class)
          .setParameter("accountName", repositoryPK.getAccountName())
          .setParameter("reposName", repositoryPK.getRepositoryName())
          .setParameter("branchName", branchName)
          .getResultList().stream();
        })
        .collect(Collectors.groupingBy(PullRequest::getPk))
        .values()
        .stream()
        .map(list -> list.get(0))
        .collect(Collectors.toList());

    pullRequests.forEach(this::updateFromPush);
  }

  @Transactional
  public Result<Issue> merge(IssuePK issuePK, CommentForm form) {
    LoginContext loginContext = LoginContext.get(reqProvider.get());
    PullRequest pullRequest = emProvider.get().find(PullRequest.class, issuePK);
    
    boolean merged = gitOperation.merge(pullRequest.getBaseBranchName(), issuePK, form.getContent(), loginContext);

    if (merged) {
      issueService.addMergeComment(issuePK, form);

      emProvider.get().flush();
      emProvider.get().clear();
      return Result.success(emProvider.get().find(Issue.class, issuePK));
    }

    return Result.error("");
  }

  private Result<PullRequest> updateFromPush(PullRequest pullRequest) {
    gitOperation.updatePullRequestRefSpec(pullRequest.getPk(), pullRequest.getRequestRepositoryPK(), pullRequest.getRequestBranchName());

    updateCommitIds(pullRequest);
    return Result.error("");
  }

  private Result<PullRequest> updateCommitIds(PullRequest pullRequest) {
    ObjectId objectId = gitOperation.getCommitId(pullRequest.getPk().getRepositoryPK(), pullRequest.getBaseBranchName());
    ObjectId mergeBaseObjectId = gitOperation.getPullRequestBaseObjectId(pullRequest.getPk(), pullRequest.getBaseBranchName());
    ObjectId reqObjectId = gitOperation.getCommitId(pullRequest.getRequestRepositoryPK(), pullRequest.getRequestBranchName());
    pullRequest.setCommitId(objectId.getName());
    pullRequest.setBaseCommitId(mergeBaseObjectId.getName());
    pullRequest.setRequestCommitId(reqObjectId.getName());
    emProvider.get().merge(pullRequest);
    return Result.success(pullRequest);
  }
}
