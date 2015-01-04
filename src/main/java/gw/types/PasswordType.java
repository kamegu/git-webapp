package gw.types;

public enum PasswordType {
  PLAIN {
    @Override
    public String encrypt(String plain, String salt) {
      return plain;
    }
  };

  public abstract String encrypt(String plain, String salt);
}
