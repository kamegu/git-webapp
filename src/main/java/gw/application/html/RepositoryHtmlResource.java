package gw.application.html;

import gw.application.html.repository.CompareHtmlResource;
import gw.application.html.repository.IssueHtmlResource;
import gw.core.auth.Login;
import gw.core.auth.Repo;
import gw.core.mvc.MapViewable;
import gw.git.FileContent;
import gw.git.FileListDto;
import gw.git.GitOperation;
import gw.git.RefPath;
import gw.model.Repository;
import gw.model.pk.RepositoryPK;
import gw.service.RepositoryService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import lombok.Getter;
import lombok.Setter;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.mvc.Viewable;

@Path("")
public class RepositoryHtmlResource {

  @Inject private RepositoryService repositoryService;
  @Inject private GitOperation gitOperation;
  @Context private HttpServletRequest servletRequest;

  @GET
  @Path("repository/new")
  @Produces(MediaType.TEXT_HTML)
  @Login
  public Viewable getNewRepositoryHtml() {
    return new MapViewable("/repository/new.ftl");
  }

  @Repo
  @GET @Path("{userName}/{repositoryName}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getIndexHtml(@BeanParam RepositoryKey key) {
    Repository repository = repositoryService.find(key.getPK());

    RefPath refPath = new RefPath(repository.getDefaultBranchName(), "");
    FileListDto fileListDto = gitOperation.getFileList(key.getPK(), refPath);
    if (fileListDto != null) {
      return new MapViewable("/repository/main.ftl").add("refPath", refPath)
          .add("fileList", fileListDto);
    } else {
      return new MapViewable("/repository/guide.ftl");
    }
  }

  @Repo
  @GET @Path("{userName}/{repositoryName}/tree/{refAndPath :.+}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getTreeHtml(@BeanParam RepositoryKey key, @PathParam("refAndPath") final String refAndPath) {
    RefPath refPath = gitOperation.separateRefAndPath(key.getPK(), refAndPath);
    FileListDto fileListDto = gitOperation.getFileList(key.getPK(), refPath);
    return new MapViewable("/repository/main.ftl").add("refPath", refPath)
        .add("fileList", fileListDto);
  }

  @Repo
  @GET @Path("{userName}/{repositoryName}/commit/{commitId}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getCommitHtml(@BeanParam RepositoryKey key, @PathParam("commitId") final String commitId) {
    return new MapViewable("/repository/commit.ftl");
  }

  @Repo
  @GET @Path("{userName}/{repositoryName}/blob/{refAndPath :.+}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getBlobHtml(@BeanParam RepositoryKey key, @PathParam("refAndPath") final String refAndPath) {
    RefPath refPath = gitOperation.separateRefAndPath(key.getPK(), refAndPath);
    FileContent fileContent = gitOperation.getFileContent(key.getPK(), refPath);
    return new MapViewable("/repository/blob.ftl").add("refPath", refPath)
        .add("fileContent", fileContent);
  }

  @Path("{userName}/{repositoryName}/issues")
  public Resource getIssueResource() {
    return Resource.from(IssueHtmlResource.class);
  }

  @Path("{userName}/{repositoryName}/compare")
  public Resource getCompareResource() {
    return Resource.from(CompareHtmlResource.class);
  }


  @Repo // userName/repositoryName
  @GET
  @Path("{userName}/{repositoryName}/labels")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getHtml() {
    return new MapViewable("/repository/issue.ftl");
  }

  @Repo // userName/repositoryName
  @GET
  @Path("{userName}/{repositoryName}/pulls")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getListHtml() {
    return new MapViewable("/repository/issue.ftl");
  }

  @Path("{userName}/{repositoryName}/settings")
  public Resource getSettingsResource() {
    return Resource.from(gw.application.html.repository.SettingsHtmlResource.class);
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
