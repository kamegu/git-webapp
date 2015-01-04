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
public class LabelPK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Embedded
  private RepositoryPK repositoryPK;
  @Column(name = "LABEL_NAME")
  @Size(max = 64)
  private String labelName;
}
