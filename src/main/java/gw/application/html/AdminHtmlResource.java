package gw.application.html;

import gw.core.auth.Admin;
import gw.core.mvc.MapViewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("settings/admin")
@Admin
public class AdminHtmlResource {

  /** users **/
  @GET
  @Path("users")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getUserHtml() {
    return new MapViewable("/settings/admin.ftl");
  }

  @GET
  @Path("users/new")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getUserCreateHtml() {
    return new MapViewable("/settings/admin.ftl");
  }

  @GET
  @Path("users/{userName}/edit")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getUserEditHtml(@PathParam("userName") final String userName) {
    return new MapViewable("/settings/admin.ftl");
  }

  /** groups **/
  @GET
  @Path("groups/new")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getGroupCreateHtml() {
    return new MapViewable("/settings/admin.ftl");
  }

  @GET
  @Path("groups/{groupName}/edit")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getGroupEditHtml(@PathParam("groupName") final String groupName) {
    return new MapViewable("/settings/admin.ftl");
  }

  @GET
  @Path("system")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getSystemHtml() {
    return new MapViewable("/settings/admin.ftl");
  }

  @GET
  @Path("mail")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getMailHtml() {
    return new MapViewable("/settings/admin.ftl");
  }

}
