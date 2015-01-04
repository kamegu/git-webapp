package gw.servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

@WebListener
public class PersistenceInitializeListener implements ServletContextListener {

  private static String persistenceUnitName;

  protected static String getPersistenceUnitName() {
    if (persistenceUnitName == null) {
      try {
        Context context=new InitialContext();
        DataSource dataSource = (DataSource)context.lookup("java:comp/env/jdbc/gitapp");
        migrate(dataSource, "db/migration/common", "db/migration/mysql");
        persistenceUnitName = "gitapp";
      } catch (NamingException e) {
        try {
          Context context=new InitialContext();
          DataSource dataSource = (DataSource)context.lookup("java:comp/env/jdbc/gitapp-default");
          migrate(dataSource, "db/migration/common", "db/migration/h2");
          persistenceUnitName = "gitapp-default";
        } catch (NamingException e1) {
        }
      }
    }
    return persistenceUnitName;
  }

  private static void migrate(DataSource dataSource, String... locations) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.setLocations(locations);

    /* for development */
//    flyway.clean();
//    flyway.repair();

    flyway.migrate();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    getPersistenceUnitName();
    
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    
  }
}
