package cn.aethli.lunar;

import cn.aethli.lunar.exception.LunarException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Calendar support range:
 * <p>      Calendar               Minimum             Maximum
 * <p>      Gregorian              1901/02/19          2101/01/28
 * <p>      LunarDate              1901/01/01          2100/12/29
 *
 * @author selcarpa
 **/
public class LunarDate implements Serializable {

  private static final long serialVersionUID = 0x123456789abcdL;

  private static final int MIN_LUNAR_YEAR = 1901;
  private static final int MAX_LUNAR_YEAR = 2100;
  private static final int MAX_DAY_IN_YEAR = 384;
  private static final int MAX_LUNAR_MONTH = 13;
  private static final int MAX_DAY_PER_MONTH = 30;
//  private static final LocalDate MIN_DATE = LocalDate.of(1901, 2, 19);
//  private static final LocalDate MAX_DATE = LocalDate.of(2101, 1, 28);

  private static final String[] DAY_HEADER = {"初", "十", "廿", "卅"};
  private static final String[] NUMBER = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
  private static final String[] MONTH_NAME = {"正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月",
      "十月", "冬月", "腊月"};
  private static final String LEAP_HEADER = "闰";
  private static final String[] YEAR_NAME = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
  /**
   * <p>Lmon:month of chinese NewYear
   * <p>Lday:day of chinese NewYear
   * <p>DaysPerMonth:days per month 1 for 30 days 0 for 29 days
   * <p>LM:leap Month
   */
  private static final int[][] YEAR_INFO =
      {
/*Y          LM Lmon Lday DaysPerMonth           D1   D2   D3   D4   D5   D6   D7   D8   D9   D10  D11  D12  D13  #Days
1901     */ {0, 2, 19, 0b0100101011100000}, /*   29   30   29   29   30   29   30   29   30   30   30   29        354
1902     */ {0, 2, 8, 0b1010010101110000},  /*   30   29   30   29   29   30   29   30   29   30   30   30        355
1903     */ {5, 1, 29, 0b0101001001101000}, /*   29   30   29   30   29   29   30   29   29   30   30   29   30   383
1904     */ {0, 2, 16, 0b1101001001100000}, /*   30   30   29   30   29   29   30   29   29   30   30   29        354
1905     */ {0, 2, 4, 0b1101100101010000},  /*   30   30   29   30   30   29   29   30   29   30   29   30        355
1906     */ {4, 1, 25, 0b0110101010101000}, /*   29   30   30   29   30   29   30   29   30   29   30   29   30   384
1907     */ {0, 2, 13, 0b0101011010100000}, /*   29   30   29   30   29   30   30   29   30   29   30   29        354
1908     */ {0, 2, 2, 0b1001101011010000},  /*   30   29   29   30   30   29   30   29   30   30   29   30        355
1909     */ {2, 1, 22, 0b0100101011101000}, /*   29   30   29   29   30   29   30   29   30   30   30   29   30   384
1910     */ {0, 2, 10, 0b0100101011100000}, /*   29   30   29   29   30   29   30   29   30   30   30   29        354
1911     */ {6, 1, 30, 0b1010010011011000}, /*   30   29   30   29   29   30   29   29   30   30   29   30   30   384
1912     */ {0, 2, 18, 0b1010010011010000}, /*   30   29   30   29   29   30   29   29   30   30   29   30        354
1913     */ {0, 2, 6, 0b1101001001010000},  /*   30   30   29   30   29   29   30   29   29   30   29   30        354
1914     */ {5, 1, 26, 0b1101010100101000}, /*   30   30   29   30   29   30   29   30   29   29   30   29   30   384
1915     */ {0, 2, 14, 0b1011010101000000}, /*   30   29   30   30   29   30   29   30   29   30   29   29        354
1916     */ {0, 2, 3, 0b1101011010100000},  /*   30   30   29   30   29   30   30   29   30   29   30   29        355
1917     */ {2, 1, 23, 0b1001011011010000}, /*   30   29   29   30   29   30   30   29   30   30   29   30   29   384
1918     */ {0, 2, 11, 0b1001010110110000}, /*   30   29   29   30   29   30   29   30   30   29   30   30        355
1919     */ {7, 2, 1, 0b0100100110111000},  /*   29   30   29   29   30   29   29   30   30   29   30   30   30   384
1920     */ {0, 2, 20, 0b0100100101110000}, /*   29   30   29   29   30   29   29   30   29   30   30   30        354
1921     */ {0, 2, 8, 0b1010010010110000},  /*   30   29   30   29   29   30   29   29   30   29   30   30        354
1922     */ {5, 1, 28, 0b1011001001011000}, /*   30   29   30   30   29   29   30   29   29   30   29   30   30   384
1923     */ {0, 2, 16, 0b0110101001010000}, /*   29   30   30   29   30   29   30   29   29   30   29   30        354
1924     */ {0, 2, 5, 0b0110110101000000},  /*   29   30   30   29   30   30   29   30   29   30   29   29        354
1925     */ {4, 1, 24, 0b1010110110101000}, /*   30   29   30   29   30   30   29   30   30   29   30   29   30   385
1926     */ {0, 2, 13, 0b0010101101100000}, /*   29   29   30   29   30   29   30   30   29   30   30   29        354
1927     */ {0, 2, 2, 0b1001010101110000},  /*   30   29   29   30   29   30   29   30   29   30   30   30        355
1928     */ {2, 1, 23, 0b0100100101111000}, /*   29   30   29   29   30   29   29   30   29   30   30   30   30   384
1929     */ {0, 2, 10, 0b0100100101110000}, /*   29   30   29   29   30   29   29   30   29   30   30   30        354
1930     */ {6, 1, 30, 0b0110010010110000}, /*   29   30   30   29   29   30   29   29   30   29   30   30   29   383
1931     */ {0, 2, 17, 0b1101010010100000}, /*   30   30   29   30   29   30   29   29   30   29   30   29        354
1932     */ {0, 2, 6, 0b1110101001010000},  /*   30   30   30   29   30   29   30   29   29   30   29   30        355
1933     */ {5, 1, 26, 0b0110110101001000}, /*   29   30   30   29   30   30   29   30   29   30   29   29   30   384
1934     */ {0, 2, 14, 0b0101101011010000}, /*   29   30   29   30   30   29   30   29   30   30   29   30        355
1935     */ {0, 2, 4, 0b0010101101100000},  /*   29   29   30   29   30   29   30   30   29   30   30   29        354
1936     */ {3, 1, 24, 0b1001001101110000}, /*   30   29   29   30   29   29   30   30   29   30   30   30   29   384
1937     */ {0, 2, 11, 0b1001001011100000}, /*   30   29   29   30   29   29   30   29   30   30   30   29        354
1938     */ {7, 1, 31, 0b1100100101101000}, /*   30   30   29   29   30   29   29   30   29   30   30   29   30   384
1939     */ {0, 2, 19, 0b1100100101010000}, /*   30   30   29   29   30   29   29   30   29   30   29   30        354
1940     */ {0, 2, 8, 0b1101010010100000},  /*   30   30   29   30   29   30   29   29   30   29   30   29        354
1941     */ {6, 1, 27, 0b1101101001010000}, /*   30   30   29   30   30   29   30   29   29   30   29   30   29   384
1942     */ {0, 2, 15, 0b1011010101010000}, /*   30   29   30   30   29   30   29   30   29   30   29   30        355
1943     */ {0, 2, 5, 0b0101011010100000},  /*   29   30   29   30   29   30   30   29   30   29   30   29        354
1944     */ {4, 1, 25, 0b1010101011011000}, /*   30   29   30   29   30   29   30   29   30   30   29   30   30   385
1945     */ {0, 2, 13, 0b0010010111010000}, /*   29   29   30   29   29   30   29   30   30   30   29   30        354
1946     */ {0, 2, 2, 0b1001001011010000},  /*   30   29   29   30   29   29   30   29   30   30   29   30        354
1947     */ {2, 1, 22, 0b1100100101011000}, /*   30   30   29   29   30   29   29   30   29   30   29   30   30   384
1948     */ {0, 2, 10, 0b1010100101010000}, /*   30   29   30   29   30   29   29   30   29   30   29   30        354
1949     */ {7, 1, 29, 0b1011010010101000}, /*   30   29   30   30   29   30   29   29   30   29   30   29   30   384
1950     */ {0, 2, 17, 0b0110110010100000}, /*   29   30   30   29   30   30   29   29   30   29   30   29        354
1951     */ {0, 2, 6, 0b1011010101010000},  /*   30   29   30   30   29   30   29   30   29   30   29   30        355
1952     */ {5, 1, 27, 0b0101010110101000}, /*   29   30   29   30   29   30   29   30   30   29   30   29   30   384
1953     */ {0, 2, 14, 0b0100110110100000}, /*   29   30   29   29   30   30   29   30   30   29   30   29        354
1954     */ {0, 2, 3, 0b1010010110110000},  /*   30   29   30   29   29   30   29   30   30   29   30   30        355
1955     */ {3, 1, 24, 0b0101001010111000}, /*   29   30   29   30   29   29   30   29   30   29   30   30   30   384
1956     */ {0, 2, 12, 0b0101001010110000}, /*   29   30   29   30   29   29   30   29   30   29   30   30        354
1957     */ {8, 1, 31, 0b1010100101010000}, /*   30   29   30   29   30   29   29   30   29   30   29   30   29   383
1958     */ {0, 2, 18, 0b1110100101010000}, /*   30   30   30   29   30   29   29   30   29   30   29   30        355
1959     */ {0, 2, 8, 0b0110101010100000},  /*   29   30   30   29   30   29   30   29   30   29   30   29        354
1960     */ {6, 1, 28, 0b1010110101010000}, /*   30   29   30   29   30   30   29   30   29   30   29   30   29   384
1961     */ {0, 2, 15, 0b1010101101010000}, /*   30   29   30   29   30   29   30   30   29   30   29   30        355
1962     */ {0, 2, 5, 0b0100101101100000},  /*   29   30   29   29   30   29   30   30   29   30   30   29        354
1963     */ {4, 1, 25, 0b1010010101110000}, /*   30   29   30   29   29   30   29   30   29   30   30   30   29   384
1964     */ {0, 2, 13, 0b1010010101110000}, /*   30   29   30   29   29   30   29   30   29   30   30   30        355
1965     */ {0, 2, 2, 0b0101001001100000},  /*   29   30   29   30   29   29   30   29   29   30   30   29        353
1966     */ {3, 1, 21, 0b1110100100110000}, /*   30   30   30   29   30   29   29   30   29   29   30   30   29   384
1967     */ {0, 2, 9, 0b1101100101010000},  /*   30   30   29   30   30   29   29   30   29   30   29   30        355
1968     */ {7, 1, 30, 0b0101101010101000}, /*   29   30   29   30   30   29   30   29   30   29   30   29   30   384
1969     */ {0, 2, 17, 0b0101011010100000}, /*   29   30   29   30   29   30   30   29   30   29   30   29        354
1970     */ {0, 2, 6, 0b1001011011010000},  /*   30   29   29   30   29   30   30   29   30   30   29   30        355
1971     */ {5, 1, 27, 0b0100101011101000}, /*   29   30   29   29   30   29   30   29   30   30   30   29   30   384
1972     */ {0, 2, 15, 0b0100101011010000}, /*   29   30   29   29   30   29   30   29   30   30   29   30        354
1973     */ {0, 2, 3, 0b1010010011010000},  /*   30   29   30   29   29   30   29   29   30   30   29   30        354
1974     */ {4, 1, 23, 0b1101001001101000}, /*   30   30   29   30   29   29   30   29   29   30   30   29   30   384
1975     */ {0, 2, 11, 0b1101001001010000}, /*   30   30   29   30   29   29   30   29   29   30   29   30        354
1976     */ {8, 1, 31, 0b1101010100101000}, /*   30   30   29   30   29   30   29   30   29   29   30   29   30   384
1977     */ {0, 2, 18, 0b1011010101000000}, /*   30   29   30   30   29   30   29   30   29   30   29   29        354
1978     */ {0, 2, 7, 0b1011011010100000},  /*   30   29   30   30   29   30   30   29   30   29   30   29        355
1979     */ {6, 1, 28, 0b1001011011010000}, /*   30   29   29   30   29   30   30   29   30   30   29   30   29   384
1980     */ {0, 2, 16, 0b1001010110110000}, /*   30   29   29   30   29   30   29   30   30   29   30   30        355
1981     */ {0, 2, 5, 0b0100100110110000},  /*   29   30   29   29   30   29   29   30   30   29   30   30        354
1982     */ {4, 1, 25, 0b1010010010111000}, /*   30   29   30   29   29   30   29   29   30   29   30   30   30   384
1983     */ {0, 2, 13, 0b1010010010110000}, /*   30   29   30   29   29   30   29   29   30   29   30   30        354
1984     */ {10, 2, 2, 0b1011001001011000}, /*   30   29   30   30   29   29   30   29   29   30   29   30   30   384
1985     */ {0, 2, 20, 0b0110101001010000}, /*   29   30   30   29   30   29   30   29   29   30   29   30        354
1986     */ {0, 2, 9, 0b0110110101000000},  /*   29   30   30   29   30   30   29   30   29   30   29   29        354
1987     */ {6, 1, 29, 0b1010110110100000}, /*   30   29   30   29   30   30   29   30   30   29   30   29   29   384
1988     */ {0, 2, 17, 0b1010101101100000}, /*   30   29   30   29   30   29   30   30   29   30   30   29        355
1989     */ {0, 2, 6, 0b1001010101110000},  /*   30   29   29   30   29   30   29   30   29   30   30   30        355
1990     */ {5, 1, 27, 0b0100100101111000}, /*   29   30   29   29   30   29   29   30   29   30   30   30   30   384
1991     */ {0, 2, 15, 0b0100100101110000}, /*   29   30   29   29   30   29   29   30   29   30   30   30        354
1992     */ {0, 2, 4, 0b0110010010110000},  /*   29   30   30   29   29   30   29   29   30   29   30   30        354
1993     */ {3, 1, 23, 0b0110101001010000}, /*   29   30   30   29   30   29   30   29   29   30   29   30   29   383
1994     */ {0, 2, 10, 0b1110101001010000}, /*   30   30   30   29   30   29   30   29   29   30   29   30        355
1995     */ {8, 1, 31, 0b0110101100101000}, /*   29   30   30   29   30   29   30   30   29   29   30   29   30   384
1996     */ {0, 2, 19, 0b0101101011000000}, /*   29   30   29   30   30   29   30   29   30   30   29   29        354
1997     */ {0, 2, 7, 0b1010101101100000},  /*   30   29   30   29   30   29   30   30   29   30   30   29        355
1998     */ {5, 1, 28, 0b1001001101101000}, /*   30   29   29   30   29   29   30   30   29   30   30   29   30   384
1999     */ {0, 2, 16, 0b1001001011100000}, /*   30   29   29   30   29   29   30   29   30   30   30   29        354
2000     */ {0, 2, 5, 0b1100100101100000},  /*   30   30   29   29   30   29   29   30   29   30   30   29        354
2001     */ {4, 1, 24, 0b1101010010101000}, /*   30   30   29   30   29   30   29   29   30   29   30   29   30   384
2002     */ {0, 2, 12, 0b1101010010100000}, /*   30   30   29   30   29   30   29   29   30   29   30   29        354
2003     */ {0, 2, 1, 0b1101101001010000},  /*   30   30   29   30   30   29   30   29   29   30   29   30        355
2004     */ {2, 1, 22, 0b0101101010101000}, /*   29   30   29   30   30   29   30   29   30   29   30   29   30   384
2005     */ {0, 2, 9, 0b0101011010100000},  /*   29   30   29   30   29   30   30   29   30   29   30   29        354
2006     */ {7, 1, 29, 0b1010101011011000}, /*   30   29   30   29   30   29   30   29   30   30   29   30   30   385
2007     */ {0, 2, 18, 0b0010010111010000}, /*   29   29   30   29   29   30   29   30   30   30   29   30        354
2008     */ {0, 2, 7, 0b1001001011010000},  /*   30   29   29   30   29   29   30   29   30   30   29   30        354
2009     */ {5, 1, 26, 0b1100100101011000}, /*   30   30   29   29   30   29   29   30   29   30   29   30   30   384
2010     */ {0, 2, 14, 0b1010100101010000}, /*   30   29   30   29   30   29   29   30   29   30   29   30        354
2011     */ {0, 2, 3, 0b1011010010100000},  /*   30   29   30   30   29   30   29   29   30   29   30   29        354
2012     */ {4, 1, 23, 0b1011010101010000}, /*   30   29   30   30   29   30   29   30   29   30   29   30   29   384
2013     */ {0, 2, 10, 0b1010110101010000}, /*   30   29   30   29   30   30   29   30   29   30   29   30        355
2014     */ {9, 1, 31, 0b0101010110101000}, /*   29   30   29   30   29   30   29   30   30   29   30   29   30   384
2015     */ {0, 2, 19, 0b0100101110100000}, /*   29   30   29   29   30   29   30   30   30   29   30   29        354
2016     */ {0, 2, 8, 0b1010010110110000},  /*   30   29   30   29   29   30   29   30   30   29   30   30        355
2017     */ {6, 1, 28, 0b0101001010111000}, /*   29   30   29   30   29   29   30   29   30   29   30   30   30   384
2018     */ {0, 2, 16, 0b0101001010110000}, /*   29   30   29   30   29   29   30   29   30   29   30   30        354
2019     */ {0, 2, 5, 0b1010100100110000},  /*   30   29   30   29   30   29   29   30   29   29   30   30        354
2020     */ {4, 1, 25, 0b0111010010101000}, /*   29   30   30   30   29   30   29   29   30   29   30   29   30   384
2021     */ {0, 2, 12, 0b0110101010100000}, /*   29   30   30   29   30   29   30   29   30   29   30   29        354
2022     */ {0, 2, 1, 0b1010110101010000},  /*   30   29   30   29   30   30   29   30   29   30   29   30        355
2023     */ {2, 1, 22, 0b0100110110101000}, /*   29   30   29   29   30   30   29   30   30   29   30   29   30   384
2024     */ {0, 2, 10, 0b0100101101100000}, /*   29   30   29   29   30   29   30   30   29   30   30   29        354
2025     */ {6, 1, 29, 0b1010010101110000}, /*   30   29   30   29   29   30   29   30   29   30   30   30   29   384
2026     */ {0, 2, 17, 0b1010010011100000}, /*   30   29   30   29   29   30   29   29   30   30   30   29        354
2027     */ {0, 2, 6, 0b1101001001100000},  /*   30   30   29   30   29   29   30   29   29   30   30   29        354
2028     */ {5, 1, 26, 0b1110100100110000}, /*   30   30   30   29   30   29   29   30   29   29   30   30   29   384
2029     */ {0, 2, 13, 0b1101010100110000}, /*   30   30   29   30   29   30   29   30   29   29   30   30        355
2030     */ {0, 2, 3, 0b0101101010100000},  /*   29   30   29   30   30   29   30   29   30   29   30   29        354
2031     */ {3, 1, 23, 0b0110101101010000}, /*   29   30   30   29   30   29   30   30   29   30   29   30   29   384
2032     */ {0, 2, 11, 0b1001011011010000}, /*   30   29   29   30   29   30   30   29   30   30   29   30        355
2033     */ {11, 1, 31, 0b0100101011101000},/*   29   30   29   29   30   29   30   29   30   30   30   29   30   384
2034     */ {0, 2, 19, 0b0100101011010000}, /*   29   30   29   29   30   29   30   29   30   30   29   30        354
2035     */ {0, 2, 8, 0b1010010011010000},  /*   30   29   30   29   29   30   29   29   30   30   29   30        354
2036     */ {6, 1, 28, 0b1101001001011000}, /*   30   30   29   30   29   29   30   29   29   30   29   30   30   384
2037     */ {0, 2, 15, 0b1101001001010000}, /*   30   30   29   30   29   29   30   29   29   30   29   30        354
2038     */ {0, 2, 4, 0b1101010100100000},  /*   30   30   29   30   29   30   29   30   29   29   30   29        354
2039     */ {5, 1, 24, 0b1101101010100000}, /*   30   30   29   30   30   29   30   29   30   29   30   29   29   384
2040     */ {0, 2, 12, 0b1011010110100000}, /*   30   29   30   30   29   30   29   30   30   29   30   29        355
2041     */ {0, 2, 1, 0b0101011011010000},  /*   29   30   29   30   29   30   30   29   30   30   29   30        355
2042     */ {2, 1, 22, 0b0100101011011000}, /*   29   30   29   29   30   29   30   29   30   30   29   30   30   384
2043     */ {0, 2, 10, 0b0100100110110000}, /*   29   30   29   29   30   29   29   30   30   29   30   30        354
2044     */ {7, 1, 30, 0b1010010010111000}, /*   30   29   30   29   29   30   29   29   30   29   30   30   30   384
2045     */ {0, 2, 17, 0b1010010010110000}, /*   30   29   30   29   29   30   29   29   30   29   30   30        354
2046     */ {0, 2, 6, 0b1010101001010000},  /*   30   29   30   29   30   29   30   29   29   30   29   30        354
2047     */ {5, 1, 26, 0b1011010100101000}, /*   30   29   30   30   29   30   29   30   29   29   30   29   30   384
2048     */ {0, 2, 14, 0b0110110100100000}, /*   29   30   30   29   30   30   29   30   29   29   30   29        354
2049     */ {0, 2, 2, 0b1010110110100000},  /*   30   29   30   29   30   30   29   30   30   29   30   29        355
2050     */ {3, 1, 23, 0b0101010110110000}, /*   29   30   29   30   29   30   29   30   30   29   30   30   29   384
2051     */ {0, 2, 11, 0b1001001101110000}, /*   30   29   29   30   29   29   30   30   29   30   30   30        355
2052     */ {8, 2, 1, 0b0100100101111000},  /*   29   30   29   29   30   29   29   30   29   30   30   30   30   384
2053     */ {0, 2, 19, 0b0100100101110000}, /*   29   30   29   29   30   29   29   30   29   30   30   30        354
2054     */ {0, 2, 8, 0b0110010010110000},  /*   29   30   30   29   29   30   29   29   30   29   30   30        354
2055     */ {6, 1, 28, 0b0110101001010000}, /*   29   30   30   29   30   29   30   29   29   30   29   30   29   383
2056     */ {0, 2, 15, 0b1110101001010000}, /*   30   30   30   29   30   29   30   29   29   30   29   30        355
2057     */ {0, 2, 4, 0b0110101010100000},  /*   29   30   30   29   30   29   30   29   30   29   30   29        354
2058     */ {4, 1, 24, 0b1010101101100000}, /*   30   29   30   29   30   29   30   30   29   30   30   29   29   384
2059     */ {0, 2, 12, 0b1010101011100000}, /*   30   29   30   29   30   29   30   29   30   30   30   29        355
2060     */ {0, 2, 2, 0b1001001011100000},  /*   30   29   29   30   29   29   30   29   30   30   30   29        354
2061     */ {3, 1, 21, 0b1100100101110000}, /*   30   30   29   29   30   29   29   30   29   30   30   30   29   384
2062     */ {0, 2, 9, 0b1100100101100000},  /*   30   30   29   29   30   29   29   30   29   30   30   29        354
2063     */ {7, 1, 29, 0b1101010010101000}, /*   30   30   29   30   29   30   29   29   30   29   30   29   30   384
2064     */ {0, 2, 17, 0b1101010010100000}, /*   30   30   29   30   29   30   29   29   30   29   30   29        354
2065     */ {0, 2, 5, 0b1101101001010000},  /*   30   30   29   30   30   29   30   29   29   30   29   30        355
2066     */ {5, 1, 26, 0b0101101010101000}, /*   29   30   29   30   30   29   30   29   30   29   30   29   30   384
2067     */ {0, 2, 14, 0b0101011010100000}, /*   29   30   29   30   29   30   30   29   30   29   30   29        354
2068     */ {0, 2, 3, 0b1010011011010000},  /*   30   29   30   29   29   30   30   29   30   30   29   30        355
2069     */ {4, 1, 23, 0b0101001011101000}, /*   29   30   29   30   29   29   30   29   30   30   30   29   30   384
2070     */ {0, 2, 11, 0b0101001011010000}, /*   29   30   29   30   29   29   30   29   30   30   29   30        354
2071     */ {8, 1, 31, 0b1010100101011000}, /*   30   29   30   29   30   29   29   30   29   30   29   30   30   384
2072     */ {0, 2, 19, 0b1010100101010000}, /*   30   29   30   29   30   29   29   30   29   30   29   30        354
2073     */ {0, 2, 7, 0b1011010010100000},  /*   30   29   30   30   29   30   29   29   30   29   30   29        354
2074     */ {6, 1, 27, 0b1011010101010000}, /*   30   29   30   30   29   30   29   30   29   30   29   30   29   384
2075     */ {0, 2, 15, 0b1010110101010000}, /*   30   29   30   29   30   30   29   30   29   30   29   30        355
2076     */ {0, 2, 5, 0b0101010110100000},  /*   29   30   29   30   29   30   29   30   30   29   30   29        354
2077     */ {4, 1, 24, 0b1010010111010000}, /*   30   29   30   29   29   30   29   30   30   30   29   30   29   384
2078     */ {0, 2, 12, 0b1010010110110000}, /*   30   29   30   29   29   30   29   30   30   29   30   30        355
2079     */ {0, 2, 2, 0b0101001010110000},  /*   29   30   29   30   29   29   30   29   30   29   30   30        354
2080     */ {3, 1, 22, 0b1010100100111000}, /*   30   29   30   29   30   29   29   30   29   29   30   30   30   384
2081     */ {0, 2, 9, 0b0110100100110000},  /*   29   30   30   29   30   29   29   30   29   29   30   30        354
2082     */ {7, 1, 29, 0b0111001010011000}, /*   29   30   30   30   29   29   30   29   30   29   29   30   30   384
2083     */ {0, 2, 17, 0b0110101010100000}, /*   29   30   30   29   30   29   30   29   30   29   30   29        354
2084     */ {0, 2, 6, 0b1010110101010000},  /*   30   29   30   29   30   30   29   30   29   30   29   30        355
2085     */ {5, 1, 26, 0b0100110110101000}, /*   29   30   29   29   30   30   29   30   30   29   30   29   30   384
2086     */ {0, 2, 14, 0b0100101101100000}, /*   29   30   29   29   30   29   30   30   29   30   30   29        354
2087     */ {0, 2, 3, 0b1010010101110000},  /*   30   29   30   29   29   30   29   30   29   30   30   30        355
2088     */ {4, 1, 24, 0b0101001001110000}, /*   29   30   29   30   29   29   30   29   29   30   30   30   29   383
2089     */ {0, 2, 10, 0b1101000101100000}, /*   30   30   29   30   29   29   29   30   29   30   30   29        354
2090     */ {8, 1, 30, 0b1110100100110000}, /*   30   30   30   29   30   29   29   30   29   29   30   30   29   384
2091     */ {0, 2, 18, 0b1101010100100000}, /*   30   30   29   30   29   30   29   30   29   29   30   29        354
2092     */ {0, 2, 7, 0b1101101010100000},  /*   30   30   29   30   30   29   30   29   30   29   30   29        355
2093     */ {6, 1, 27, 0b0110101101010000}, /*   29   30   30   29   30   29   30   30   29   30   29   30   29   384
2094     */ {0, 2, 15, 0b0101011011010000}, /*   29   30   29   30   29   30   30   29   30   30   29   30        355
2095     */ {0, 2, 5, 0b0100101011100000},  /*   29   30   29   29   30   29   30   29   30   30   30   29        354
2096     */ {4, 1, 25, 0b1010010011101000}, /*   30   29   30   29   29   30   29   29   30   30   30   29   30   384
2097     */ {0, 2, 12, 0b1010001011010000}, /*   30   29   30   29   29   29   30   29   30   30   29   30        354
2098     */ {0, 2, 1, 0b1101000101010000},  /*   30   30   29   30   29   29   29   30   29   30   29   30        354
2099     */ {2, 1, 21, 0b1101100100101000}, /*   30   30   29   30   30   29   29   30   29   29   30   29   30   384
2100     */ {0, 2, 9, 0b1101010100100000},  /*   30   30   29   30   29   30   29   30   29   29   30   29        354
   */};
  private String yearName;
  private String monthName;
  private String dayName;
  private int year;
  private int month;
  private int day;
  private LeapType leapType;
  private LocalDate gregorianDate;

