package gw.application.rs.repository;

import gw.core.auth.Repo;
import gw.dto.repository.PullRequestEndpoint;
import gw.git.DiffInfo;
import gw.git.FileListDto.CommitInfo;
import gw.git.GitApi;
import gw.git.GitOperation;
import gw.model.pk.RepositoryPK;
import gw.service.MergeService;
import gw.types.MergeableType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.hibernate.validator.constraints.NotEmpty;

@Repo
@Path("")
public class GitResource {
  @Inject private MergeService mergeService;
  @Inject private GitOperation gitOperation;
  @Inject private GitApi gitApi;
  

  @PathParam("userName")
  private String userName;
  @PathParam("repositoryName")
  private String repositoryName;

  private RepositoryPK getRepositoryPK() {
    return new RepositoryPK(userName, repositoryName);
  }

  @GET
  @Path("branchs")
  @Produces(MediaType.APPLICATION_JSON)
  public List<RefJson> getBranchs() {
    List<Ref> refs = gitOperation.getAllBranch(getRepositoryPK());
    return refs.stream().map(RefJson::fromRef).collect(Collectors.toList());
  }

  @GET
  @Path("mergecheck")
  @Produces(MediaType.APPLICATION_JSON)
  public Object mergeCheck(@QueryParam("baseBranch") String base, @QueryParam("compareBranch") String compare) {
    MergeableType mergeable = mergeService.getMeargeable(
        new PullRequestEndpoint(getRepositoryPK(), base),
        new PullRequestEndpoint(getRepositoryPK(), compare));
    Map<String, Object> data = new HashMap<>();
    data.put("mergeable", mergeable.name());
    return data;
  }

  /**
   * git log ref1 (case ref2 is null)
   * git log ref1..ref2 (case ref2 is not null)
   */
  @GET
  @Path("log")
  @Produces(MediaType.APPLICATION_JSON)
  public List<CommitInfo> getCommitLogs(@NotEmpty @QueryParam("ref1") String ref1, @QueryParam("ref2") String ref2, @QueryParam("n") Integer num) {
    if (StringUtils.isBlank(ref2)) {
      return gitApi.log(getRepositoryPK(), ref1, num);
    }
    return gitApi.log(getRepositoryPK(), ref1, ref2);
  }

  /**
   * git diff ref1..ref2
   */
  @GET
  @Path("diff")
  @Produces(MediaType.APPLICATION_JSON)
  public List<DiffInfo> getDiffResult(@QueryParam("ref1") String ref1, @QueryParam("ref2") String ref2) {
    List<DiffInfo> diffs = gitApi.diff(getRepositoryPK(), ref1, ref2);
    return diffs;
  }

  /**
   * git diff base...compare
   */
  @GET
  @Path("diff-merge-base")
  @Produces(MediaType.APPLICATION_JSON)
  public List<DiffInfo> getDiffMergeBase(@QueryParam("baseRef") String base, @QueryParam("compareRef") String compare) {
    List<DiffInfo> diffs = gitApi.diffMergeBase(getRepositoryPK(), base, compare);
    return diffs;
  }

  /**
   * get file contents as diff format
   * for initial commit
   */
  @GET
  @Path("filesAsDiff")
  @Produces(MediaType.APPLICATION_JSON)
  public List<DiffInfo> getFilesAsDiff(@QueryParam("ref") String ref) {
    List<DiffInfo> diffs = gitOperation.getFileAsDiffFormat(getRepositoryPK(), ref);
    return diffs;
  }

  @AllArgsConstructor
  @Getter
  public static class RefJson {
    private final String type; // branch, tag, commitId
    private final String name;

    public static RefJson fromRef(Ref ref) {
      if (ref.getName().startsWith(Constants.R_HEADS)) {
        return new RefJson("branch", ref.getName().substring(11));
      } else if (ref.getName().startsWith(Constants.R_TAGS)) {
        return new RefJson("tag", ref.getName().substring(10));
      }
      return new RefJson("", ref.getName());
    }
  }
}
