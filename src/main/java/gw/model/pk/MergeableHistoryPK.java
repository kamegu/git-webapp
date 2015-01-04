package gw.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MergeableHistoryPK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Embedded
  private RepositoryPK repositoryPK;

  @Column(name = "COMMIT_ID")
  @Size(min = 40, max = 40)
  private String commitId;
  @Column(name = "REQUEST_COMMIT_ID")
  @Size(min = 40, max = 40)
  private String requestCommitId;
}
