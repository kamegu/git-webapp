package gw.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuePK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "ACCOUNT_NAME")
  @Size(max = 100)
  private String accountName;
  @Column(name = "REPOSITORY_NAME")
  @Size(max = 100)
  private String repositoryName;
  @Column(name = "ISSUE_ID")
  private long issueId;

  public RepositoryPK getRepositoryPK() {
    return new RepositoryPK(accountName, repositoryName);
  }
}
