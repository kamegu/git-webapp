package gw.application.rs.admin;

import gw.core.action.Result;
import gw.core.auth.Admin;
import gw.dto.admin.UserFullJson;
import gw.dto.admin.UserJson;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.model.Account;
import gw.service.UserService;
import gw.validator.PasswordRequired;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("")
@Admin
public class UserResource {
  @Inject private UserService userService;

  @GET
  @Path("list.json")
  @Produces(MediaType.APPLICATION_JSON)
  public List<UserFullJson> getListJson(@QueryParam("deleted") boolean showDeleted) {
    List<Account> allAcounts = userService.findAll(showDeleted);
    return allAcounts.stream().map(dto -> new UserFullJson(dto)).collect(Collectors.toList());
  }

  @GET
  @Path("{userName}")
  @Produces(MediaType.APPLICATION_JSON)
  public UserJson getUserJson(@PathParam("userName") final String userName) {
    Account account = userService.findDto(userName);
    return new UserJson(account);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult createUser(@Valid @PasswordRequired UserJson userJson) {
    Result<Boolean> result = userService.register(userJson, false);
    return PostJsonResult.get(result);
  }

  @POST
  @Path("{userName}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult updateUser(@PathParam("userName") final String userName, @Valid UserJson userJson) {
    if (!userName.equals(userJson.getAccount().getName())) {
      throw new NotFoundException();
    }
    Result<Boolean> result = userService.register(userJson, true);
    return PostJsonResult.get(result);
  }

}
