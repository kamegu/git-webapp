package gw.application.rs.admin;

import gw.core.action.Result;
import gw.core.auth.Admin;
import gw.dto.admin.GroupJson;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.model.Account;
import gw.service.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
@Admin
public class GroupResource {
  @Inject private UserService userService;

  @GET
  @Path("new")
  @Produces(MediaType.APPLICATION_JSON)
  public GroupJson getNewGroup() {
    return userService.newGroup();
  }

  @GET
  @Path("{groupName}")
  @Produces(MediaType.APPLICATION_JSON)
  public GroupJson getGroup(@PathParam("groupName") final String groupName) {
    Account account = userService.findDto(groupName);
    return new GroupJson(account);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult createGroup(@Valid GroupJson groupJson) {
    Result<Boolean> result = userService.registerGroup(groupJson, false);
    return PostJsonResult.get(result);
  }

  @POST
  @Path("{groupName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult updateGroup(@PathParam("groupName") final String userName, @Valid GroupJson groupJson) {
    if (!userName.equals(groupJson.getAccount().getName())) {
      throw new NotFoundException();
    }

    Result<Boolean> result = userService.registerGroup(groupJson, true);
    return PostJsonResult.get(result);
  }
}
