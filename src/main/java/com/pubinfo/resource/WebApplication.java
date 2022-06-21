package com.pubinfo.resource;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
@MapperScan("com.pubinfo.resource.repository")
@Slf4j
@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
public class   WebApplication   {
  
  public static void main(String[] args) throws UnknownHostException {
  
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
 SpringApplication.run(WebApplication.class, args);
    
  }
  
}