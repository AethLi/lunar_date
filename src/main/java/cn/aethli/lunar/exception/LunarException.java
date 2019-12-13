package cn.aethli.lunar.exception;

/**
 * just extends Exception
 *
 * @author selcarpa
 **/
public class LunarException extends Exception {

  public LunarException() {
  }

  public LunarException(String message) {
    super(message);
  }

  public LunarException(String message, Throwable cause) {
    super(message, cause);
  }

  public LunarException(Throwable cause) {
    super(cause);
  }

  public LunarException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
