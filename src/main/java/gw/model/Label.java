package gw.model;

import gw.model.pk.LabelPK;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "LABEL")
@Getter @Setter
@ToString(of = { "pk" })
public class Label implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private LabelPK pk;

  @NotNull
  @Size(min = 7, max = 7)
  @Column(name = "COLOR")
  private String color;
}
