package support.ui.utilities;

import java.util.Random;

/**
 * 字符串工具类
 */
public final class Strings {

  private Strings() {
    // No instances.
  }

  public static boolean isBlank(CharSequence string){
    return (string == null || string.toString().trim().length() == 0);
  }

  public static String valueOrDefault(String string, String defaultString) {
    return isBlank(string) ? defaultString : string;
  }

  public static String truncateAt(String string, int length) {
    return string.length() > length ? string.substring(0, length) : string;
  }

  /**
   * 生成随机字符串
   */
  public static String randomString(int length) {
    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int num = random.nextInt(62);
      buf.append(str.charAt(num));
    }
    return buf.toString();
  }


}
