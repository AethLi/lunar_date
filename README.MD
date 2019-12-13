#主要功能
1. 格里高利历转中国月历 √
2. 中国月历转格利高利历 √

#数据来源
1. C# ChineseLunisolarCalendar类的静态数据 √
2. 基于儒略日的高精度数据 ×

#阴历日期类
1. LunarDate：不继承于其他Java提供的类 √
2. LunarLocalData:一个继承于Temporal, TemporalAdjuster, ChronoLocalDate的阴历实现类 × 

#使用方法
pom.xml加入
```
<dependency>
   <groupId>cn.aethli</groupId>
   <artifactId>lunar-date</artifactId>
   <version>0.1.0</version>
 </dependency>
```

*√为已实现×为可能以后会做*