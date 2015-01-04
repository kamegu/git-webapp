package gw.servlet;

import gw.core.LoginContext;
import gw.core.RepositoryContext;
import gw.model.UserAccount;
import gw.model.pk.RepositoryPK;
import gw.service.RepositoryController;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Provider;

import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.FileResolver;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

@WebServlet(urlPatterns = { "/git/*" }, initParams = { /* @WebInitParam(name = "base-path", value = "d:/dev2") */})
public class GitRepositoryServlet extends GitServlet {
  private static final long serialVersionUID = 1L;

  @Inject
  private Provider<EntityManager> emProvider;
  @Inject
  private RepositoryController repositoryController;

  @Override
  public void init(ServletConfig config) throws ServletException {
    String basePath = config.getInitParameter("base-path");
    File dir;
    if (basePath != null) {
      dir = new File(basePath);
    } else {
      dir = new File(System.getProperty("user.home"), ".gitapp/repos");
    }
    setRepositoryResolver(new FileResolver<HttpServletRequest>(dir, true));
    setReceivePackFactory(new GitReceivePackFactory());

    super.init(config);

    GuiceListener.get().injectMembers(this);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    System.out.println(request.getServletPath());
    System.out.println(request.getPathInfo());

    Optional<RepositoryPK> repositoryPK = parsePK(request);
    if (!repositoryPK.isPresent()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    gw.model.Repository repository = emProvider.get().find(gw.model.Repository.class, repositoryPK.get());

    if (repository == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    if (authRequired(request, repository)) {
      if (!enable(request, repository)) {
        response.setHeader("WWW-Authenticate", "BASIC realm=\"GitApp\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    super.service(req, res);
  }

  // instead of DefaultReceivePackFactory
  private static class GitReceivePackFactory implements ReceivePackFactory<HttpServletRequest> {
    @Override
    public ReceivePack create(HttpServletRequest req, Repository db) throws ServiceNotEnabledException, ServiceNotAuthorizedException {
      ReceivePack receivePack = new ReceivePack(db);

      GwReceiveHook hook = new GwReceiveHook(parsePK(req).get());
      GuiceListener.get().injectMembers(hook);
      receivePack.setPreReceiveHook(hook);
      receivePack.setPostReceiveHook(hook);
      return receivePack;
    }
  }

  private boolean authRequired(HttpServletRequest request, gw.model.Repository repository) {
    // case push
    if (request.getRequestURI().endsWith("/git-receive-pack") || "service=git-receive-pack".equals(request.getQueryString())) {
      return true;
    }

    // case private
    return repository.isPrivateRepo();
  }

  private boolean enable(HttpServletRequest request, gw.model.Repository repository) {
    String auth = request.getHeader("Authorization");
    if (auth == null || auth.trim().equals("")) {
      return false;
    }
    String[] array = new String(Base64.getDecoder().decode(auth.substring(6))).split(":");
    if (array.length != 2 || array[0].length() == 0 || array[1].length() == 0) {
      return false;
    }
    return checkAvailable(request, array[0], array[1], repository);
  }

  private boolean checkAvailable(HttpServletRequest request, String username, String password, gw.model.Repository repository) {
    UserAccount account = emProvider.get().find(UserAccount.class, username);
    if (account == null || !account.authenticate(password)) {
      return false;
    }

    request.setAttribute(LoginContext.SESSION_KEY, new LoginContext(account)); // for EntityOperator
    Optional<RepositoryContext> context = repositoryController.getContext(repository.getPk(), account.getName());
    return context.filter(c -> c.canAccess(true)).isPresent();
  }

  private static Optional<RepositoryPK> parsePK(HttpServletRequest request) {
    String[] paths = request.getPathInfo().split("/");
    if (paths.length < 3) {
      return Optional.empty();
    }

    String owner = paths[1];
    String repos = paths[2].replaceFirst("\\.wiki\\.git$|\\.git$", "");
    return Optional.of(new RepositoryPK(owner, repos));
  }
}
