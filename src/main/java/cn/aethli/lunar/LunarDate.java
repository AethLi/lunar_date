package cn.aethli.lunar;

import cn.aethli.lunar.utils.Utils;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * @author Termite
 * @device Hades
 * @date 2019-12-06 09:22
 */
public class LunarDate {
  private static final String[] MONTH_NAME = {
    "零", "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "冬", "腊"
  };
  LocalDate localDate;
  private int year, month, day;
  private Lunar lunar;

  LunarDate(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  /**
   * 获取 LunarCalendar 对象
   *
   * @param year 公历年
   * @param month 公立月
   * @param day 公立日
   * @return LunarCalender
   */
  public static LunarDate obtainCalendar(int year, int month, int day) {
    return DPCManager.getInstance().getDPInfo(year, month, day);
  }

  /**
   * 获取指定年月的日历对象数组
   *
   * @param year 公历年
   * @param month 公历月
   * @return 日历对象数组 该数组长度恒为6x7 如果某个下标对应无数据则填充为null
   */
  public static LunarDate[][] obtainCalendar(int year, int month) {
    return DPCManager.getInstance().obtainDPInfo(year, month);
  }

  public Lunar getLunar() {
    if (lunar == null) {
      lunar = DPCNCalendar.getLunar(year, month, day);
    }
    return lunar;
  }

  public LocalDate getDate() {
    return LocalDate.of(year, month - 1, day);
  }

  public long getMillis() {
    return getDate().atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
  }

  public int getDay() {
    return day;
  }

  public int getMonth() {
    return month;
  }

  public int getYear() {
    return year;
  }

  public String getLunarDay() {
    checkLunar();
    char[] c = String.valueOf(lunar.day).toCharArray();
    return Utils.lunarNumToStr(c);
  }

  private void checkLunar() {
    if (lunar == null) {
      lunar = getLunar();
    }
  }

  public String getLunarMonth() {
    checkLunar();
    return MONTH_NAME[lunar.month];
  }

  public String getLunarYear() {
    checkLunar();
    return Utils.getChineseNumber(lunar.year);
  }
}
