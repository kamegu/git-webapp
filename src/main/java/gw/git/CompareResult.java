package gw.git;

import gw.git.FileListDto.CommitInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CompareResult {
  private final List<CommitInfo> commits;
  private final List<DiffInfo> diffs;
}
