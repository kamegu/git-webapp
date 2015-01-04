package gw.application;

import gw.core.HttpCacheControlFilter;
import gw.core.auth.AdminNecessaryRequestFilter;
import gw.core.auth.AuthenticationRequestFilter;
import gw.core.auth.RepositoryControllRequestFilter;
import gw.core.mvc.AttachContextInterceptor;
import gw.core.mvc.freemarker.FlexibleConfiguration;
import gw.core.mvc.mapper.html.HtmlLoginRequiredExceptionMapper;
import gw.servlet.GuiceListener;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

@ApplicationPath("")
public class AppConfig extends ResourceConfig {

  @Inject
  public AppConfig(ServiceLocator serviceLocator) {
    register(FreemarkerMvcFeature.class);
    property(FreemarkerMvcFeature.TEMPLATES_BASE_PATH, "/WEB-INF/templates");
    property(FreemarkerMvcFeature.TEMPLATE_OBJECT_FACTORY, FlexibleConfiguration.class);

    register(AttachContextInterceptor.class);
    register(ValidationFeature.class);
    property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, "true");

    register(AuthenticationRequestFilter.class);
    register(AdminNecessaryRequestFilter.class);
    register(RepositoryControllRequestFilter.class);
    register(HtmlLoginRequiredExceptionMapper.class);

    register(HttpCacheControlFilter.class);

    GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    guiceBridge.bridgeGuiceInjector(GuiceListener.get());

    packages(false, this.getClass().getPackage() + ".html");
  }
}
