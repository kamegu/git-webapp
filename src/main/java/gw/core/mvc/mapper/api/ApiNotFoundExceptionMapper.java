package gw.core.mvc.mapper.api;

import gw.dto.result.JsonResult;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ApiNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  @Override
  public Response toResponse(NotFoundException exception) {
    return Response.status(Status.NOT_FOUND).entity(new JsonResult() {
      @Override
      public String getStatus() {
        return "not_found";
      }
    }).build();
  }
}
