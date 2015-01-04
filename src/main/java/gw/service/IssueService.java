package gw.service;

import gw.core.LoginContext;
import gw.core.action.Result;
import gw.dto.repository.CommentForm;
import gw.dto.repository.IssueForm;
import gw.dto.repository.IssueSearch;
import gw.model.Issue;
import gw.model.IssueComment;
import gw.model.IssueLabel;
import gw.model.Label;
import gw.model.Repository;
import gw.model.pk.IssueLabelPK;
import gw.model.pk.IssuePK;
import gw.model.pk.LabelPK;
import gw.model.pk.RepositoryPK;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import jersey.repackaged.com.google.common.collect.Lists;

import org.apache.commons.lang3.BooleanUtils;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class IssueService {
  @Inject private Provider<EntityManager> emProvider;
  @Inject private Provider<HttpServletRequest> reqProvider;

  public Issue getIssue(IssuePK issuePK, boolean withComments) {
    Issue issue = emProvider.get().find(Issue.class, issuePK);
    if (issue == null) {
      throw new NotFoundException();
    }
    if (withComments) {
      issue.getIssueComments();
    }
    return issue;
  }

  public List<Issue> getIssues(RepositoryPK repositoryPK, IssueSearch search) {
    List<Issue> issues = emProvider.get()
        .createQuery(
            "SELECT i FROM Issue i LEFT JOIN FETCH i.pullRequest pr"
                + " WHERE i.pk.accountName=:accountName AND i.pk.repositoryName=:repoName"
                + " AND pr.pk IS " + BooleanUtils.toString(search.isPull(), "NOT", "") + " NULL", Issue.class)
        .setParameter("accountName", repositoryPK.getAccountName())
        .setParameter("repoName", repositoryPK.getRepositoryName())
        .getResultList();
    return issues;
  }

  public List<IssueComment> getComments(IssuePK issuePK) {
    return emProvider.get().createQuery("SELECT ic FROM IssueComment ic WHERE ic.issuePK=:issuePK ORDER BY ic.id",
        IssueComment.class)
        .setParameter("issuePK", issuePK)
        .getResultList();
  }

  @Transactional
  public Issue create(RepositoryPK repositoryPK, IssueForm form) {
    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    Issue issue = createIssueAndComment(repository, form.getTitle(), form.getContent(), form.getAssignedTo());
    return issue;
  }

  @Transactional
  public IssueComment addComment(IssuePK issuePK, CommentForm form, boolean close) {
    IssueComment comment = addComment(issuePK, form.getContent(), BooleanUtils.toString(close, "CLOSE", "COMMENT"));
    if (close) {
      Issue issue = emProvider.get().find(Issue.class, issuePK);
      issue.setClosed(true);
    }
    return comment;
  }

  @Transactional
  public IssueComment addMergeComment(IssuePK issuePK, CommentForm form) {
    addComment(issuePK, form.getContent(), "MERGE");
    return addComment(issuePK, "CLOSE", "CLOSE");
  }

  @Transactional
  public IssueComment modifyComment(long commentId, IssuePK issuePK, CommentForm form) {
    LoginContext context = LoginContext.get(reqProvider.get());
    IssueComment comment = emProvider.get().find(IssueComment.class, commentId);
    if (!comment.getIssuePK().equals(issuePK)) {
      throw new NotFoundException();
    }
    comment.setCommentAccountName(context.getName());
    comment.setContent(form.getContent());
    return comment;
  }

  @Transactional
  public Result<Boolean> registerLabels(IssuePK issuePK, List<String> labels) {
    long nullCount = labels.stream().filter(labelName -> {
      Label label = emProvider.get().find(Label.class, new LabelPK(issuePK.getRepositoryPK(), labelName));
      return label == null;
    }).count();
    if (nullCount > 0) {
      return Result.error("no master");
    }

    Issue issue = emProvider.get().find(Issue.class, issuePK);

    List<IssueLabelPK> newPks = Lists.transform(labels, l -> new IssueLabelPK(issuePK, l));
    List<IssueLabel> issueLabels = issue.getIssueLabels();
    issueLabels.forEach(issueLabel -> {
      if (!newPks.contains(issueLabel.getPk())) {
        emProvider.get().remove(issueLabel);
      }
    });
    newPks.forEach(pk -> {
      if (emProvider.get().find(IssueLabel.class, pk) == null) {
        IssueLabel issueLabel = new IssueLabel();
        issueLabel.setPk(pk);
        emProvider.get().persist(issueLabel);
      }
    });

    return Result.success(true);
  }

  // transaction necessary
  protected Issue createIssueAndComment(Repository repository, String title, String content, String assignedAccountName) {
    LoginContext context = LoginContext.get(reqProvider.get());
    RepositoryPK repositoryPK = repository.getPk();

    Optional<Long> latestIssueId = emProvider.get()
        .createQuery("SELECT i.pk.issueId FROM Issue i WHERE i.pk.accountName=?1 AND i.pk.repositoryName=?2 ORDER BY i.pk.issueId DESC", Long.class)
        .setParameter(1, repositoryPK.getAccountName())
        .setParameter(2, repositoryPK.getRepositoryName()).setMaxResults(1)
        // .setLockMode(LockModeType.PESSIMISTIC_READ)
        .getResultList().stream().findFirst();
    long nextIssueId = latestIssueId.orElse(0L) + 1;

    Issue issue = new Issue();
    issue.setPk(new IssuePK(repositoryPK.getAccountName(), repositoryPK.getRepositoryName(), nextIssueId));
    issue.setOpenedAccountName(context.getName());
    issue.setTitle(title);
    issue.setAssignedAccountName(assignedAccountName);
    issue.setClosed(false);
    emProvider.get().persist(issue);
    IssueComment comment = new IssueComment();
    comment.setIssuePK(issue.getPk());
    comment.setContent(content);
    comment.setActionType("OPEN");
    comment.setCommentAccountName(context.getName());
    emProvider.get().persist(comment);
    return issue;
  }

  private IssueComment addComment(IssuePK issuePK, String content, String actionType) {
    LoginContext context = LoginContext.get(reqProvider.get());
    IssueComment comment = new IssueComment();
    comment.setIssuePK(issuePK);
    comment.setActionType(actionType);
    comment.setCommentAccountName(context.getName());
    comment.setContent(content);
    emProvider.get().persist(comment);
    return comment;
  }
}
