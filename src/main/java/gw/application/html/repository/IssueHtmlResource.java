package gw.application.html.repository;

import gw.core.auth.Login;
import gw.core.auth.Repo;
import gw.core.mvc.MapViewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Repo // userName/repositoryName
@Path("")
public class IssueHtmlResource {

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Viewable getListHtml() {
    return new MapViewable("/repository/issue.ftl");
  }

  // issue / pull request
  @GET @Path("{issueId :[0-9]+}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getDetailHtml(@PathParam("issueId") long issueId) {
    return new MapViewable("/repository/issue.ftl");
  }

  @Login
  @GET @Path("new")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getNewHtml() {
    return new MapViewable("/repository/issue.ftl");
  }
}
