package gw.application.rs;

import gw.core.LoginContext;
import gw.dto.result.JsonResult;
import gw.dto.result.SuccessResult;
import gw.model.UserAccount;
import gw.service.LoginService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("login")
public class LoginResource {
  @Context private HttpServletRequest servletRequest;
  @Inject private LoginService loginService;

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public JsonResult login(LoginForm loginForm) {
    if (loginForm.loginid != "" && loginForm.password != null) {
      UserAccount userAccount = loginService.login(loginForm.loginid, loginForm.password);
      if (userAccount != null) {
        servletRequest.getSession(true).setAttribute(LoginContext.SESSION_KEY, new LoginContext(userAccount));

        return new SuccessResult<>("ok");
      }
    }
    return new SuccessResult<>("ng");
  }

  public static class LoginForm {
    public String loginid;
    public String password;
    public String url;
  }
}
