package gw.dto.repository;

import gw.model.Issue;
import gw.model.IssueComment;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.validator.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class IssueForm {
  @NotEmpty
  private String title;
  private String content;
  private String assignedTo;
  private List<String> labels;

  public void applyTo(Issue issue) {
    issue.setTitle(title);
    issue.setAssignedAccountName(assignedTo);
    // set label
  }

  public void applyTo(IssueComment issueComment) {
    issueComment.setContent(content);
  }
}