  private LunarDate(int year, int month, int day, LeapType leapType) {
    this.year = year;
    this.month = month - 1;
    this.day = day;
    this.leapType = leapType;
    int[] thisYearInfo = YEAR_INFO[year - MIN_LUNAR_YEAR];
    int[] daysPerMonth = toBinaryInts(thisYearInfo[3]);
    LocalDate thisChineseNewYear = LocalDate
        .of(year, thisYearInfo[1], thisYearInfo[2]);
    int dayCount = 0;
    for (int i = 1; i <= month; i++) {
      if (i < month) {
        dayCount = dayCount + daysPerMonth[i] == 0 ? 29 : 30;
      } else {
        dayCount = dayCount + day;
      }
    }
    gregorianDate = thisChineseNewYear.plusDays(dayCount);
    updateLunarName(thisYearInfo);
  }

  private LunarDate(LocalDate gregorianDate) throws LunarException {
    this.gregorianDate = gregorianDate;
    int[] thisYearInfo = YEAR_INFO[gregorianDate.getYear() - MIN_LUNAR_YEAR];
    //get Chinese New Year of target date
    LocalDate thisChineseNewYear = LocalDate
        .of(gregorianDate.getYear(), thisYearInfo[1], thisYearInfo[2]);
    if (thisChineseNewYear.isAfter(gregorianDate)) {
      thisYearInfo = YEAR_INFO[gregorianDate.getYear() - MIN_LUNAR_YEAR - 1];
      thisChineseNewYear = LocalDate
          .of(gregorianDate.getYear() - 1, thisYearInfo[1], thisYearInfo[2]);
    }
    long between = ChronoUnit.DAYS.between(thisChineseNewYear, gregorianDate);
    year = thisChineseNewYear.getYear();
    month = 0;
    day = 0;
    leapType = LeapType.NOT_LEAP;
    int[] daysPerMonth = toBinaryInts(thisYearInfo[3]);
    for (int i = 0; i < 16; i++) {
      if ((daysPerMonth[i] == 0 ? 29 : 30) > between) {
//        if (month == 0) {
//          day = (int) between;
//        } else {
//          day = (int) between - 1;
//        }
        day = (int) between;
        break;
      } else {
        if (i == 15) {
          throw new LunarException("unknown error 0");
        } else {
          month += 1;
          between = between - (daysPerMonth[i] == 0 ? 29 : 30);
        }
      }
    }
    updateLunarName(thisYearInfo);

  }

