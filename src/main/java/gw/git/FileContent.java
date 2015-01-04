package gw.git;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileContent {
  private String path;
  private boolean binary;
  private String text;
}
