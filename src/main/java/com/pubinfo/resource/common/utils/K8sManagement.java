package com.pubinfo.resource.common.utils;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.vo.ResultCode;
import io.kubernetes.client.Discovery;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Data
public class K8sManagement {
  protected String k8sConfig = null;
  private ApiClient apiClient;
  
  
  public void setApiClient(ApiClient apiClient) {
    this.apiClient = apiClient;
  }
  
  public StorageV1Api getStorageV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new StorageV1Api();
  }
  
  public ApiextensionsV1beta1Api getApiextensionsV1beta1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new ApiextensionsV1beta1Api();
  }
  
  public Discovery getDiscovery(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new Discovery();
  }
  
  public StorageV1beta1Api getStorageV1beta1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new StorageV1beta1Api();
  }
  
  public CoreV1Api getCoreV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new CoreV1Api();
  }
  
  public EventsV1beta1Api getEventsV1beta1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new EventsV1beta1Api();
  }
  
  public ExtensionsV1beta1Api getExtensionsV1beta1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new ExtensionsV1beta1Api();
  }
  
  public RbacAuthorizationV1beta1Api getRbacAuthorizationV1beta1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new RbacAuthorizationV1beta1Api();
  }
  
  public BatchV1beta1Api getBatchV1beta1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    return new BatchV1beta1Api();
  }
  
  public AutoscalingV1Api getAutoscalingV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    //设置10分钟的超时时间
    return new AutoscalingV1Api();
  }
  
  public AppsV1Api getAppsV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    //设置10分钟的超时时间
    return new AppsV1Api();
  }
  
  public ApiextensionsV1Api getApiextensionsV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    //设置10分钟的超时时间
    return new ApiextensionsV1Api();
  }
  
  public BatchV1Api getBatchV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    //设置10分钟的超时时间
    return new BatchV1Api();
  }
  
  public BatchV2alpha1Api getBatchV2alpha1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    //设置10分钟的超时时间
    return new BatchV2alpha1Api();
  }
  
  public RbacAuthorizationV1Api getRbacAuthorizationV1Api(String k8sConfig, Boolean debugger) {
    init(k8sConfig, debugger);
    //设置10分钟的超时时间
    return new RbacAuthorizationV1Api();
  }
  
  public HttpServletRequest getHttpServletRequest() {
    //获取request、response、session
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      return requestAttributes.getRequest();
    }
    return null;
  }
  
  public String getNameSpace() {
    //如果前端传过来all,则查询用户权限
    return CookieUtil.getNamespace(getHttpServletRequest());
  }
  
  public Integer getEnvId() {
    return CookieUtil.getEnvId(getHttpServletRequest());
  }
  
  public ApiClient init(String k8sConfig, Boolean debugger) {
    
    if (k8sConfig == null) {
      throw new ServiceException(ResultCode.K8S_CONFIG_ERROR);
    }
    if (k8sConfig.equals("default")) {
      //获取本机集群
      try {
        apiClient = ClientBuilder.cluster().build();
      } catch (IOException e) {
        log.error("初始化k8s失败!", e);
        
      }
    } else {
      
      try (InputStream resourceAsStream = new ByteArrayInputStream(k8sConfig.getBytes("utf-8"))) {
        
        apiClient = Config.fromConfig(resourceAsStream);
        
      } catch (IOException e) {
        log.error("初始化k8s失败!", e);
        
      }
    }
    
    if (apiClient != null) {
      io.kubernetes.client.openapi.Configuration.setDefaultApiClient(apiClient);
      apiClient.setDebugging(debugger);
    }
    
    return apiClient;
  }
  
}
