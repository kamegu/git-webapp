package gw.application.html;

import gw.core.auth.Login;
import gw.core.mvc.MapViewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("settings")
@Login
public class SettingsHtmlResource {

  @GET
  @Path("profile")
  @Produces(MediaType.TEXT_HTML)
  public Viewable index() {

    return new MapViewable("/settings/profile.ftl");
  }
}
