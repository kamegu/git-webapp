package gw.servlet;

import gw.core.jpa.EntityOperatorProvider;
import gw.core.util.ClassFinder;
import gw.git.GitApi;
import gw.git.GitOperation;

import javax.inject.Singleton;
import javax.servlet.annotation.WebListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

@WebListener
public class GuiceListener extends GuiceServletContextListener {

  private static Injector injector;

  @Override
  protected Injector getInjector() {
    injector = Guice.createInjector(new ServletModule() {
      @Override
      protected void configureServlets() {
        String persistenceUnitName = PersistenceInitializeListener.getPersistenceUnitName();

        install(new JpaPersistModule(persistenceUnitName));
        filter("/*").through(PersistFilter.class);

        requestStaticInjection(EntityOperatorProvider.class);

        bind(GitOperation.class).in(Singleton.class);
        bind(GitApi.class).in(Singleton.class);

        ClassFinder.findClasses("gw.service").forEach(clazz -> bind(clazz).in(Singleton.class));
      }
    });
    return injector;
  }

  public static Injector get() {
    return injector;
  }
}
