package gw.application.rs.repository;

import gw.core.action.Result;
import gw.core.auth.Repo;
import gw.dto.repository.LabelJson;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.model.pk.RepositoryPK;
import gw.service.LabelService;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Repo // userName/repositoryName
@Path("")
public class LabelResource {

  @Inject private LabelService labelService;

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  private RepositoryPK getRepositoryPK() {
    return new RepositoryPK(userName, repositoryName);
  }

  @Repo(collaboratorOnly = true)
  @POST
  @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public JsonResult createLabel(@Valid LabelJson form) {
    Result<LabelJson> result = labelService.createLabel(getRepositoryPK(), form);
    return PostJsonResult.get(result);
  }

  @Repo(collaboratorOnly = true)
  @POST
  @Path("{name}")
  @Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
  public JsonResult updateLabel(@PathParam("name") String labelName, @Valid LabelJson form) {
    Result<LabelJson> result = labelService.updateLabel(getRepositoryPK(), labelName, form);
    return PostJsonResult.get(result);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<LabelJson> getListJson() {
    return labelService.findLabels(getRepositoryPK());
  }
}
