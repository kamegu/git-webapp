package gw.model;

import gw.model.embed.UpdateInfo;
import gw.model.pk.MergeableHistoryPK;
import gw.types.MergeableType;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MERGEABLE_HISTORY")
@Getter @Setter
@ToString(of = { "pk" })
public class MergeableHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private MergeableHistoryPK pk;

  @NotNull
  @Column(name = "MERGEABLE_TYPE")
  private MergeableType mergeableType;

  @Embedded
  private UpdateInfo updateInfo;
}
