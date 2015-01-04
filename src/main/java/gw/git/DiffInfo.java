package gw.git;

import gw.git.DiffBlock.DiffLine;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DiffInfo {
  @JsonIgnore
  private DiffEntry diffEntry;
  @JsonIgnore
  private String oldPath;
  @JsonIgnore
  private String newPath;
  private ChangeType changeType;
  private boolean binary;
  // private String oldText;
  // private String newText;
  private List<DiffBlock> diffBlocks;

  public String getFileName() {
    if (changeType == ChangeType.RENAME) {
      return oldPath + " => " + newPath;
    } else if (changeType == ChangeType.DELETE) {
      return oldPath;
    }
    return newPath;
  }

  public static DiffInfo ofDiff(DiffEntry diffEntry, FileContent oldContent, FileContent newContent, List<DiffBlock> diffBlocks) {
    DiffInfo diffInfo = new DiffInfo();
    diffInfo.diffEntry = diffEntry;
    diffInfo.changeType = diffEntry.getChangeType();
    diffInfo.diffBlocks = diffBlocks;
    diffInfo.binary = (oldContent != null && oldContent.isBinary()) || (newContent != null && newContent.isBinary());
    if (oldContent != null) {
      diffInfo.oldPath = oldContent.getPath();
      // diffInfo.oldText = oldContent.getText();
    }
    if (newContent != null) {
      diffInfo.newPath = newContent.getPath();
      // diffInfo.newText = newContent.getText();
    }
    return diffInfo;
  }

  public static DiffInfo ofNewCommit(FileContent fileContent) {
      DiffInfo diffInfo = new DiffInfo(null, null, fileContent.getPath(), ChangeType.ADD,
          fileContent.isBinary(), /*null, fileContent.getText(),*/ null);
      if (!fileContent.isBinary()) {
        DiffBlock diffBlock = new DiffBlock(0, 0);
        String[] lines = fileContent.getText().split("\n");
        for (int i = 0; i < lines.length; i++) {
          DiffLine diffLine = new DiffLine(2, lines[i]);
          diffLine.setALine(0);
          diffLine.setBLine(i);
          diffBlock.getDiffLines().add(diffLine);
        }
        diffBlock.setEnd(false, 0, lines.length);
        diffInfo.diffBlocks = Arrays.asList(diffBlock);
      }
    return diffInfo;
  }
}