  /**
   * get a instance by a gregorian date
   *
   * @param gregorianDate gregorian date
   * @return a already converted LunarDate
   * @throws LunarException Exception with reason
   */
  public static LunarDate ofDay(LocalDate gregorianDate) throws LunarException {
    if (gregorianDate.getYear() < MIN_LUNAR_YEAR || gregorianDate.getYear() > MAX_LUNAR_YEAR) {
      throw new LunarException("out of Range");
    }
    return new LunarDate(gregorianDate);
  }

  /**
   * get a instance by lunar date(year,month,day)
   * <p>attention:LeapType.LEAP_0 Equivalent to LeapType.LEAP_0</p>
   *
   * @param year     lunar year
   * @param month    lunar month
   * @param day      lunar day
   * @param leapType leap type
   * @return a already converted LunarDate
   * @throws LunarException Exception with reason
   */
  public static LunarDate ofDay(int year, int month, int day, LeapType leapType)
      throws LunarException {
    day -= 1;
    if (year < MIN_LUNAR_YEAR || year > MAX_LUNAR_YEAR || month < 0 || month > MAX_LUNAR_MONTH
        || day < 0 || day > MAX_DAY_PER_MONTH) {
      throw new LunarException("out of Range");
    }
    return new LunarDate(year, month, day, leapType);
  }


