package gw.core.mvc.mapper.html;

import gw.core.auth.LoginRequiredException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class HtmlLoginRequiredExceptionMapper implements ExceptionMapper<LoginRequiredException> {

  @Context private HttpServletRequest servletRequest;

  @Override
  public Response toResponse(LoginRequiredException exception) {
    try {
      String redirect = URLEncoder.encode(servletRequest.getRequestURI(), "UTF-8");
      return Response.seeOther(new URI(servletRequest.getContextPath() + "/login?url=" + redirect)).build();
    } catch (URISyntaxException | UnsupportedEncodingException e) {
      return Response.serverError().build();
    }
  }
}
