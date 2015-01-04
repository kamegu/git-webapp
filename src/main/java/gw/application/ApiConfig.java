package gw.application;

import gw.core.HttpCacheControlFilter;
import gw.core.auth.AdminNecessaryRequestFilter;
import gw.core.auth.AuthenticationRequestFilter;
import gw.core.auth.RepositoryControllRequestFilter;
import gw.core.mvc.mapper.api.ApiForbiddenExceptionMapper;
import gw.core.mvc.mapper.api.ApiLoginRequiredExceptionMapper;
import gw.core.mvc.mapper.api.ApiNotFoundExceptionMapper;
import gw.core.validation.AjaxValidationErrorExceptionMapper;
import gw.core.validation.PostErrorResponseFilter;
import gw.servlet.GuiceListener;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

@ApplicationPath("api")
public class ApiConfig extends ResourceConfig {

  @Inject
  public ApiConfig(ServiceLocator serviceLocator) {
    register(JacksonFeature.class);
    register(ValidationFeature.class);
    property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, "true");

    register(AuthenticationRequestFilter.class);
    register(AdminNecessaryRequestFilter.class);
    register(RepositoryControllRequestFilter.class);

    register(HttpCacheControlFilter.class);

    register(ApiForbiddenExceptionMapper.class);
    register(ApiNotFoundExceptionMapper.class);
    register(ApiLoginRequiredExceptionMapper.class);
    register(AjaxValidationErrorExceptionMapper.class);
    register(PostErrorResponseFilter.class);

    GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
    GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    guiceBridge.bridgeGuiceInjector(GuiceListener.get());

    packages(false, this.getClass().getPackage() + ".rs");
  }
}
