package gw.core.validation;

import gw.dto.result.ValidationErrorResult;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.validation.internal.ValidationHelper;

@Provider
@Priority(Priorities.USER)
public class AjaxValidationErrorExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

  @Context HttpServletRequest httpServletRequest;

  @Override
  public Response toResponse(ConstraintViolationException exception) {
    ValidationErrorResult result = new ValidationErrorResult();

    for (ConstraintViolation<?> v : exception.getConstraintViolations()) {
      String paramName = ConstraintUtils.paramName(v);
      result.addError(paramName, v.getMessage());
    }
    return Response.status(ValidationHelper.getResponseStatus(exception))
        .type(MediaType.APPLICATION_JSON)
        .entity(result)
        .build();
  }
}