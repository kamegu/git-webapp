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
public class RepositoryPK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "ACCOUNT_NAME")
  @Size(max = 100)
  private String accountName;
  @Column(name = "REPOSITORY_NAME")
  @Size(max = 100)
  private String repositoryName;

  public String getPath() {
    return "/" + accountName + "/" + repositoryName;
  }
}
