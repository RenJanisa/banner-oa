package com.banneroa.dto;

import lombok.Data;

import java.util.List;

/**
 * @author rjj
 * @date 2024/3/26 - 21:52
 */
@Data
public class SignReportDto {
    private List<List<String>> lateTime;
    private List<String> lackDay;
    private List<List<List<List<String>>>> lackTime;
    private List<ReportLeaveDto> leaveTime;

}
