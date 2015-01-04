package gw.model;

import gw.model.embed.UpdateInfo;
import gw.model.pk.GroupMemberPK;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "GROUP_MEMBER")
@Getter @Setter
public class GroupMember implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private GroupMemberPK pk;

  @Column(name = "MANAGER")
  private boolean manager;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "GROUP_NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  private Account group;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "MEMBER_NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  private Account member;

  @Embedded
  private UpdateInfo updateInfo;
}
