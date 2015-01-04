package gw.service;

import gw.core.LoginContext;
import gw.core.action.Result;
import gw.dto.form.RepositoryForm;
import gw.git.GitOperation;
import gw.model.Collaborator;
import gw.model.Repository;
import gw.model.pk.RepositoryPK;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

public class RepositoryService {

  @Inject private Provider<EntityManager> emProvider;
  @Inject private GitOperation gitOperation;

  public List<Repository> getRecentList() {
    return emProvider.get()
        .createQuery("SELECT r FROM Repository r WHERE r.privateRepo=false ORDER BY r.updateInfo.updatedTimestamp DESC", Repository.class)
        .setMaxResults(15)
        .getResultList();
  }

  public List<Repository> getMyList(String userName) {
    return emProvider.get()
        .createQuery("SELECT r FROM Repository r WHERE r.pk.accountName=:userName ORDER BY r.updateInfo.updatedTimestamp DESC", Repository.class)
        .setParameter("userName", userName)
        .setMaxResults(8)
        .getResultList();
  }

  public List<Repository> getCollaboratingList(String userName) {
    return emProvider.get()
        .createQuery("SELECT c FROM Collaborator c INNER JOIN FETCH c.repository r "
            + "WHERE c.pk.collaboratorName=:userName ORDER BY r.updateInfo.updatedTimestamp DESC", Collaborator.class)
        .setParameter("userName", userName)
        .setMaxResults(5)
        .getResultList()
        .stream()
        .map(c -> c.getRepository())
        .collect(Collectors.toList());
  }

  @Transactional
  public Result<Repository> createRepository(String userName, RepositoryForm repositoryForm) {
    if (!repositoryForm.getOwner().equals(userName)) {
      throw new ForbiddenException();
    }
    Repository check = emProvider.get().find(Repository.class, new RepositoryPK(userName, repositoryForm.getName()));
    if (check != null) {
      return Result.error("already exists");
    }

    Repository repository = new Repository();
    repository.setPk(new RepositoryPK(userName, repositoryForm.getName()));
    repository.setPrivateRepo(repositoryForm.isPrivateRepo());
    repository.setDescription(repositoryForm.getDescription());
    emProvider.get().persist(repository);

    gitOperation.init(userName, repository.getPk().getRepositoryName(), repository.getDefaultBranch());

    return Result.success(repository);
  }

  @Transactional
  public boolean deleteRepository(RepositoryPK repositoryPK, LoginContext context) {
    if (!repositoryPK.getAccountName().equals(context.getName())) {
      throw new ForbiddenException();
    }
    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    emProvider.get().remove(repository);

    gitOperation.delete(repositoryPK.getAccountName(), repositoryPK.getRepositoryName());

    return true;
  }

  public Repository find(RepositoryPK repositoryPK) {
    Repository repository = emProvider.get().find(Repository.class, repositoryPK);
    if (repository == null) {
      throw new NotFoundException();
    }

    return repository;
  }

  public List<Repository> getByOwner(String ownerName) {
    return emProvider.get().createNamedQuery("Repository.findByOwner", Repository.class)
        .setParameter("owner", ownerName)
        .getResultList();
  }
}
