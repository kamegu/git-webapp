package gw.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueLabelPK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Embedded
  private IssuePK issuePK;
  @Column(name = "LABEL_NAME")
  private String labelName;
}
