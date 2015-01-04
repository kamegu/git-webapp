package gw.git;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefPath {
  private final String refName;
  private final String path;

  public String getParentPath() {
    int idx = StringUtils.lastIndexOf(path, "/");
    if (idx < 0) {
      return null;
    }
    return StringUtils.substring(path, 0, idx);
  }
}
