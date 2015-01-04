package gw.application.rs;

import gw.application.rs.repository.CommentResource;
import gw.application.rs.repository.GitResource;
import gw.application.rs.repository.IssueResource;
import gw.application.rs.repository.LabelResource;
import gw.application.rs.repository.PullResource;
import gw.core.LoginContext;
import gw.core.action.Result;
import gw.core.auth.Login;
import gw.core.auth.Repo;
import gw.dto.form.RepositoryForm;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.dto.result.SuccessResult;
import gw.model.Repository;
import gw.model.pk.RepositoryPK;
import gw.service.RepositoryService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import lombok.Getter;
import lombok.Setter;

import org.glassfish.jersey.server.model.Resource;

@Path("")
public class RepositoryResource {

  @Inject private RepositoryService repositoryService;
  @Context private HttpServletRequest servletRequest;

  @POST
  @Path("repository/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Login
  public JsonResult createRepository(@Valid RepositoryForm repositoryForm) {
    LoginContext context = LoginContext.get(servletRequest);
    Result<Repository> repository = repositoryService.createRepository(context.getName(), repositoryForm);
    return PostJsonResult.get(repository, r -> r.getPk());
  }

  @Repo(collaboratorOnly = true)
  @POST @Path("{userName}/{repositoryName}/delete")
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult delete(@PathParam("userName") final String userName, @PathParam("repositoryName") final String repositoryName) {
    LoginContext context = LoginContext.get(servletRequest);
    boolean result = repositoryService.deleteRepository(new RepositoryPK(userName, repositoryName), context);

    return new SuccessResult<>(result);
  }

  @Path("{userName}/{repositoryName}/issues")
  public Resource getIssueResource() {
    return Resource.from(IssueResource.class);
  }

  @Path("{userName}/{repositoryName}/pulls")
  public Resource getPullResource() {
    return Resource.from(PullResource.class);
  }

  @Path("{userName}/{repositoryName}/issues/{issueId :[0-9]+}/comments")
  public Resource getCommentResource() {
    return Resource.from(CommentResource.class);
  }

  @Path("{userName}/{repositoryName}/labels")
  public Resource getLabelResource() {
    return Resource.from(LabelResource.class);
  }

  @Path("{userName}/{repositoryName}/settings")
  public Resource getSettingsResource() {
    return Resource.from(gw.application.rs.repository.SettingsResource.class);
  }

  @Path("{userName}/{repositoryName}/git")
  public Resource getGitResource() {
    return Resource.from(GitResource.class);
  }

  @Getter @Setter
  public static class RepositoryKey {
    @PathParam("userName")
    private String userName;
    @PathParam("repositoryName")
    private String repositoryName;

    public RepositoryPK getPK() {
      return new RepositoryPK(userName, repositoryName);
    }
  }
}
