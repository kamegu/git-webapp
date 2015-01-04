package gw.service;

import gw.core.util.Markdown;
import gw.core.util.RequestUtils;
import gw.model.Account;
import gw.model.AppProperty;
import gw.model.Issue;
import gw.model.IssueComment;
import gw.model.Repository;
import gw.model.pk.IssuePK;
import gw.model.pk.RepositoryPK;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provider;

public class NoticeService {
  @Inject private Provider<EntityManager> emProvider;
  @Inject private MailSender mailSender;
  @Inject private CollaboratorService collaboratorService;
  @Inject private Provider<HttpServletRequest> requestProvider;

  public int sendIssueCreateNotice(IssuePK issuePK) {
    emProvider.get().clear();
    IssueComment issueComment = emProvider.get().createNamedQuery("IssueComment.list", IssueComment.class)
        .setParameter("issuePK", issuePK)
        .setMaxResults(1)
        .getSingleResult();

    return sendIssueCommentNotice(issueComment, "created");
  }

  public int sendIssueCommentNotice(IssueComment issueComment, String action) {
    IssuePK issuePK = issueComment.getIssuePK();
    Issue issue = emProvider.get().find(Issue.class, issuePK);

    Account account = emProvider.get().find(Account.class, issueComment.getCommentAccountName());

    // use /issues/{issueId} even when pull request
    String path = "issues" + "/" + issue.getPk().getIssueId();

    IssueMessageForm messageForm = new IssueMessageForm(issuePK,
        account.getUserAccount().getFullName() + " " + action + " " + BooleanUtils.toString(issue.getPullRequest() != null, "pull request", "issue"),
        url(issuePK.getRepositoryPK(), path), issueComment.getContent());

    return sendNotice(messageForm, account, issue.getTitle());
  }

  private int sendNotice(IssueMessageForm messageForm, Account account, String title) {
    RepositoryPK repositoryPK = messageForm.issuePK.getRepositoryPK();
    List<Account> recipients = getRecipients(messageForm.issuePK);
    String[] sendToAddresses = recipients.stream().filter(recipient -> !recipient.getName().equals(account.getName()))
        .map(recipient -> recipient.getUserAccount().getMailAddress())
        .distinct()
        .toArray(size -> new String[size]);

    mailSender.sendMail("[" + repositoryPK.getRepositoryName() + " #" + messageForm.issuePK.getIssueId() + "]" + title,
        messageForm.getMessage(), sendToAddresses);
    return 0;
  }

  /**
   * recipients
   * includes owner, collaborator, commented users
   */
  private List<Account> getRecipients(IssuePK issuePK) {
    Set<String> userNames = new HashSet<>();

    Repository repository = emProvider.get().find(Repository.class, issuePK.getRepositoryPK());
    userNames.addAll(repository.getAllOwners());

    userNames.addAll(collaboratorService.getNames(repository.getPk()));

    List<IssueComment> issueComments = emProvider.get().createNamedQuery("IssueComment.list", IssueComment.class)
        .setParameter("issuePK", issuePK)
        .getResultList();
    userNames.addAll(Lists.transform(issueComments, IssueComment::getCommentAccountName));

    return emProvider.get().createNamedQuery("Account.findIncluded", Account.class)
        .setParameter("names", userNames)
        .getResultList()
        .stream()
        .filter(account -> !account.isDeleted() && account.getUserAccount() != null)
        .collect(Collectors.toList());
  }

  private String url(RepositoryPK repositoryPK, String path) {
    return url(repositoryPK.getPath() + "/" + path);
  }

  private String url(String path) {
    HttpServletRequest request = requestProvider.get();
    AppProperty property = emProvider.get().find(AppProperty.class, AppProperty.SYSTEM_URL);
    if (property != null) {
      return StringUtils.removeEnd(property.getValue(), "/") + request.getContextPath() + path;
    }
    return StringUtils.removeEnd(RequestUtils.getHost(request), "/") + request.getContextPath() + path;
  }

  @RequiredArgsConstructor
  private static class IssueMessageForm {
    private final IssuePK issuePK;
    
    private final String header;
    private final String url;
    private final String content;

    public String getMessage() {
      return header + "\r\n\r\n"
          + Markdown.compile(content)
          + "\r\n\r\n" + url;
    }
  }
}
