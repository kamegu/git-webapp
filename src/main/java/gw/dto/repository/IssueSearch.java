package gw.dto.repository;

import javax.ws.rs.QueryParam;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IssueSearch {
  @QueryParam("pull")
  private boolean pull;
}
