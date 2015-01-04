package gw.core.auth;

import gw.core.LoginContext;
import gw.core.util.ResourceUtils;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class AdminNecessaryRequestFilter implements ContainerRequestFilter {

  @Context private ResourceInfo resourceInfo;
  @Context private HttpServletRequest servletRequest;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (ResourceUtils.getAnnotation(resourceInfo, Admin.class).isPresent()) {
      LoginContext context = LoginContext.get(servletRequest);
      if (!context.isAdmin()) {
        throw new ForbiddenException("admin-only");
      }
    }
  }

}
