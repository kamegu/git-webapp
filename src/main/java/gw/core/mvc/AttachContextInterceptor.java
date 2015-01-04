package gw.core.mvc;

import gw.core.RepositoryContext;
import gw.model.AppProperty;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.server.mvc.Viewable;

@Provider
@Priority(Priorities.ENTITY_CODER + 1)
public class AttachContextInterceptor implements WriterInterceptor {

  @Context private HttpServletRequest servletRequest;
  @Inject private com.google.inject.Provider<EntityManager> emProvider;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
    if (context.getEntity() != null && context.getEntity() instanceof Viewable) {
      final Viewable viewable = (Viewable) context.getEntity();

      Object model = viewable.getModel();
      if (!(model instanceof Map)) {
        model = new HashMap<String, Object>() {
          private static final long serialVersionUID = 1L;
          {
            put("model", viewable.getModel());
          }
        };
      }
      ((Map) model).put("context", getContextMap());
      ((Map) model).put("session", getSessionMap());

      if (servletRequest.getAttribute(RepositoryContext.ATTR_NAME) != null) {
        RepositoryContext repoContext = (RepositoryContext) servletRequest.getAttribute(RepositoryContext.ATTR_NAME);
        ((Map) model).put("repo", repoContext);
      }

      context.setEntity(new Viewable(viewable.getTemplateName(), model));
    }

    context.proceed();
  }

  private Map<String, Object> getContextMap() {
    Map<String, Object> context = new HashMap<>();
    context.put("path", servletRequest.getContextPath());
    context.put("appPath", servletRequest.getContextPath() + servletRequest.getServletPath());
    context.put("fullPath", servletRequest.getRequestURI());
    String withQuery = servletRequest.getRequestURI();
    if (StringUtils.isNotBlank(servletRequest.getQueryString())) {
      withQuery += "?" + servletRequest.getQueryString();
    }
    context.put("fullPathWithQuery", withQuery);
    
    AppProperty property = emProvider.get().find(AppProperty.class, AppProperty.SYSTEM_NAME);
    if (property != null) {
      context.put("appName", property.getValue());
    }

    return context;
  }

  private Map<String, Object> getSessionMap() {
    Map<String, Object> sessions = new HashMap<>();
    for (String key : Collections.list(servletRequest.getSession().getAttributeNames())) {
      sessions.put(key, servletRequest.getSession().getAttribute(key));
    }
    return sessions;
  }
}
