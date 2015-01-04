package gw.application.html.repository;

import gw.core.auth.Repo;
import gw.core.mvc.MapViewable;
import gw.model.Repository;
import gw.model.pk.RepositoryPK;
import gw.service.RepositoryService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.mvc.Viewable;

@Repo // userName/repositoryName
@Path("")
public class CompareHtmlResource {
  @Inject private RepositoryService repositoryService;

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  private RepositoryPK getRepositoryPK() {
    return new RepositoryPK(userName, repositoryName);
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Viewable getHtml() {
    Repository repository = repositoryService.find(getRepositoryPK());
    return new MapViewable("/repository/compare.ftl").add("compare",
        compareBranchMap(repository.getDefaultBranchName(), repository.getDefaultBranchName()));
  }

  @GET @Path("{ref :.*[^¥¥.]}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getCompareHtml(@PathParam("ref") String ref) {
    Repository repository = repositoryService.find(getRepositoryPK());
    return new MapViewable("/repository/compare.ftl").add("compare", compareBranchMap(repository.getDefaultBranchName(), ref));
  }

  @GET @Path("{base :.*[^¥¥.]}...{compare :[^¥¥.].*}")
  @Produces(MediaType.TEXT_HTML)
  public Viewable getCompareHtml(@PathParam("base") String base, @PathParam("compare") String compare) {
    return new MapViewable("/repository/compare.ftl").add("compare", compareBranchMap(base, compare));
  }

  private Map<String, String> compareBranchMap(String base, String compare) {
    Map<String, String> compareMap = new HashMap<>();
    compareMap.put("baseBranch", base);
    compareMap.put("compareBranch", compare);
    return compareMap;
  }
}
