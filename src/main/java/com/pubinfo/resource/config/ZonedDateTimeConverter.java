package com.pubinfo.resource.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * String转ZonedDateTime，解决时间条件查询格式转换问题
 *
 * @author lym
 */
@Configuration
public class ZonedDateTimeConverter {
  
  private static final Logger log = LoggerFactory.getLogger(ZonedDateTimeConverter.class);
  
  @Bean
  public Converter<String, ZonedDateTime> zonedDateTimeConvert() {
    return new Converter<String, ZonedDateTime>() {
      @Override
      public ZonedDateTime convert(String source) {
        /*根据需要修改时间格式与时区*/
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        ZonedDateTime date = null;
        try {
          date = ZonedDateTime.parse(source, df).withZoneSameInstant(ZoneId.of("UTC"));
        } catch (Exception e) {
          log.error("can not convert ZonedDateTime to Date", e.getMessage());
        }
        return date;
      }
    };
  }
}
