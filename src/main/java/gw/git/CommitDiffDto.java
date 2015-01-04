package gw.git;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.eclipse.jgit.revwalk.RevCommit;

@AllArgsConstructor
@Getter
public class CommitDiffDto {
  private RevCommit commit;
  private List<DiffInfo> diffInfos;

}
