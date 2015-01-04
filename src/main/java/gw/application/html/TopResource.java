package gw.application.html;

import gw.core.LoginContext;
import gw.core.mvc.MapViewable;
import gw.model.Repository;
import gw.service.RepositoryService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

@Path("")
public class TopResource {

  @Context private HttpServletRequest servletRequest;
  @Inject private RepositoryService repositoryService;

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Viewable index() {
    LoginContext context = LoginContext.get(servletRequest);

    List<Repository> recentRepositories = repositoryService.getRecentList();
    if (context != null && context.getName() != null) {
      List<Repository> myList = repositoryService.getMyList(context.getName());
      List<Repository> collaboratingList = repositoryService.getCollaboratingList(context.getName());
      return new MapViewable("/index.ftl").add("recentRepositories", recentRepositories)
          .add("myList", myList)
          .add("collaboratingList", collaboratingList);
    }
    return new MapViewable("/index-nologin.ftl").add("recentRepositories", recentRepositories);
  }

  @GET
  @Path("login")
  @Produces(MediaType.TEXT_HTML)
  public Viewable loginForm(@QueryParam("url") final String url) {

    return new MapViewable("/login.ftl").add("url", url);
  }

  @GET
  @Path("logout")
  public Response logout() throws URISyntaxException {
    servletRequest.getSession().invalidate();
    return Response.seeOther(new URI(servletRequest.getContextPath() + "/")).build();
  }
}
