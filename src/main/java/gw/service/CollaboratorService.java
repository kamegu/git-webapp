package gw.service;

import gw.core.action.Result;
import gw.model.Account;
import gw.model.Collaborator;
import gw.model.Repository;
import gw.model.pk.CollaboratorPK;
import gw.model.pk.RepositoryPK;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class CollaboratorService {
  @Inject
  private Provider<EntityManager> emProvider;

  public List<String> getNames(RepositoryPK repositoryPK) {
    List<Collaborator> collaborators = emProvider.get()
        .createNamedQuery("Collaborator.fetchGroupMembers", Collaborator.class)
        .setParameter("pk", repositoryPK)
        .getResultList();

    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    Set<String> allOwners = repository.getAllOwners();

    return collaborators.stream()
        .map(c -> c.getPk().getCollaboratorName())
        .filter(name -> !allOwners.contains(name))
        .collect(Collectors.toList());
  }

  /**
   * userName: not group account
   */
  @Transactional
  public Result<Collaborator> add(RepositoryPK repositoryPK, String userName) {
    Account account = emProvider.get().find(Account.class, userName);
    if (account == null) {
      return Result.error("not account");
    }
    if (account.isGroup()) {
      return Result.error("group account cannot be added");
    }

    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    Set<String> allOwners = repository.getAllOwners();
    if (allOwners.contains(userName)) {
      return Result.error(userName + " is owner");
    }

    Collaborator collaborator = emProvider.get().find(Collaborator.class, new CollaboratorPK(repositoryPK, userName));
    if (collaborator != null) {
      return Result.error(userName + " is already registered");
    }

    collaborator = new Collaborator();
    collaborator.setPk(new CollaboratorPK(repositoryPK, userName));
    emProvider.get().persist(collaborator);
    return Result.success(collaborator);
  }

  @Transactional
  public Result<String> delete(RepositoryPK repositoryPK, String userName) {
    Collaborator collaborator = emProvider.get().find(Collaborator.class, new CollaboratorPK(repositoryPK, userName));
    if (collaborator == null) {
      return Result.error(userName + " is not collaborator");
    }
    emProvider.get().remove(collaborator);
    return Result.success(userName);
  }
}
