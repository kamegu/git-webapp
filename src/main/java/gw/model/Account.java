package gw.model;

import gw.model.embed.UpdateInfo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ACCOUNT")
@NamedQueries({
    @NamedQuery(name = "Account.findAll",
            query = "SELECT a FROM Account a LEFT JOIN FETCH a.userAccount u"),
    @NamedQuery(name = "Account.findNotDeleted",
            query = "SELECT a FROM Account a LEFT JOIN FETCH a.userAccount u WHERE a.deleted = false"),
    @NamedQuery(name = "Account.findIncluded",
            query = "SELECT a FROM Account a LEFT JOIN FETCH a.userAccount WHERE a.name IN :names"),
    })
@Getter @Setter
@ToString(of = { "name" })
public class Account implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "NAME")
  @NotNull
  @Size(max = 100)
  private String name;

  @Column(name = "URL")
  @Size(max = 200)
  private String url;

  @Column(name = "IMAGE")
  @Size(max = 100)
  private String image;

  @Column(name = "GROUP_ACCOUNT")
  @NotNull
  private boolean group;

  @Column(name = "DELETED")
  @NotNull
  private boolean deleted;

  @Embedded
  private UpdateInfo updateInfo;

  @OneToOne(fetch = FetchType.LAZY, mappedBy = "account")
  private UserAccount userAccount;

  @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
  private List<GroupMember> groupMembers;
}
