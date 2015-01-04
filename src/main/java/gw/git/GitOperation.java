package gw.git;

import gw.core.LoginContext;
import gw.dto.repository.PullRequestEndpoint;
import gw.git.FileListDto.CommitInfo;
import gw.git.FileListDto.FileInfo;
import gw.model.PullRequest;
import gw.model.pk.IssuePK;
import gw.model.pk.RepositoryPK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.ws.rs.NotFoundException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoMergeBaseException;
import org.eclipse.jgit.errors.NoMergeBaseException.MergeBaseFailureReason;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitOperation {

  public boolean init(String owner, String repositoryName, String defaultBranch) {
    try {
      File dir = GitConfig.getDirectory(owner, repositoryName);
      Repository repository = new FileRepositoryBuilder().setGitDir(dir).setBare().build();
      repository.create(true);
      repository.close();
      return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(String owner, String repositoryName) {
    File dir = GitConfig.getDirectory(owner, repositoryName);
    try {
      FileUtils.deleteDirectory(dir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ObjectId getCommitId(RepositoryPK repositoryPK, String refName) {
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Repository repository = reader.getRepository();

      String branch = repository.getBranch();
      if (branch == null || repository.resolve(branch) == null) {
        return null;
      }
      return repository.resolve(refName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public RefPath separateRefAndPath(RepositoryPK repositoryPK, String refAndpath) {
    if (StringUtils.isBlank(refAndpath)) {
      throw new NotFoundException();
    }

    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Repository repository = reader.getRepository();

      Map<String, Ref> allRefs = repository.getAllRefs();
      if (allRefs.size() == 0) {
        throw new NotFoundException();
      }
      String[] paths = StringUtils.split(refAndpath, '/');
      String refName = IntStream.range(0, paths.length).mapToObj(i -> {
        return String.join("/", ArrayUtils.subarray(paths, 0, i+1));
      })
      .filter(refName1 -> {
        try {
          return repository.resolve(refName1) != null;
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      })
      .findFirst().orElseThrow(NotFoundException::new);

      String path = StringUtils.substring(refAndpath, refName.length() + 1);
      return new RefPath(refName, path);
    }
  }

  public FileListDto getFileList(RepositoryPK repositoryPK, RefPath refPath) {
    String refName = refPath.getRefName();
    String path = refPath.getPath();
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      String branch = repository.getBranch();
      if (branch == null || repository.resolve(branch) == null) {
        return null;
      }
      List<Ref> allBranch = git.branchList().call();
      List<Ref> allTag = git.tagList().call();
      Ref ref = repository.getRef(refName);
      ObjectId objectId = repository.resolve(refName);
      RevTree revTree = JGitUtils.getRevTree(repository, refName);

      CommitInfo commitInfo;
      List<FileInfo> files;
      if (path.length() > 0) {
        TreeWalk treeWalk = TreeWalk.forPath(repository, path, revTree);
        treeWalk.enterSubtree();
        files = getFileInfos(git, objectId, treeWalk);
        RevCommit commit = getFirstCommit(git, objectId, path);
        commitInfo = new CommitInfo(commit);
      } else {
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.reset(revTree);
        files = getFileInfos(git, objectId, treeWalk);
        RevCommit commit = getFirstCommit(git, objectId, null);
        commitInfo = new CommitInfo(commit);
      }

      files.sort(Comparator.comparing(FileInfo::isDirectory).reversed().thenComparing(FileInfo::getName));
      return new FileListDto(allBranch, allTag, ref, files, commitInfo);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  public FileContent getFileContent(RepositoryPK repositoryPK, RefPath refPath) {
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      RevTree revTree = JGitUtils.getRevTree(repository, refPath.getRefName());

      TreeWalk treeWalk = TreeWalk.forPath(repository, refPath.getPath(), revTree);

      ObjectId fileId = treeWalk.getObjectId(0);
      return getFileContent(git, refPath.getPath(), fileId);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<DiffInfo> getFileAsDiffFormat(RepositoryPK repositoryPK, String ref) {
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      ObjectId objectId = repository.resolve(ref);
      List<RevCommit> commits = getFirstCommits(git, objectId, null, 2);

      TreeWalk treeWalk = new TreeWalk(repository);
      List<DiffInfo> diffInfos = new ArrayList<>();
      treeWalk.addTree(commits.get(0).getTree());
      treeWalk.setRecursive(true);
      while (treeWalk.next()) {
        ObjectId fileId = treeWalk.getObjectId(0);
        if (!treeWalk.isSubtree()) {
          FileContent fileContent = getFileContent(git, treeWalk.getPathString(), fileId);
          diffInfos.add(DiffInfo.ofNewCommit(fileContent));
        }
      }
      return diffInfos;
    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean merge(String baseBranchName, IssuePK issuePK, String message, LoginContext loginContext) {
    String mergeMessage = PullRequest.getMergeMessage(issuePK, new PullRequestEndpoint(issuePK.getRepositoryPK(), baseBranchName))
        + "\n\n" + message;
    try (RepositoryReader reader = new RepositoryReader(issuePK.getRepositoryPK())) {
      Repository repository = reader.getRepository();

      String pullBranch = Constants.R_REFS + "pull/" + issuePK.getIssueId() + "/head";
      String baseBranchRefName = Constants.R_HEADS + baseBranchName;

      ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repository, true);
      ObjectInserter inserter = repository.newObjectInserter();

      try {
        ObjectId baseObjectId = repository.resolve(baseBranchRefName);
        ObjectId pullBranchObjectId = repository.resolve(pullBranch);

        boolean noError = merger.merge(baseObjectId, pullBranchObjectId);
        if (!noError) {
          throw new RuntimeException("cannot merge!!");
        }

        ObjectId resultTreeId = merger.getResultTreeId();

        PersonIdent personIdent = new PersonIdent(loginContext.getName(), loginContext.getEmail());
        CommitBuilder commit = new CommitBuilder();
        commit.setCommitter(personIdent);
        commit.setAuthor(personIdent);
        commit.setMessage(mergeMessage);
        commit.setParentIds(baseObjectId, pullBranchObjectId);
        commit.setTreeId(resultTreeId);
        ObjectId mergedCommitId = inserter.insert(commit);
        inserter.flush();

        RefUpdate refUpdate = repository.updateRef(baseBranchRefName);
        refUpdate.setNewObjectId(mergedCommitId);
        refUpdate.setForceUpdate(false);
        refUpdate.setRefLogIdent(personIdent);
        refUpdate.setRefLogMessage("merged", false);
        refUpdate.update();

        return true;
      } catch (RevisionSyntaxException | IOException e) {
        return false;
      } finally {
        inserter.release();
      }
    }
  }

  public boolean canMerge(RepositoryPK baseRepositoryPK, String baseBranchName, RepositoryPK compareRepositoryPK, String compareBranchName) {
    try (RepositoryReader reader = new RepositoryReader(compareRepositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      String mergeCheckBranch = Constants.R_REFS + "merge_check/" + UUID.randomUUID();
      RefSpec refSpec = new RefSpec().setSource(Constants.R_HEADS + baseBranchName).setDestination(mergeCheckBranch).setForceUpdate(true);

      ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(repository, true);
      try {
        ObjectId compareObjectId = repository.resolve(Constants.R_HEADS + compareBranchName);

        git.fetch().setRemote(GitConfig.getDirectory(baseRepositoryPK).toURI().toString()).setRefSpecs(refSpec).call();
        ObjectId mergeCheckObjectId = repository.resolve(mergeCheckBranch);

        boolean noError = merger.merge(mergeCheckObjectId, compareObjectId);

        return noError;
      } catch (RevisionSyntaxException | IOException | GitAPIException e) {
        return false;
      } finally {
        try {
          RefUpdate refUpdate = repository.updateRef(mergeCheckBranch);
          refUpdate.setForceUpdate(true);
          refUpdate.delete();
        } catch (IOException e) {
        }
      }
    }
  }

  public boolean updatePullRequestRefSpec(IssuePK issuePK, RepositoryPK reqRepoPK, String reqBranchName) {
    Git git = GitConfig.getGit(issuePK.getRepositoryPK());

    String pullBranch = Constants.R_REFS + "pull/" + issuePK.getIssueId() + "/head";
    RefSpec refSpec = new RefSpec().setSource(Constants.R_HEADS + reqBranchName)
        .setDestination(pullBranch)
        .setForceUpdate(true);
    try {
      git.fetch().setRemote(GitConfig.getDirectory(reqRepoPK).toURI().toString()).setRefSpecs(refSpec).call();
      return true;
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  public ObjectId getPullRequestBaseObjectId(IssuePK issuePK, String baseBranch) {
    String pullBranch = Constants.R_REFS + "pull/" + issuePK.getIssueId() + "/head";
    Optional<RevCommit> mergeBase = getMergeBase(issuePK.getRepositoryPK(), baseBranch, pullBranch);
    return mergeBase.get().getId();
  }

  protected Optional<RevCommit> getMergeBase(RepositoryPK repositoryPK, String ref1, String ref2) {
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Repository repository = reader.getRepository();

      RevWalk walk = new RevWalk(repository);
      walk.reset();
      walk.setRevFilter(RevFilter.MERGE_BASE);

      walk.markStart(getRevCommit(repository, walk, ref1));
      walk.markStart(getRevCommit(repository, walk, ref2));
      final RevCommit base = walk.next();
      if (base == null) {
        return Optional.empty();
      }
      final RevCommit base2 = walk.next();
      if (base2 != null) {
        throw new NoMergeBaseException(
            MergeBaseFailureReason.MULTIPLE_MERGE_BASES_NOT_SUPPORTED,
            MessageFormat.format(
                JGitText.get().multipleMergeBasesFor, ref1, ref2,
                base.name(), base2.name()));
      }

      return Optional.of(base);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * wrapper of git diff ref1..ref2
   */
  protected List<DiffInfo> diff(RepositoryPK repositoryPK, String ref1, String ref2) {
    try (RepositoryReader reader = new RepositoryReader(repositoryPK)) {
      Git git = reader.getGit();
      Repository repository = reader.getRepository();

      List<DiffEntry> entries = diff(git, ref1, ref2);

      List<DiffInfo> diffInfos = new ArrayList<>();
      for (DiffEntry entry : entries) {
        Optional<FileContent> oldContent = getFileContent(git, entry.getOldPath(), entry.getOldId());
        Optional<FileContent> newContent = getFileContent(git, entry.getNewPath(), entry.getNewId());

        CustomDiffFormatter diffFormatter = new CustomDiffFormatter(repository);
        List<DiffBlock> diffLineList = diffFormatter.diffFormat(entry);
        DiffInfo diffInfo = DiffInfo.ofDiff(entry, oldContent.orElse(null), newContent.orElse(null), diffLineList);
        diffInfos.add(diffInfo);
      }
      return diffInfos;
    } catch (RevisionSyntaxException | IOException e) {
      throw new RuntimeException(e);
    }
  }

/*
    //HEADの場合Refの形が違うので注意
    public List<Ref> getAllRefs(RepositoryPK repositoryPK) {
        Git git = getGit(repositoryPK);
        Repository repository = git.getRepository();
        Map<String, Ref> refMap = repository.getAllRefs();
        return refMap.entrySet().stream().map(Entry::getValue).collect(Collectors.toList());
    }
*/

  public List<Ref> getAllBranch(RepositoryPK repositoryPK) {
    Git git = GitConfig.getGit(repositoryPK);
    try {
      List<Ref> refs = git.branchList().call();
      return refs;
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * git diff ref1..ref2
   */
  private List<DiffEntry> diff(Git git, String ref1, String ref2) {
    Repository repository = git.getRepository();
    try {
      ObjectId oldTree = repository.resolve(ref1 + "^{tree}");
      ObjectId newTree = repository.resolve(ref2 + "^{tree}");
      ObjectReader reader = repository.newObjectReader();
      List<DiffEntry> entries = git.diff()
          .setNewTree(new CanonicalTreeParser(null, reader, newTree))
          .setOldTree(new CanonicalTreeParser(null, reader, oldTree))
          .call();
      return entries;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private RevCommit getRevCommit(Repository repository, RevWalk walk, String refName) throws IOException {
    ObjectId object1Id = repository.resolve(refName);
    return walk.lookupCommit(object1Id);
  }

  private List<FileInfo> getFileInfos(Git git, ObjectId objectId, TreeWalk treeWalk)
      throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException, NoHeadException, GitAPIException {

    List<FileInfo> files = new ArrayList<>();

    while (treeWalk.next()) {
      RevCommit commit = getFirstCommit(git, objectId, treeWalk.getPathString());
      CommitInfo commitInfo = new CommitInfo(commit);

      files.add(new FileInfo(treeWalk.getObjectId(0), treeWalk.isSubtree(), treeWalk.getNameString(), treeWalk.getPathString(), commitInfo));
    }
    return files;
  }

  private RevCommit getFirstCommit(Git git, ObjectId objectId, String path)
      throws MissingObjectException, IncorrectObjectTypeException, NoHeadException, GitAPIException {
    List<RevCommit> commits = getFirstCommits(git, objectId, path, 1);
    return commits.get(0);
  }

  private List<RevCommit> getFirstCommits(Git git, ObjectId objectId, String path, int length)
      throws MissingObjectException, IncorrectObjectTypeException, NoHeadException, GitAPIException {
    LogCommand command = git.log();
    if (objectId != null) {
      command.add(objectId);
    }
    if (StringUtils.isNotBlank(path)) {
      command.addPath(path);
    }
    Iterator<RevCommit> iterator = command.setMaxCount(length).call().iterator();
    List<RevCommit> list = new ArrayList<RevCommit>();
    for (int i = 0; i < length; i++) {
      if (iterator.hasNext()) {
        list.add(iterator.next());
      } else {
        break;
      }
    }
        
    return list;
  }

  private Optional<FileContent> getFileContent(Git git, String path, AbbreviatedObjectId abbreviatedObjectId) {
    if (abbreviatedObjectId == null || path.equals("/dev/null")) {
      return Optional.empty();
    }
    return Optional.of(getFileContent(git, path, abbreviatedObjectId.toObjectId()));
  }

  private FileContent getFileContent(Git git, String path, ObjectId objectId) {
    try {
      ObjectLoader loader = git.getRepository().open(objectId);
      if (RawText.isBinary(loader.openStream())) {
        return new FileContent(path, true, null);
      }
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      loader.copyTo(stream);
      return new FileContent(path, false, stream.toString());
    } catch (IOException e) {

    }
    return null;
  }
}
