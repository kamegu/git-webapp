package gw.model;

import gw.model.pk.IssueLabelPK;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ISSUE_LABEL")
@NamedQueries({
    @NamedQuery(name = "IssueLabel.byLabel",
            query = "SELECT il FROM IssueLabel il WHERE il.pk.issuePK.accountName=:accountName "
                    + "AND il.pk.issuePK.repositoryName=:repoName AND il.pk.labelName=:name")
})
@Getter @Setter
@ToString(of = { "pk" })
public class IssueLabel implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private IssueLabelPK pk;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "ACCOUNT_NAME", referencedColumnName = "ACCOUNT_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "REPOSITORY_NAME", referencedColumnName = "REPOSITORY_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "ISSUE_ID", referencedColumnName = "ISSUE_ID", updatable = false, insertable = false)
  })
  private Issue issue;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
      @JoinColumn(name = "ACCOUNT_NAME", referencedColumnName = "ACCOUNT_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "REPOSITORY_NAME", referencedColumnName = "REPOSITORY_NAME", updatable = false, insertable = false),
      @JoinColumn(name = "LABEL_NAME", referencedColumnName = "LABEL_NAME", updatable = false, insertable = false)
  })
  private Label label;
}