  /**
   * get a LunarDate array by gregorian date, from first day of month to end day of month
   *
   * @param gregorianDate gregorian date, only use year and month
   * @return LunarDate array of a gregorian month
   * @throws LunarException Exception with reason
   */
  public static LunarDate[] ofMonth(LocalDate gregorianDate) throws LunarException {
    //gregorian month 31 days per a month
    LunarDate[] lunarDates = new LunarDate[MAX_DAY_PER_MONTH + 1];
    if (gregorianDate.getYear() < MIN_LUNAR_YEAR || gregorianDate.getYear() > MAX_LUNAR_YEAR) {
      throw new LunarException("out of Range");
    }
    gregorianDate = gregorianDate.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = gregorianDate.with(TemporalAdjusters.lastDayOfMonth());
    long between = ChronoUnit.DAYS.between(gregorianDate, endDate);
    for (int i = 0; i <= between; i++) {
      lunarDates[i] = new LunarDate(gregorianDate.plusDays(i));
    }
    ArrayList<LunarDate> dates = new ArrayList<>(Arrays.asList(lunarDates));
    dates.removeIf(Objects::isNull);
    return dates.toArray(new LunarDate[0]);
  }

  /**
   * get a LunarDate array by lunar date, from first day of month to end day of month
   * <p>attention:LeapType.LEAP_0 Equivalent to LeapType.LEAP_0</p>
   *
   * @param year     lunar date year
   * @param month    lunar date month
   * @param leapType leap type
   * @return LunarDate array of a lunar date
   * @throws LunarException Exception with reason
   */
  public static LunarDate[] ofMonth(int year, int month, LeapType leapType) throws LunarException {
    List<LunarDate> lunarDateList = new ArrayList<>();
    if (year < MIN_LUNAR_YEAR || year > MAX_LUNAR_YEAR || month < 0 || month > MAX_LUNAR_MONTH) {
      throw new LunarException("out of Range");
    }
    int[] thisYearInfo = YEAR_INFO[year - MIN_LUNAR_YEAR];
    int dayCount;
    if (leapType == LeapType.LEAP_1) {
      if (month == thisYearInfo[0]) {
        dayCount = toBinaryInts(thisYearInfo[3])[month - 2] == 0 ? 29 : 30;
      } else {
        throw new LunarException("not leap month");
      }
    } else {
      dayCount = toBinaryInts(thisYearInfo[3])[month - 1] == 0 ? 29 : 30;
    }
    for (int i = 0; i < dayCount; i++) {
      lunarDateList.add(new LunarDate(year, month, i, leapType));
    }

    return lunarDateList.toArray(new LunarDate[0]);
  }

