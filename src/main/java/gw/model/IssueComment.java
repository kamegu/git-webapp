package gw.model;

import gw.model.embed.UpdateInfo;
import gw.model.pk.IssuePK;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ISSUE_COMMENT")
@NamedQuery(name = "IssueComment.list", query = "SELECT c FROM IssueComment c WHERE c.issuePK=:issuePK ORDER BY c.id")
@Getter
@Setter
@ToString(of = { "id", "issuePK" })
public class IssueComment implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Embedded
  private IssuePK issuePK;

  @NotNull
  @Column(name = "ACTION_TYPE")
  private String actionType;

  @NotNull
  @Column(name = "COMMENT_ACCOUNT_NAME")
  private String commentAccountName;

  @Column(name = "CONTENT")
  private String content;

  @Embedded
  private UpdateInfo updateInfo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "ACCOUNT_NAME", referencedColumnName = "ACCOUNT_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "REPOSITORY_NAME", referencedColumnName = "REPOSITORY_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "ISSUE_ID", referencedColumnName = "ISSUE_ID", updatable = false, insertable = false)
  })
  private Issue issue;
}
