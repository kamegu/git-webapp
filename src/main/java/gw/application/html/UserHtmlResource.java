package gw.application.html;

import gw.core.mvc.MapViewable;
import gw.model.Account;
import gw.service.RepositoryService;
import gw.service.UserService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("")
public class UserHtmlResource {

  @Inject private UserService userService;
  @Inject private RepositoryService repositoryService;

  @GET
  @Path("{userName}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable index(@PathParam("userName") String userName, @QueryParam("tab") final String tab) {
    Account account = userService.findAccount(userName);

    MapViewable viewable = new MapViewable("/user/main.ftl").add("account", account).add("tab", tab);

    if ("activity".equals(tab)) {

    } else {
      viewable.add("repositories", repositoryService.getByOwner(userName));
    }

    return viewable;
  }
}
