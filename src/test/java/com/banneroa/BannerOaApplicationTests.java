package com.banneroa;

import com.banneroa.mapper.OaClassTimeMapper;
import com.banneroa.mapper.OaSignMapper;
import com.banneroa.pojo.OaClassTime;
import com.banneroa.pojo.OaSign;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.swagger.models.auth.In;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.datetime.DateFormatter;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class BannerOaApplicationTests {

    @Resource
    private OaSignMapper oaSignMapper;
    @Resource
    private OaClassTimeMapper classTimeMapper;

    @Test
    void contextLoads() {
        LocalDateTime dateTime = LocalDateTime.parse("2024-03-24 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        int value = dateTime.getDayOfWeek().getValue() - 1;
        //获取当前周周一的日期
        LocalDateTime beginTime = dateTime.minusDays(value);
        //获取周一到现在的签到记录
        List<LocalDateTime> oaSigns = oaSignMapper.getSginsForReport("20231514223", beginTime, dateTime);
        //获取课表
        List<OaClassTime> oaClassTimes = classTimeMapper.selectList(Wrappers.<OaClassTime>lambdaQuery()
                .eq(OaClassTime::getMemberId, "20211574103").orderByAsc(OaClassTime::getOrders));

        // 使用 Map 来保存按日期分组的数据
        Map<String, List<LocalDateTime>> groupedData = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            groupedData.put(beginTime.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), new ArrayList<>());
        }

        // 遍历原始 List，将数据按日期分组
        for (LocalDateTime date : oaSigns) {
            date = date.withSecond(0); //去掉秒
            String day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // 将数据添加到对应日期的 List 中
            groupedData.get(day).add(date);
        }

        List<List<String>> r = new ArrayList<>();
        List<String> lackDay = new ArrayList<>();
        for (Map.Entry<String, List<LocalDateTime>> signTime : groupedData.entrySet()) {
            //每天签到时间
//            System.out.println("日期 " + signTime.getKey() + " 对应的数据列表为：" + signTime.getValue());
            //存储签到标准时间
            Map<Integer, LocalDateTime> targetTime = new HashMap<Integer, LocalDateTime>() {
                {
                    put(0, LocalDateTime.parse(signTime.getKey() + " 08:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    put(1, LocalDateTime.parse(signTime.getKey() + " 10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    put(2, LocalDateTime.parse(signTime.getKey() + " 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    put(3, LocalDateTime.parse(signTime.getKey() + " 17:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    put(4, LocalDateTime.parse(signTime.getKey() + " 20:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            };
            if (signTime.getValue().isEmpty()) {
                //一天未签到或一天均有课
                LocalDate date = LocalDate.parse(signTime.getKey(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String status = oaClassTimes.get(date.getDayOfWeek().getValue()-1).getStatus();
                if (!status.equals("11111")) {
                    //该天未满课,则一天未签到
                    lackDay.add(signTime.getKey());
                }
            } else {
                int week = signTime.getValue().get(0).getDayOfWeek().getValue();
                Map<Integer, List<LocalDateTime>> outcome = getLateTime(oaClassTimes, targetTime, signTime, week - 1);
                if (outcome != null) {
                    if (outcome.keySet().size() == 1) {
                        //每天均未缺签,打印迟到情况
                        List<String> late = outcome.get(0).stream().map(i->{
                            return i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        }).collect(Collectors.toList());
                        System.out.println("当天未缺签但迟到的签到时间" + late);
                        r.add(late);
                    } else {
                        //存在缺签
                        //标准时间
                        List<String> need = outcome.get(2).stream().map(i->{
                            return i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        }).collect(Collectors.toList());
                        System.out.println("缺签当天应签到时间" + need);
                        r.add(need);
                        //签到时间
                        List<String> sign = outcome.get(1).stream().map(i->{
                            return i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        }).collect(Collectors.toList());
                        System.out.println("缺签当天签到时间" + sign);
                        r.add(sign);
                    }
                }
            }
        }
        System.out.println("一天未签到:"+lackDay);
        r.add(lackDay);
    }

    private Map<Integer, List<LocalDateTime>> getLateTime(List<OaClassTime> oaClassTimes, Map<Integer, LocalDateTime> targetTime,
                                                          Map.Entry<String, List<LocalDateTime>> signTime, Integer week) {
        //根据周几获取对应课表
        String status = oaClassTimes.get(week).getStatus();
        if (status.equals("11111")) return null;
        // 保存所有有课的位置索引
        List<Integer> indexes = new ArrayList<>();
        int index = status.indexOf("1");
        while (index != -1) {
            indexes.add(index);
            // 从上一个位置的下一个位置开始搜索
            index = status.indexOf("1", index + 1);
        }
        //删除有课校验时间
        for (Integer i : indexes) {
            targetTime.remove(i);
        }
        //连续时间删除较晚校验时间
        if (targetTime.containsKey(0)) targetTime.remove(1);
        if (targetTime.containsKey(2)) targetTime.remove(3);

        Map<Integer, List<LocalDateTime>> r = new HashMap<>();
        int i = targetTime.size() - signTime.getValue().size();
        if (i == 0) {
            //每天均未缺签,查询是否迟到
            //存储迟到时间
            List<LocalDateTime> lateTime = new ArrayList<>();
            for (Map.Entry<Integer, LocalDateTime> targetTimes : targetTime.entrySet()) {
                //使用标准时间与签到时间比较
                for (LocalDateTime time : signTime.getValue()) {
                    if (targetTimes.getValue().isBefore(time)) {
                        lateTime.add(time);
                        break;
                    }
                }
            }
            r.put(0, lateTime.stream().distinct().collect(Collectors.toList()));
            return r;
        } else if (i > 0) {
            //存在未签到情况
            r.put(1, signTime.getValue());
            r.put(2, new ArrayList<>(targetTime.values()));
            return r;
        } else {
            return null;
        }
    }

}
