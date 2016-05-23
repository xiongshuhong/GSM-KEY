package irene.com.framework.util;

import java.text.DecimalFormat;

/**
 * Created by Irene on 2015/8/18.
 */
public class StringUtil {
    /**
     * 字符串为Null或者为""
     *
     * @param str
     * @return
     */
    public static boolean isEmptyOrNull(String str) {
        return str == null || "".equals(str);
    }


    /**
     * 字符串为Null或者为""
     *
     * @param str
     * @return
     */
    public static boolean isEmptyNull(String str) {
        return str == null || "".equals(str) || "0".equals(str);
    }

    /**
     * 字符串为Null或者为""
     *
     * @param str
     * @return
     */
    public static String returnEmptyNull(String str) {
        if (str == null || "".equals(str) || "0".equals(str)) {
            return "";
        } else {
            return str;
        }
    }


    /**
     * 比较两个字符串是否相等（两个同为NULL不相等，两个同为""相等）
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean compareString(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        } else {
            return str1.equals(str2);
        }
    }

    /**
     * 无效数据返回-1
     *
     * @param intValue
     * @return
     */
    public static int toInt(String intValue) {
        try {
            return Integer.parseInt(intValue);
        } catch (Exception e) {
            return -1;
        }
    }

    private static DecimalFormat mformat = new DecimalFormat("########.## ");

    /**
     * 格式化输出 "########.## "
     *
     * @param f
     * @return
     */
    public static String fromatString(double f) {
        try {
            return mformat.format(f);
        } catch (Exception e) {
            return "-1";
        }
    }


    /**
     * 判断各种空情况
     */
    public static boolean isEmpty(String str) {

        return trim(str).equals("") || trim(str).equals("null") ? true : false;
    }

      static String trim(String str) {
        if (str == null) {
            return "";
        }
        return str.trim();
    }

}
