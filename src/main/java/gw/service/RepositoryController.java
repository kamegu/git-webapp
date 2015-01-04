package gw.service;

import gw.core.LoginContext;
import gw.core.RepositoryContext;
import gw.model.Collaborator;
import gw.model.Repository;
import gw.model.pk.RepositoryPK;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Provider;

public class RepositoryController {
  @Inject private Provider<EntityManager> emProvider;
  @Inject private Provider<HttpServletRequest> requestProvider;

  public Optional<RepositoryContext> getContext(RepositoryPK repositoryPK, String loginUser) {
    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    if (repository == null) {
      return Optional.empty();
    }

    Set<String> allCollaborators = getAllCollaborators(repository);

    RepositoryContext context = new RepositoryContext(repository, repository.getAllOwners(), allCollaborators, loginUser);
    return Optional.of(context);
  }

  public Optional<RepositoryContext> getContext(RepositoryPK repositoryPK) {
    LoginContext loginContext = LoginContext.get(requestProvider.get());
    String loginUser = Optional.ofNullable(loginContext).map(c -> c.getName()).orElse(null);

    return getContext(repositoryPK, loginUser);
  }

  /**
   * owners and collaborators
   */
  private Set<String> getAllCollaborators(Repository repository) {
    List<Collaborator> collaborators = emProvider.get()
        .createNamedQuery("Collaborator.fetchGroupMembers", Collaborator.class)
        .setParameter("pk", repository.getPk())
        .getResultList();
    Set<String> allCollaborators = new HashSet<>();
    allCollaborators.addAll(repository.getAllOwners());

    collaborators.stream().map(c -> c.getCollaborator())
    .forEach(account -> {
      if (account.isGroup()) {
        Set<String> members = new HashSet<>(account.getGroupMembers()).stream().map(m -> m.getPk().getMemberName()).collect(Collectors.toSet());
        allCollaborators.addAll(members);
      } else {
        allCollaborators.add(account.getName());
      }
    });

    return allCollaborators;
  }
}
