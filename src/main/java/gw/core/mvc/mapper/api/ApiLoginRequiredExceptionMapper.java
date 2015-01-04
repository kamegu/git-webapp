package gw.core.mvc.mapper.api;

import gw.core.auth.LoginRequiredException;
import gw.dto.result.JsonResult;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ApiLoginRequiredExceptionMapper implements ExceptionMapper<LoginRequiredException> {

  @Override
  public Response toResponse(LoginRequiredException exception) {
    return Response.status(Status.UNAUTHORIZED).entity(new JsonResult() {
      @Override
      public String getStatus() {
        return "nologin";
      }
    }).build();
  }
}
