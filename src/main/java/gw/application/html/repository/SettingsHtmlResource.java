package gw.application.html.repository;

import gw.core.auth.Repo;
import gw.core.mvc.MapViewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Repo(collaboratorOnly = true) // userName/repositoryName
@Path("")
public class SettingsHtmlResource {

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Viewable getHtml() {
    return new MapViewable("/repository/setting.ftl");
  }

  @GET @Path("options")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getOptionHtml() {
    return new MapViewable("/repository/setting.ftl");
  }

  @GET @Path("collaborators")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getCollaboratorHtml() {
    return new MapViewable("/repository/setting.ftl");
  }
}
