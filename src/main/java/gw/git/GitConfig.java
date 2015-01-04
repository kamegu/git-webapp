package gw.git;

import gw.model.pk.RepositoryPK;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.jgit.api.Git;

public class GitConfig {
  private static final String SEPARATOR = System.getProperty("file.separator");

  public static String getRootDirectory() {
    return System.getProperty("user.home") + SEPARATOR + ".gitapp" + SEPARATOR + "repos";
  }

  public static File getDirectory(String owner, String repository) {
    return new File(getRootDirectory() + SEPARATOR + owner + SEPARATOR + repository + ".git");
  }

  public static File getDirectory(RepositoryPK repositoryPK) {
    return getDirectory(repositoryPK.getAccountName(), repositoryPK.getRepositoryName());
  }

  public static File getTemporaryDirectory() {
    String name = UUID.randomUUID().toString();
    return new File(System.getProperty("user.home") + SEPARATOR + ".gitapp" + SEPARATOR + "repos_temp" + SEPARATOR + name + ".git");
  }

  public static Git getGit(RepositoryPK repositoryPK) {
    File dir = getDirectory(repositoryPK);
    try {
      Git git = Git.open(dir);
      return git;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
