package gw.application.rs;

import gw.application.rs.admin.GroupResource;
import gw.application.rs.admin.MailResource;
import gw.application.rs.admin.SystemResource;
import gw.application.rs.admin.UserResource;
import gw.core.auth.Admin;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.model.Resource;

@Path("settings/admin")
@Admin
public class AdminResource {

  @Path("users")
  public Resource getUserResource() {
    return Resource.from(UserResource.class);
  }

  @Path("groups")
  public Resource getGroupResource() {
    return Resource.from(GroupResource.class);
  }

  @Path("system")
  public Resource getSystemResource() {
    return Resource.from(SystemResource.class);
  }

  @Path("mail")
  public Resource getMailResource() {
    return Resource.from(MailResource.class);
  }

}
