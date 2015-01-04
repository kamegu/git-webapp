package gw.core.auth;

import gw.core.LoginContext;
import gw.core.util.ResourceUtils;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationRequestFilter implements ContainerRequestFilter {

  @Context private ResourceInfo resourceInfo;
  @Context private HttpServletRequest servletRequest;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (ResourceUtils.getAnnotation(resourceInfo, Login.class).isPresent() || ResourceUtils.getAnnotation(resourceInfo, Admin.class).isPresent()) {
      Object object = servletRequest.getSession().getAttribute(LoginContext.SESSION_KEY);
      if (object == null && !(object instanceof LoginContext)) {
        throw new LoginRequiredException();
      }
    }
  }

}
