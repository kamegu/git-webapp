package gw.core.auth;

import gw.core.RepositoryContext;
import gw.core.util.ResourceUtils;
import gw.model.pk.RepositoryPK;
import gw.service.RepositoryController;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RepositoryControllRequestFilter implements ContainerRequestFilter {

  @Context private ResourceInfo resourceInfo;
  @Context private HttpServletRequest servletRequest;
  @Context private UriInfo uriInfo;
  @Inject private RepositoryController repositoryController;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Repo repo = ResourceUtils.getAnnotation(resourceInfo, Repo.class).orElse(null);
    if (repo != null) {
      String owner = uriInfo.getPathParameters().getFirst(repo.ownerPath());
      String repoName = uriInfo.getPathParameters().getFirst(repo.repositoryPath());
      if (owner == null || repoName == null) {
        throw new NotFoundException();
      }

      Optional<RepositoryContext> repoContext = repositoryController.getContext(new RepositoryPK(owner, repoName));
      if (!repoContext.isPresent()) {
        throw new NotFoundException();
      }

      servletRequest.setAttribute(RepositoryContext.ATTR_NAME, repoContext.get());

      if (!repoContext.get().canAccess(repo.collaboratorOnly())) {
        throw new ForbiddenException("collaborator-only");
      }
    }
  }
}
