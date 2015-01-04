package gw.application.rs.repository;

import gw.core.action.Result;
import gw.core.auth.Login;
import gw.core.auth.Repo;
import gw.dto.repository.CommentForm;
import gw.dto.repository.IssueJson;
import gw.dto.repository.PullRequestForm;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.dto.result.SuccessResult;
import gw.model.Issue;
import gw.model.pk.IssuePK;
import gw.model.pk.RepositoryPK;
import gw.service.NoticeService;
import gw.service.PullRequestService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Repo // userName/repositoryName
@Path("")
public class PullResource {
  @Inject private PullRequestService pullRequestService;
  @Inject private NoticeService noticeService;

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  private RepositoryPK getRepositoryPK() {
    return new RepositoryPK(userName, repositoryName);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  @Login
  public JsonResult create(@Valid PullRequestForm pullRequestForm) {
    Issue issue = pullRequestService.create(getRepositoryPK(), pullRequestForm);

    noticeService.sendIssueCreateNotice(issue.getPk());

    return new SuccessResult<>(issue.getPk());
  }

  @Repo(collaboratorOnly = true)
  @POST
  @Path("{issueId :[0-9]+}/merge")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult merge(@PathParam("issueId") final long issueId, @Valid CommentForm form) {
    IssuePK issuePK = new IssuePK(userName, repositoryName, issueId);
    Result<Issue> issue = pullRequestService.merge(issuePK, form);
    return PostJsonResult.get(issue, IssueJson::newInstance);
  }
}
