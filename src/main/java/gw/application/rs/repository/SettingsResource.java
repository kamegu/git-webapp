package gw.application.rs.repository;

import gw.core.action.Result;
import gw.core.auth.Repo;
import gw.dto.repository.CollaboratorJson;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.model.Collaborator;
import gw.model.pk.RepositoryPK;
import gw.service.CollaboratorService;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.validator.constraints.NotEmpty;

@Repo(collaboratorOnly = true) // userName/repositoryName
@Path("")
public class SettingsResource {

  @Inject private CollaboratorService collaboratorService;

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  private RepositoryPK getRepositoryPK() {
    return new RepositoryPK(userName, repositoryName);
  }

  @GET @Path("collaborators")
  @Produces(MediaType.APPLICATION_JSON)
  public List<CollaboratorJson> getCollaborators() {
    return collaboratorService.getNames(getRepositoryPK()).stream().map(CollaboratorJson::new).collect(Collectors.toList());
  }

  @POST @Path("collaborators")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult addCollaborator(@NotEmpty String collaboratorName) {
    Result<Collaborator> result = collaboratorService.add(getRepositoryPK(), collaboratorName);
    return PostJsonResult.get(result, c -> c.getPk());
  }

  @POST @Path("collaborators/{collaboratorName}/delete")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult deleteCollaborator(@PathParam("collaboratorName") String collaboratorName) {
    Result<String> result = collaboratorService.delete(getRepositoryPK(), collaboratorName);
    return PostJsonResult.get(result);
  }
}
