package gw.git;

import gw.git.FileListDto.CommitInfo;
import gw.model.pk.RepositoryPK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class GitApi {
  @Inject private GitOperation gitOperation;

  /**
   * git log ref1
   */
  public List<CommitInfo> log(RepositoryPK repositoryPK, String ref, Integer num) {
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      ObjectId objectId = repository.resolve(ref);

      LogCommand command = git.log();
      if (objectId != null) {
        command.add(objectId);
      }
      if (num != null) {
        command.setMaxCount(num);
      }
      Iterable<RevCommit> commits = command.call();

      List<CommitInfo> commitInfos = new ArrayList<>();
      for (RevCommit commit : commits) {
        commitInfos.add(new CommitInfo(commit));
      }
          
      return commitInfos;
    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * git log ref1..ref2
   */
  public List<CommitInfo> log(RepositoryPK repositoryPK, String ref1, String ref2) {
    List<CommitInfo> commitInfos = new ArrayList<>();
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      ObjectId objectId1 = repository.resolve(ref1);
      ObjectId objectId2 = repository.resolve(ref2);
      Iterable<RevCommit> commits = git.log().addRange(objectId1, objectId2).call();
      for (RevCommit commit : commits) {
        commitInfos.add(new CommitInfo(commit));
      }
    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
    return commitInfos;
  }

  /**
   * git diff ref1...ref2
   */
  public List<DiffInfo> diffMergeBase(RepositoryPK repositoryPK, String base, String compare) {
    Optional<RevCommit> baseCommit = gitOperation.getMergeBase(repositoryPK, base, compare);
    if (!baseCommit.isPresent()) {
      return null;
    }
    return gitOperation.diff(repositoryPK, baseCommit.get().getName(), compare);
  }

  /**
   * git diff ref1..ref2
   */
  public List<DiffInfo> diff(RepositoryPK repositoryPK, String ref1, String ref2) {
    return gitOperation.diff(repositoryPK, ref1, ref2);
  }
}
