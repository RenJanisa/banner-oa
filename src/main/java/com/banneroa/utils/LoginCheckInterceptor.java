package com.banneroa.utils;

import cn.hutool.core.util.StrUtil;
import com.banneroa.dto.MemberInfoDto;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rjj
 * @date 2022/9/16 - 8:55
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url = request.getRequestURL().toString();
        if (url.contains("doc.html")
                || url.contains("swagger")
                || url.contains("error")
                || url.contains("webjars")
                || url.contains("resource")
                || url.contains("index")) {
            return true;
        }

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            response.getWriter().print("无权限访问!输入token!");
            return false;
        }
        //todo 0: 过期, 1,2: 未过期
        Claims claims;
        try {
            claims = AppJwtUtil.getClaimsBody(token);
        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().print("无权限访问!");
            return false;
        }
        int status = AppJwtUtil.checkTokenTime(claims);
        if (status == 1 || status == 2) {
            BaseContext.setMember(new MemberInfoDto(
                    (Long) claims.get("id"),
                    (int) claims.get("flag")));
            return true;
        } else {
            response.setStatus(401);
            response.getWriter().print("无权限访问!");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.removeMember();
    }
}
