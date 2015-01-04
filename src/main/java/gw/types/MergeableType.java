package gw.types;

public enum MergeableType {
  YET, ENABLE, DISABLE;

  public static MergeableType of(boolean canMerge) {
    return canMerge ? ENABLE : DISABLE;
  }
}
