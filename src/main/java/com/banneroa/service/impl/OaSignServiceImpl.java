package com.banneroa.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.banneroa.dto.ClassTimeDto;
import com.banneroa.dto.ReportLeaveDto;
import com.banneroa.dto.SignReportDto;
import com.banneroa.mapper.OaClassTimeMapper;
import com.banneroa.mapper.OaLeaveMapper;
import com.banneroa.mapper.OaMemberMapper;
import com.banneroa.mapper.OaSignMapper;
import com.banneroa.pojo.OaClassTime;
import com.banneroa.pojo.OaSign;
import com.banneroa.service.OaSignService;
import com.banneroa.utils.AppHttpCodeEnum;
import com.banneroa.utils.BaseContext;
import com.banneroa.utils.ResponseResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.banneroa.utils.RedisConstants.SING_CODE_KEY;
import static com.banneroa.utils.RedisConstants.SING_CODE_TIME;

/**
 * @author rjj
 * @date 2023/12/22 - 15:17
 */
@Service
@Slf4j
public class OaSignServiceImpl extends ServiceImpl<OaSignMapper, OaSign> implements OaSignService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private OaSignMapper oaSignMapper;

    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private ExecutorService executorService;

    @Resource
    private OaMemberMapper oaMemberMapper;
    @Value("${spring.mail.username}")
    private String from;


    @Override
    public ResponseResult sendCode(String objId) {

        if (StrUtil.isBlank(objId)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if (BaseContext.getMember().getFlag() != 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_ADMIND);
        }
        if ("12345678901".equals(BaseContext.getMember().getId().toString())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_ADMIND);
        }

        String code = RandomUtil.randomNumbers(6);
        String key = SING_CODE_KEY + objId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return ResponseResult.errorResult(201, "请勿多次请求");
        }
        stringRedisTemplate.opsForValue().set(key, code, SING_CODE_TIME, TimeUnit.MINUTES);

        //发送邮件提醒
        executorService.execute(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from + "(旗帜软件工作室)");
            String qq = oaMemberMapper.getMemberQQ(objId) + "@qq.com";
            message.setTo(qq);
            message.setSubject("验证码已发放");
            message.setText("验证码已发放,请及时签到");
            javaMailSender.send(message);
            log.info("已发送...");
        });

        return ResponseResult.okResult(code);
    }


    @Override
    public ResponseResult getCode(String direction) {

        Long userId = BaseContext.getMember().getId();
        String key = SING_CODE_KEY + userId;
        String code = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(code)) {
            return ResponseResult.errorResult(201, "尚无验证码，请联系管理员申请");
        }

        return ResponseResult.okResult(code);
    }

    @Override
    public ResponseResult doSign(String code) {

        if (StrUtil.isBlank(code)) {
            return ResponseResult.errorResult(201, "请先获取验证码");
        }
        Long id = BaseContext.getMember().getId();
        String key = SING_CODE_KEY + id;
        String codeRight = stringRedisTemplate.opsForValue().get(key);
        if (!code.equals(codeRight)) {
            return ResponseResult.errorResult(201, "验证码错误");
        }
        OaSign oaSign = new OaSign();
        oaSign.setMemberId(id);
        oaSign.setComeTime(LocalDateTime.now());
        int insert = oaSignMapper.insert(oaSign);
        if (insert > 0 && Boolean.TRUE.equals(stringRedisTemplate.delete(key))) {
            return ResponseResult.okResult(200, "签到成功：" + oaSign.getComeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        return ResponseResult.errorResult(201, "签到失败");
    }

    @Override
    public ResponseResult getSigns(String objId, Integer currentPage, String checkTime) {

        if (StrUtil.isBlank(objId)) objId = BaseContext.getMember().getId().toString();

        Integer page = (currentPage - 1) * 8;
        List<OaSign> oaSigns;
        Integer total;
        if (StrUtil.isBlank(checkTime)) {
            oaSigns = oaSignMapper.getSgins(objId, page);
            total = oaSignMapper.selectCount(Wrappers.<OaSign>lambdaQuery().eq(OaSign::getMemberId, objId));
        } else {
            total = oaSignMapper.selectCount(Wrappers.<OaSign>lambdaQuery().eq(OaSign::getMemberId, objId).like(OaSign::getComeTime, checkTime));
            checkTime = checkTime + "%";
            oaSigns = oaSignMapper.getSginsWithCondition(objId, page, checkTime);
        }
        Map<String, Object> r = new HashMap<>();
        r.put("signs", oaSigns);
        r.put("total", total);


        return ResponseResult.okResult(r);
    }

    @Override
    public ResponseResult doSignLeave(String id) {

        if (StrUtil.isBlank(id)) return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);

        if (StrUtil.isNotBlank(oaSignMapper.getLeaveTime(id))) return ResponseResult.errorResult(201, "无法再次签退");

        OaSign oaSign = new OaSign();
        oaSign.setId(Long.valueOf(id));
        oaSign.setLeaveTime(LocalDateTime.now());
        int updated = oaSignMapper.updateById(oaSign);

        return updated > 0 ? ResponseResult.okResult(200, "签退成功：" + oaSign.getLeaveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                : ResponseResult.errorResult(201, "签退失败");
    }

    @Override
    public ResponseResult delSign(String id) {

        if (BaseContext.getMember().getFlag() != 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_ADMIND);
        }

        int delete = oaSignMapper.deleteById(id);

        return delete > 0 ? ResponseResult.okResult(200, "删除成功")
                : ResponseResult.errorResult(201, "删除失败");
    }

    @Resource
    private OaClassTimeMapper oaClassTimeMapper;

    @Override
    public ResponseResult addClassTIme(ClassTimeDto classTimeDto) {

        //获取类对象属性值
        Field[] declaredFields = classTimeDto.getClass().getDeclaredFields();
        Long userId = BaseContext.getMember().getId();

        //开启线程异步添加数据
        executorService.execute(() -> {
        for (int i = 0; i < declaredFields.length; i++) {
            try {
                String day = declaredFields[i].getName();
                //反射调用get方法获取值
                String days = day.substring(0, 1).toUpperCase() + day.substring(1);
                Method method = classTimeDto.getClass().getMethod("get" + days);
                List<String> r = (List<String>) method.invoke(classTimeDto);
                insertClassTime(r, userId, day, i);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        });
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private void insertClassTime(List<String> list, Long userId, String day, Integer order) {
        StringBuilder builder = new StringBuilder("00000");
        if (!list.isEmpty()) {
            //当天存在课表需要转换
            for (String time : list) {
                //汉字转数字格式
                compareTime(time, builder);
            }
        }
        Integer count = oaClassTimeMapper.isExist(userId, day);
        if (count > 0) {
            //已添加过,现修改
            oaClassTimeMapper.updateClassTime(userId, day, builder.toString());
        } else {
            //未添加过
            oaClassTimeMapper.insert(new OaClassTime(userId, day, builder.toString(), order));
        }
    }

    private void compareTime(String time, StringBuilder builder) {
        switch (time) {
            case "上午一":
                builder.replace(0, 1, "1");
                break;
            case "上午二":
                builder.replace(1, 2, "1");
                break;
            case "下午一":
                builder.replace(2, 3, "1");
                break;
            case "下午二":
                builder.replace(3, 4, "1");
                break;
            case "晚上一":
                builder.replace(4, 5, "1");
                break;
        }
    }

    @Override
    public ResponseResult getClassTime(String userId) {

        if(StrUtil.isBlank(userId)) userId = BaseContext.getMember().getId().toString();
        ClassTimeDto classTimeDto = new ClassTimeDto();
        List<OaClassTime> oaClassTimes = oaClassTimeMapper.selectList(Wrappers.<OaClassTime>lambdaQuery().eq(OaClassTime::getMemberId, userId));
        if (oaClassTimes.isEmpty()) {
            return ResponseResult.okResult(classTimeDto);
        }
        //已添加过课表,处理值返回
        Field[] fields = classTimeDto.getClass().getDeclaredFields();
        for (OaClassTime oaClassTime : oaClassTimes) {
            String day = oaClassTime.getDay();
            //数字格式转汉字
            List<String> strings = compareTimeTo(oaClassTime.getStatus());
            try {
                //根据方法名反射获取set方法执行
                day = day.substring(0, 1).toUpperCase() + day.substring(1);
                Method method = classTimeDto.getClass().getDeclaredMethod("set" + day, fields[0].getType());
                method.invoke(classTimeDto, strings);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseResult.okResult(classTimeDto);
    }

    private List<String> compareTimeTo(String time) {
        List<String> r = new ArrayList<>();
        for (int i = 0; i < time.length(); i++) {
            //取出为1的值转成汉字
            if ('1' == (time.charAt(i))) {
                r.add(intToString(i));
            }
        }
        return r;
    }

    private String intToString(int i) {
        switch (i) {
            case 0:
                return "上午一";
            case 1:
                return "上午二";
            case 2:
                return "下午一";
            case 3:
                return "下午二";
            case 4:
                return "晚上一";
            default:
                return null;
        }
    }

    @Override
    public ResponseResult delClassTime(String userId) {

        if(StrUtil.isBlank(userId)) userId = BaseContext.getMember().getId().toString();
        int delete = oaClassTimeMapper.delete(Wrappers.<OaClassTime>lambdaQuery().eq(OaClassTime::getMemberId, userId));

        return delete > 0 ? ResponseResult.okResult(AppHttpCodeEnum.SUCCESS)
                : ResponseResult.errorResult(AppHttpCodeEnum.USER_DATA_NOT_EXIST);
    }

    @Resource
    private OaLeaveMapper oaLeaveMapper;

    @Override
    public ResponseResult getSignReport(String userId) {

//        LocalDateTime dateTime = LocalDateTime.parse("2024-03-24 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime dateTime = LocalDateTime.now();
        if(StrUtil.isBlank(userId)) userId = BaseContext.getMember().getId().toString();
        int value = dateTime.getDayOfWeek().getValue() - 1;
        //获取当前周周一的日期
        LocalDateTime beginTime = dateTime.minusDays(value).withHour(0);
        //获取周一到现在的签到记录
        List<LocalDateTime> oaSigns = oaSignMapper.getSginsForReport(userId, beginTime, dateTime);
        //获取周一到现在的假条
        List<ReportLeaveDto> oaLeaves = oaLeaveMapper.getLeaveforReport(userId, beginTime, dateTime);
        //获取课表
        List<OaClassTime> oaClassTimes = oaClassTimeMapper.selectList(Wrappers.<OaClassTime>lambdaQuery()
                .eq(OaClassTime::getMemberId, userId).orderByAsc(OaClassTime::getOrders));

        if (oaClassTimes.isEmpty()) return ResponseResult.errorResult(201,"请补充课表!");
        // 使用 Map 来保存按日期分组的数据
        Map<String, List<LocalDateTime>> groupedData = new HashMap<>();
        for (int i = 0; i <= value; i++) {
            groupedData.put(beginTime.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), new ArrayList<>());
        }

        // 遍历原始 List，将数据按日期分组
        for (LocalDateTime date : oaSigns) {
            date = date.withSecond(0); //去掉秒
            String day = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // 将数据添加到对应日期的 List 中
            groupedData.get(day).add(date);
        }

        //存储结果
        SignReportDto signReportDto = new SignReportDto();
        //存储不缺签但迟到情况
        List<List<String>> lateTime = new ArrayList<>();
        //存储缺签那天签到情况
        List<List<List<List<String>>>> lackTime = new ArrayList<>();
        lackTime.add(0,new ArrayList<>());
        lackTime.add(1,new ArrayList<>());

        //存储一天未签到日期
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
                String status = oaClassTimes.get(date.getDayOfWeek().getValue() - 1).getStatus();
                if (!status.equals("11111")) {
                    //该天未满课,则一天未签到
                    lackDay.add("(星期" + date.getDayOfWeek().getValue() + ")" + signTime.getKey());
                }
            } else {
                int week = signTime.getValue().get(0).getDayOfWeek().getValue();
                Map<Integer, List<LocalDateTime>> outcome = getLateTime(oaClassTimes, targetTime, signTime, week - 1);
                if (outcome != null) {
                    if (outcome.keySet().size() == 1) {
                        //每天均未缺签,查出迟到情况
                        List<String> late = outcome.get(0).stream().map(i -> {
                            return "(星期" + i.getDayOfWeek().getValue() + ")" + i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        }).collect(Collectors.toList());
                        System.out.println("当天未缺签但迟到的签到时间" + late);
                        lateTime.add(late);
                    } else {
                        //存在缺签
                        //标准时间
                        List<List<String>> lackTargetTime = new ArrayList<>();
                        List<String> need = outcome.get(2).stream().map(i -> {
                            return "(星期" + i.getDayOfWeek().getValue() + ")" + i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        }).collect(Collectors.toList());
                        System.out.println("缺签当天应签到时间" + need);
                        lackTargetTime.add(need);
                        //签到时间
                        List<List<String>> lackSignTime = new ArrayList<>();
                        List<String> sign = outcome.get(1).stream().map(i -> {
                            return "(星期" + i.getDayOfWeek().getValue() + ")" + i.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        }).collect(Collectors.toList());
                        System.out.println("缺签当天签到时间" + sign);
                        lackSignTime.add(sign);
                        lackTime.get(0).add(lackTargetTime);
                        lackTime.get(1).add(lackSignTime);
                    }
                }
            }
        }
        lackDay.sort((Comparator.comparingInt(o -> o.charAt(3)))); //按照星期排序
        lateTime.sort((Comparator.comparingInt(o -> o.get(0).charAt(3))));
        lackTime.get(0).sort((Comparator.comparingInt(o -> o.get(0).get(0).charAt(3))));
        lackTime.get(1).sort((Comparator.comparingInt(o -> o.get(0).get(0).charAt(3))));
        System.out.println("一天未签到:" + lackDay);
        signReportDto.setLackDay(lackDay);
        signReportDto.setLateTime(lateTime);
        signReportDto.setLackTime(lackTime);
        signReportDto.setLeaveTime(oaLeaves);

        return ResponseResult.okResult(signReportDto);
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
