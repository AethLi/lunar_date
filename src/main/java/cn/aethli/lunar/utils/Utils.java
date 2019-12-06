package cn.aethli.lunar.utils;

public class Utils {

    private Utils() {
    }

    private static final String[] LUNAR_HEADER = {"初", "十", "廿", "卅", "正", "腊", "冬", "闰"};
    private static final String[] NUMBER_CAPITAL = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    /**
     * 从数字转换成中文大写
     *
     * @param number 原数字
     * @return 中文大写
     */
    public static String getChineseNumber(int number) {
        StringBuilder builder = new StringBuilder();
        int bit = getBit(number);
        int temp = (int) Math.pow(10, bit - 1);
        for (int i = 0; i < bit; ++i) {
            int num = number / temp;
            builder.append(NUMBER_CAPITAL[num]);
            number %= temp;
            temp /= 10;
        }
        return builder.toString();
    }

    /**
     * 获取十进制的位数
     *
     * @param number 数
     * @return 位数
     */
    private static int getBit(int number) {
        int temp = 10;
        int bit = 1;
        while (number / temp != 0) {
            temp *= 10;
            ++bit;
        }
        return bit;
    }

    public static String lunarNumToStr(char[] c) {
        String result = "";
        if (c.length == 1) {
            for (int i = 1; i < 10; i++) {
                if (c[0] == String.valueOf(i).charAt(0)) {
                    result = LUNAR_HEADER[0] + NUMBER_CAPITAL[i];
                }
            }
        } else {
            if (c[0] == '1') {
                if (c[1] == '0') {
                    result = LUNAR_HEADER[0] + LUNAR_HEADER[1];
                } else {
                    for (int i = 1; i < 10; i++) {
                        if (c[1] == String.valueOf(i).charAt(0)) {
                            result = LUNAR_HEADER[1] + NUMBER_CAPITAL[i];
                        }
                    }
                }
            } else if (c[0] == '2') {
                if (c[1] == '0') {
                    result = LUNAR_HEADER[2] + LUNAR_HEADER[1];
                } else {
                    for (int i = 1; i < 10; i++) {
                        if (c[1] == String.valueOf(i).charAt(0)) {
                            result = LUNAR_HEADER[2] + NUMBER_CAPITAL[i];
                        }
                    }
                }
            } else {
                if (c[1] == '0') {
                    result = LUNAR_HEADER[3] + LUNAR_HEADER[1];
                } else {
                    for (int i = 1; i < 10; i++) {
                        if (c[1] == String.valueOf(i).charAt(0)) {
                            result = LUNAR_HEADER[3] + NUMBER_CAPITAL[i];
                        }
                    }
                }
            }
        }
        return result;
    }
}
