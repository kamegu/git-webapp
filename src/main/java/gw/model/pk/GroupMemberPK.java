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
public class GroupMemberPK implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "GROUP_NAME")
  @Size(max = 100)
  private String groupName;
  @Column(name = "MEMBER_NAME")
  @Size(max = 100)
  private String memberName;
}
