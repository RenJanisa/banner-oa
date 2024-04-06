package com.banneroa.config;

import com.banneroa.utils.JacksonObjectMapper;
import com.banneroa.utils.LoginCheckInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author rjj
 * @date 2022/9/16 - 8:45
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor()).excludePathPatterns(
                "/oa-member/login",
                "/oa-member/add"
        ).order(0);
    }

    /**
     * 扩展mvc消息转换器(Long --> String 解决id为long型传递时精确度丢失)
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器,底层使用jackson将java对象转换成JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将自定义的消息转换器添加到mvc中转换器集合中(放在前面优先使用)
        converters.add(0, messageConverter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/doc.html");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        //前端资源
//        registry.addResourceHandler("index.html")
//                .addResourceLocations("classpath:/META-INF/resources/index.html");
//        registry.addResourceHandler("/resource/**")
//                .addResourceLocations("classpath:/META-INF/resources/resource/");

    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //允许白名单域名进行跨域调用(设置http://localhost:8080/ 表示指定请求源允许跨域)
        config.addAllowedOriginPattern("*");
        //允许跨越发送cookie
        config.setAllowCredentials(true);
        //放行全部原始头信息
        config.addAllowedHeader("*");
        //允许所有请求方法跨域调用
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //指定拦截路径
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }




}