  private static int[] toBinaryInts(int res) {
    int[] ints = new int[16];
    for (int i = 16; i > 0; i--) {
      ints[i - 1] = res % 2;
      res = res / 2;
    }
    return ints;
  }

  private void updateLunarName(int[] thisYearInfo) {
    dayName = "";
    //dayName
    if (day + 1 <= 10) {
      dayName += DAY_HEADER[0];
      dayName += NUMBER[day + 1];
    } else if (day + 1 < 20) {
      dayName += DAY_HEADER[1];
      dayName += NUMBER[day + 1 - 10];
    } else if (day + 1 == 20) {
      dayName += NUMBER[2];
      dayName += NUMBER[10];
    } else if (day + 1 < 30) {
      dayName += DAY_HEADER[2];
      dayName += NUMBER[day + 1 - 20];
    } else if (day + 1 == 30) {
      dayName += NUMBER[3];
      dayName += NUMBER[10];
    } else {
      dayName += DAY_HEADER[3];
      dayName += NUMBER[day + 1 - 30];
    }
    //monthName
    if (thisYearInfo[0] == 0) {
      thisYearInfo[0] = 999;
    }
    if (month + 1 < thisYearInfo[0] + 1) {
      monthName = MONTH_NAME[month];
      leapType = LeapType.NOT_LEAP;
    } else if (month + 1 == thisYearInfo[0]) {
      leapType = LeapType.LEAP_0;
    } else if (month + 1 == thisYearInfo[0] + 1) {//leap
      monthName = LEAP_HEADER + MONTH_NAME[month - 1];
      leapType = LeapType.LEAP_1;
    } else {
      monthName = MONTH_NAME[month - 1];
      leapType = LeapType.NOT_LEAP;
    }
    //yearName
    yearName = String.valueOf(year);
    yearName = yearName.replace("0", YEAR_NAME[0]);
    yearName = yearName.replace("1", YEAR_NAME[1]);
    yearName = yearName.replace("2", YEAR_NAME[2]);
    yearName = yearName.replace("3", YEAR_NAME[3]);
    yearName = yearName.replace("4", YEAR_NAME[4]);
    yearName = yearName.replace("5", YEAR_NAME[5]);
    yearName = yearName.replace("6", YEAR_NAME[6]);
    yearName = yearName.replace("7", YEAR_NAME[7]);
    yearName = yearName.replace("8", YEAR_NAME[8]);
    yearName = yearName.replace("9", YEAR_NAME[9]);
  }

