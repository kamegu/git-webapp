package gw.application.rs.repository;

import gw.core.action.Result;
import gw.core.auth.Login;
import gw.core.auth.Repo;
import gw.dto.repository.CommentForm;
import gw.dto.repository.IssueForm;
import gw.dto.repository.IssueJson;
import gw.dto.repository.IssueSearch;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.dto.result.SuccessResult;
import gw.model.Issue;
import gw.model.IssueComment;
import gw.model.pk.IssuePK;
import gw.model.pk.RepositoryPK;
import gw.service.IssueService;
import gw.service.NoticeService;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Repo // userName/repositoryName
@Path("")
public class IssueResource {
  @Inject private EntityManager em;
  @Inject private IssueService issueService;
  @Inject private NoticeService noticeService;

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  private RepositoryPK getRepositoryPK() {
    return new RepositoryPK(userName, repositoryName);
  }

  // issue / pull request
  @GET @Path("list.json")
  @Produces(MediaType.APPLICATION_JSON)
  public List<IssueJson> getListJson(@BeanParam IssueSearch search) {
    List<Issue> issues = issueService.getIssues(getRepositoryPK(), search);
    return issues.stream().map(IssueJson::newInstance).collect(Collectors.toList());
  }

  // issue / pull request
  @GET @Path("{issueId :[0-9]+}")
  @Produces(MediaType.APPLICATION_JSON)
  public IssueJson getJson(@PathParam("issueId") long issueId) {
    Issue issue = em.find(Issue.class, new IssuePK(userName, repositoryName, issueId));
    return IssueJson.newInstance(issue, true);
  }

  @Login
  @POST
  @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public JsonResult create(@Valid IssueForm issueForm) {
    Issue issue = issueService.create(getRepositoryPK(), issueForm);

    noticeService.sendIssueCreateNotice(issue.getPk());

    return new SuccessResult<>(issue.getPk());
  }

  // issue / pull request
  @Repo(collaboratorOnly = true)
  @POST @Path("{issueId :[0-9]+}/close")
  @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public JsonResult closeIssue(@PathParam("issueId") long issueId, @Valid CommentForm form) {
    IssueComment comment = issueService.addComment(new IssuePK(userName, repositoryName, issueId), form, true);

    noticeService.sendIssueCommentNotice(comment, "closed");

    return new SuccessResult<>(comment.getId());
  }

  @Repo(collaboratorOnly = true)
  @POST @Path("{issueId :[0-9]+}/labels")
  @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public JsonResult addLabel(@PathParam("issueId") long issueId, IssueForm issueForm/*use only labels*/) {
    Result<Boolean> registerResult = issueService.registerLabels(new IssuePK(userName, repositoryName, issueId), issueForm.getLabels());
    return PostJsonResult.get(registerResult);
  }
}
