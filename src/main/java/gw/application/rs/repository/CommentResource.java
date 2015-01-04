package gw.application.rs.repository;

import gw.core.auth.Login;
import gw.core.auth.Repo;
import gw.core.util.Markdown;
import gw.dto.repository.CommentForm;
import gw.dto.result.JsonResult;
import gw.dto.result.SuccessResult;
import gw.model.Issue;
import gw.model.IssueComment;
import gw.model.pk.IssuePK;
import gw.service.IssueService;
import gw.service.NoticeService;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.Getter;

@Repo // userName/repositoryName
@Path("")
public class CommentResource {

  @Inject private IssueService issueService;
  @Inject private NoticeService noticeService;

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;
  @PathParam("issueId")
  private long issueId;

  private IssuePK getIssuePK() {
    return new IssuePK(userName, repositoryName, issueId);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<CommentJson> getIssueComments() {
    Issue issue = issueService.getIssue(getIssuePK(), true);
    return Lists.transform(issue.getIssueComments(), CommentJson::new);
  }

  @Login
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult createComment(@Valid CommentForm form) {
    IssueComment comment = issueService.addComment(getIssuePK(), form, false);

    noticeService.sendIssueCommentNotice(comment, "commented");

    return new SuccessResult<>(comment.getId());
  }

  @Repo(collaboratorOnly = true)
  @POST @Path("{commentId :[0-9]+}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult updateComment(@PathParam("commentId") long commentId, @Valid CommentForm form) {
    IssueComment comment = issueService.modifyComment(commentId, getIssuePK(), form);

    noticeService.sendIssueCommentNotice(comment, "commented");

    return new SuccessResult<>(true);
  }

  @Getter
  public static class CommentJson {
    private final long id;
    private final String content;
    private final String commentAccountName;
    private final Timestamp registered;

    public CommentJson(IssueComment comment) {
      this.id = comment.getId();
      this.content = Markdown.compile(comment.getContent());
      this.commentAccountName = comment.getCommentAccountName();
      this.registered = comment.getUpdateInfo().getRegisteredTimestamp();
    }
  }
}
