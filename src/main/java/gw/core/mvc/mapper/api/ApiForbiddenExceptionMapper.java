package gw.core.mvc.mapper.api;

import gw.dto.result.JsonResult;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class ApiForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

  @Override
  public Response toResponse(ForbiddenException exception) {
    return Response.status(Status.FORBIDDEN).entity(new JsonResult() {
      @Override
      public String getStatus() {
        return "forbidden";
      }
      @SuppressWarnings("unused")
      public List<String> getMessages() {
        return Arrays.asList(exception.getMessage());
      }
    }).build();
  }
}
