package cn.aethli.lunar;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 月历管理器
 */
class DPCManager {
  private static final Map<Integer, Map<Integer, LunarDate[][]>> DATE_CACHE = new WeakHashMap<>();

  private static volatile DPCManager sManager;

  /**
   * 获取月历管理器 Get DPCNCalendar manager
   *
   * @return 月历管理器
   */
  static DPCManager getInstance() {
    if (null == sManager) {
      synchronized (DPCManager.class) {
        if (sManager == null) {
          sManager = new DPCManager();
        }
      }
    }
    return sManager;
  }

  /**
   * 获取指定年月的日历对象数组
   *
   * @param year 公历年
   * @param month 公历月
   * @return 日历对象数组 该数组长度恒为6x7 如果某个下标对应无数据则填充为null
   */
  LunarDate[][] obtainDPInfo(int year, int month) {
    Map<Integer, LunarDate[][]> dataOfYear = DATE_CACHE.get(year);
    if (null != dataOfYear && dataOfYear.size() != 0) {
      LunarDate[][] dataOfMonth = dataOfYear.get(month);
      if (dataOfMonth != null) {
        return dataOfMonth;
      }
      dataOfMonth = buildDPInfo(year, month);
      dataOfYear.put(month, dataOfMonth);
      return dataOfMonth;
    }
    if (null == dataOfYear) {
      dataOfYear = new HashMap<>();
    }
    LunarDate[][] dataOfMonth = buildDPInfo(year, month);
    dataOfYear.put(month, dataOfMonth);
    DATE_CACHE.put(year, dataOfYear);
    return dataOfMonth;
  }

  private LunarDate[][] buildDPInfo(int year, int month) {
    LunarDate[][] info = new LunarDate[6][7];
    int[][] monthGregorian = DPCNCalendar.buildMonthGregorian(year, month);

    for (int i = 0; i < info.length; i++) {
      for (int j = 0; j < info[i].length; j++) {
        // 如果这天不存在
        if (monthGregorian[i][j] == DPCNCalendar.NOT_A_DAY) {
          continue;
        }
        LunarDate tmp = new LunarDate(year, month, monthGregorian[i][j]);
        info[i][j] = tmp;
      }
    }
    return info;
  }

  LunarDate getDPInfo(int year, int month, int day) {
    LunarDate[][] monthInfo = obtainDPInfo(year, month);
    for (LunarDate[] items : monthInfo) {
      for (LunarDate item : items) {
        if (item != null && item.getDay() == day) {
          return item;
        }
      }
    }
    throw new RuntimeException("This day is NOT FOUND!");
  }
}
