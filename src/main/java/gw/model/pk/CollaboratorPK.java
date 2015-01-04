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
public class CollaboratorPK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Embedded
  private RepositoryPK repositoryPK;
  @Column(name = "COLLABORATOR_NAME")
  @Size(max = 100)
  private String collaboratorName; // only normal user(not group)
}
