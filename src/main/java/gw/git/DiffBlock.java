package gw.git;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DiffBlock {
  private boolean same = true;
  private int aStart;
  private int aEnd;
  private int bStart;
  private int bEnd;
  @Setter(value = AccessLevel.PROTECTED)
  private String header;
  private List<DiffLine> diffLines = new ArrayList<>();

  protected DiffBlock(int aStart, int bStart) {
    this.aStart = aStart;
    this.bStart = bStart;
  }

  protected void setEnd(boolean same, int aEnd, int bEnd) {
    this.same = same;
    this.aEnd = aEnd;
    this.bEnd = bEnd;
  }

  @Getter
  public static class DiffLine {
    private int type;
    @Setter(value = AccessLevel.PROTECTED)
    private Integer aLine;
    @Setter(value = AccessLevel.PROTECTED)
    private Integer bLine;
    private String text;

    public DiffLine(int type, String text) {
      this.type = type;
      this.text = text;
    }

    public String getTypeName() {
      if (type == 3) {
        return "context";
      } else if (type == 1) {
        return "deleted";
      } else if (type == 2) {
        return "added";
      }
      throw new RuntimeException();
    }
  }
}
