package gw.core;

import gw.model.UserAccount;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginContext implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String SESSION_KEY = "USER";

  private final String name;
  private final String email;
  private final boolean admin;

  public LoginContext(UserAccount userAccount) {
    this(userAccount.getName(), userAccount.getMailAddress(), userAccount.isAdmin());
  }

  public static LoginContext get(HttpServletRequest servletRequest) {
    LoginContext context = (LoginContext) servletRequest.getAttribute(SESSION_KEY); // for GitRepositoryServlet
    if (context != null) {
      return context;
    }
    return (LoginContext) servletRequest.getSession().getAttribute(SESSION_KEY);
  }
}
