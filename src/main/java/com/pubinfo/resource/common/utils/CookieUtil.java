package com.pubinfo.resource.common.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * cookie 工具类  功能: 减少重复代码，获取cookie
 *
 * @author Administrator
 */
public class CookieUtil {
  public static final String ENV_ID = "envId";
  public static final String NAME_SPACE = "nameSpace";
  public static final String UNDEFINED = "undefined";
  
  private CookieUtil() {
  
  }
  
  private static String getCookie(HttpServletRequest request, String key) {
    String value = null;
    if (request == null){
      return null;
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(key)) {
          value = cookie.getValue();
        }
      }
    }
    if ("全选".equalsIgnoreCase(value)){
      return null;
    }
    
    
    return value;
  }
  
  public static String getNamespace(HttpServletRequest request) {
    String namespace = getCookie(request, NAME_SPACE);
    if (UNDEFINED.equalsIgnoreCase(namespace)) {
      return null;
    }
    String[] stringEnumeration;
    if (namespace == null) {
      
      String cookies = request.getHeader("Cookie");
      if (StringUtils.isNotBlank(cookies)) {
        stringEnumeration = cookies.split(";");
        if (stringEnumeration.length > 0) {
          for (int i = 0; i < stringEnumeration.length; i++) {
            String str = stringEnumeration[i];
            if (str.indexOf(NAME_SPACE + "=") >= 0) {
              namespace = str.substring(str.indexOf("=") + 1, str.length());
            }
          }
        }
        
      }
      
    }
    if(namespace == null){
      String headEnvId=null;
      if(request!=null){
        headEnvId = request.getHeader("cookies");
        if(headEnvId!=null){
  
          namespace =  getStr(headEnvId,NAME_SPACE);
        }
      }
    
    }
    return namespace;
  }
  
  public static Integer getEnvId(HttpServletRequest request) {
    String envIdStr = getCookie(request, ENV_ID);
    Integer envId = null;
    String headEnvId=null;
   
    
    if (envIdStr != null && !UNDEFINED.equalsIgnoreCase(envIdStr)) {
      envId = Integer.valueOf(envIdStr);
    }else{
      if(request!=null){
        headEnvId = request.getHeader("cookies");
        if(headEnvId!=null){
          envId = Integer.valueOf(getStr(headEnvId,ENV_ID));
        }
       
      }
     
    }
    
    return envId;
  }
  private static String getStr(String cookies,String key){
    String value=null;
    if (StringUtils.isNotBlank(cookies)) {
    String[]  stringEnumeration = cookies.split(";");
      if (stringEnumeration.length > 0) {
        for (int i = 0; i < stringEnumeration.length; i++) {
          String str = stringEnumeration[i].trim();
           if(str.contains(key)){
             value =str.substring(key.length()+1 );
           }
        }
      }
    
    }
    return value;
  }
}