package gw.git;

import gw.model.pk.RepositoryPK;

import lombok.Getter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public class RepositoryReader implements AutoCloseable {
  @Getter
  private Git git;
  @Getter
  private Repository repository;

  public RepositoryReader(RepositoryPK repositoryPK) {
    this.git = GitConfig.getGit(repositoryPK);
    this.repository = this.git.getRepository();
  }

  @Override
  public void close() {
    this.repository.close();
  }
}
