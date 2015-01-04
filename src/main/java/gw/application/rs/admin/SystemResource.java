package gw.application.rs.admin;

import gw.core.auth.Admin;
import gw.dto.admin.SystemJson;
import gw.dto.result.JsonResult;
import gw.dto.result.PostJsonResult;
import gw.service.SystemService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Admin
public class SystemResource {

  @Inject private SystemService systemService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SystemJson getSystemSetting() {
    return systemService.getSystemSetting();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult registerMailSetting(@Valid SystemJson mailJson) {
    return PostJsonResult.get(systemService.registerSettings(mailJson));
  }

}
