package gw.core;

import gw.model.Repository;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RepositoryContext implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String ATTR_NAME = RepositoryContext.class.getName();

  private final String ownerName;
  private final String name;
  private final boolean privateRepo;
  private final String description;
  private final String defaultBranchName;
  private final Set<String> allOwners;
  private final boolean isOwner;
  private final Set<String> allCollaborators;
  private final boolean isCollaborator;

  public RepositoryContext(Repository repository, Set<String> allOwners, Set<String> allCollaborators, String userName) {
    this(repository.getPk().getAccountName(), repository.getPk().getRepositoryName(), repository.isPrivateRepo(), repository.getDescription(),
        repository.getDefaultBranchName(), allOwners, allOwners.contains(userName), allCollaborators, allCollaborators.contains(userName));
  }

  public boolean canAccess(boolean collaboratorOnly) {
    if (collaboratorOnly) {
      return isCollaborator;
    } else {
      return isCollaborator || !privateRepo;
    }
  }

  public String getPath() {
    return ownerName + "/" + name;
  }
}