  public LocalDate getGregorianDate() {
    return gregorianDate;
  }

  /**
   * set new gregorian date
   *
   * @param gregorianDate gregorian date
   * @return a already converted LunarDate
   * @throws LunarException Exception with reason
   */
  public LunarDate setGregorianDate(LocalDate gregorianDate) throws LunarException {
    return ofDay(gregorianDate);
  }

  /**
   * set a new lunar date
   *
   * @param year     lunar year
   * @param month    lunar month
   * @param day      lunar day
   * @param leapType leap type
   * @return a already converted LunarDate
   * @throws LunarException Exception with reason
   */
  public LunarDate setLunarDate(int year, int month, int day, LeapType leapType)
      throws LunarException {
    return ofDay(year, month, day, leapType);
  }

  @Override
  public String toString() {
    return String
        .format("lunar:%s-%s-%s-%s-%s-%s(%s)\tgregorian:%s", yearName, monthName, dayName,
            getYear(),
            getMonth(), getDay(), getLeapType().toString(), gregorianDate.toString());
  }

  public int getYear() {
    return year;
  }

  public int getMonth() {
    if (leapType == LeapType.LEAP_1) {
      return month;
    }
    return month + 1;
  }

  public LeapType getLeapType() {
    return leapType;
  }

  public int getDay() {
    return day + 1;
  }

  public enum LeapType {
    NOT_LEAP, LEAP_0, LEAP_1;

    @Override
    public String toString() {
      switch (this) {
        case NOT_LEAP:
        case LEAP_0:
          return "非闰月";
        case LEAP_1:
          return "闰月";
        default:
          return null;
      }
    }
  }
}
