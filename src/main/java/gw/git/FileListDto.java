package gw.git;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

@AllArgsConstructor
@Getter
public class FileListDto {
  private List<Ref> allBranchs;
  private List<Ref> allTags;
  private Ref ref;
  private List<FileInfo> files = new ArrayList<>();
  private CommitInfo commit;

  public List<RefDto> getRefs() {
    List<RefDto> refs = new ArrayList<>(allBranchs).stream()
        .map(ref -> {
          String shortName = ref.getName().substring(11);
          return RefDto.newInstance("branch", ref.getName(), shortName, true, getRef());
        })
        .collect(Collectors.toList());
    Set<String> branchNames = new ArrayList<>(refs).stream()
        .map(ref -> ref.getShortName().split("/")[0])
        .collect(Collectors.toSet());
    List<RefDto> tags = new ArrayList<>(allTags).stream()
        .map(ref -> {
          String shortName = ref.getName().substring(10);
          boolean useShort = !branchNames.contains(shortName.split("/")[0]);
          return RefDto.newInstance("tag", ref.getName(), shortName, useShort, getRef());
        })
        .collect(Collectors.toList());
    refs.addAll(tags);
    return refs;
  }

  @Getter
  @AllArgsConstructor
  public static class FileInfo {
    private ObjectId id;
    private boolean directory;
    private String name;
    private String path;
    private CommitInfo commit;
  }

  @Getter
  @AllArgsConstructor
  public static class CommitInfo {
    private String commitId;
    private String message;
    private Date timestamp;
    private String author;
    private String mailAddress;
    private List<String> parentIds;

    public CommitInfo(RevCommit commit) {
      this(commit.getName(), commit.getShortMessage(), commit.getAuthorIdent().getWhen(),
          commit.getAuthorIdent().getName(), commit.getAuthorIdent().getEmailAddress(),
          getParentIds(commit));
    }

    public String getId(int length) {
      return StringUtils.substring(commitId, 0, length);
    }

    public String getLatest() {
      Date current = new Date();
      if (DateUtils.addMinutes(current, -1).before(timestamp)) {
        return (current.getTime() - timestamp.getTime()) / 1000 + " seconds ago";
      } else if (DateUtils.addHours(current, -1).before(timestamp)) {
        return (current.getTime() - timestamp.getTime()) / 1000 / 60 + " minutes ago";
      } else if (DateUtils.addDays(current, -1).before(timestamp)) {
        return (current.getTime() - timestamp.getTime()) / 1000 / 3600 + " hours ago";
      } else if (DateUtils.addMonths(current, -1).before(timestamp)) {
        return (current.getTime() - timestamp.getTime()) / 1000 / 3600 / 24 + " days ago";
      } else if (DateUtils.addYears(current, -1).before(timestamp)) {
        for (int m = 1; m < 12; m++) {
          if (DateUtils.addMonths(current, -m - 1).before(timestamp)) {
            return m + " months ago";
          }
        }
        return "? months ago";
      } else {

      }
      return "old";
    }

    private static List<String> getParentIds(RevCommit commit) {
      List<RevCommit> parents = Arrays.asList(commit.getParents());
      return Lists.transform(parents, parent -> parent.getId().getName());
    }
  }

  @Getter
  @AllArgsConstructor
  public static class RefDto {
    private String type;
    private boolean current;
    private String name;
    private String fullName;
    private String shortName;

    public static RefDto newInstance(String type, String fullName, String shortName, boolean useShort, Ref currentRef) {
      boolean c = currentRef != null && fullName.equals(currentRef.getName());
      return new RefDto(type, c, BooleanUtils.toString(useShort, shortName, fullName), fullName, shortName);
    }
  }
}
