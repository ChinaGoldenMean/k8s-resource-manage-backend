package com.pubinfo.resource.service.common.impl;

import cn.hutool.core.io.IoUtil;
import com.pubinfo.resource.common.utils.K8sManagement;
import com.pubinfo.resource.config.ApplicationConfig;
import com.pubinfo.resource.model.constant.K8sConstant;
import com.pubinfo.resource.model.dto.ProjectDTO;
import com.pubinfo.resource.repository.ManageEnvRepository;
import com.pubinfo.resource.service.common.K8sApiService;
import io.kubernetes.client.Discovery;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class K8sApiServiceImpl extends K8sManagement implements K8sApiService {
  @Value("${spring.profiles.active}")
  private String env;
  
  @Resource
  ManageEnvRepository envMapper;
  
  @Autowired
  ApplicationConfig config;
  
  private static String K8S_YAML_NAME = "k8s-default.yaml";
  private static String K8S_ERROR = "初始化k8s失败";
  
  @Override
  public ApiextensionsV1beta1Api getApiextensionsV1beta1Api() {
    ApiextensionsV1beta1Api v1beta1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      v1beta1Api = getApiextensionsV1beta1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return v1beta1Api;
  }
  @Override
  public Boolean isNotProd() {
    if (!StringUtils.isEmpty(env)) {
      if( !env.contains(K8sConstant.PROD)||K8sConstant.LOCAL_MS.equalsIgnoreCase(env)){
        return true;
      }
      
    }
    return false;
  }
  
  @Override
  public BatchV1Api getBatchV1Api() {
    BatchV1Api batchV1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      batchV1Api = getBatchV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return batchV1Api;
  }
  
  @Override
  public EventsV1beta1Api getEventsV1beta1Api() {
    EventsV1beta1Api eventsV1beta1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      eventsV1beta1Api = getEventsV1beta1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return eventsV1beta1Api;
  }
  
  /**
   * 获取资源调度类
   *
   * @return
   */
  @Override
  public ExtensionsV1beta1Api getExtensionsV1beta1Api() {
    ExtensionsV1beta1Api v1beta1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      v1beta1Api = getExtensionsV1beta1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return v1beta1Api;
  }
  
  @Override
  public RbacAuthorizationV1beta1Api getRbacAuthorizationV1beta1Api() {
    RbacAuthorizationV1beta1Api rbacAuthorizationV1beta1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      rbacAuthorizationV1beta1Api = getRbacAuthorizationV1beta1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return rbacAuthorizationV1beta1Api;
    
  }
  
  @Override
  public BatchV1beta1Api getBatchV1beta1Api() {
    BatchV1beta1Api batchV1beta1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      batchV1beta1Api = getBatchV1beta1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return batchV1beta1Api;
  }
  
  /**
   * 获取资源调度类
   *
   * @return
   */
  @Override
  public CoreV1Api getCoreV1Api() {
    CoreV1Api coreV1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      coreV1Api = getCoreV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return coreV1Api;
  }
  @Override
  public ApiClient getApiClient() {
    
    ApiClient apiClient = null;
    try {
      String k8sConfig = getK8sConfig();
      apiClient = init(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
    
    }
  
    return apiClient;
  }
  @Override
  public CoreV1Api getCoreV1Api(Integer envId) {
    CoreV1Api coreV1Api = null;
    String k8sConfig = envMapper.selectById(envId).getK8sConfig();
    try {
      coreV1Api = getCoreV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return coreV1Api;
  }
  
  @Override
  public RbacAuthorizationV1Api getRbacAuthorizationV1Api() {
    RbacAuthorizationV1Api rbacAuthorizationV1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      rbacAuthorizationV1Api = getRbacAuthorizationV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return rbacAuthorizationV1Api;
  }
  
  @Override
  public BatchV2alpha1Api getBatchV2alpha1Api() {
    BatchV2alpha1Api batchV2alpha1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      batchV2alpha1Api = getBatchV2alpha1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    return batchV2alpha1Api;
  }
  
  @Override
  public String getK8sConfig(Integer envId) {
    String k8sConfig = envMapper.selectById(envId).getK8sConfig();
    
    return k8sConfig;
  }
  @Override
  public  String getUserNameSpaceStr(){
    List<ProjectDTO> projectList = getProjects();
    StringBuilder stringBuilder = new StringBuilder(K8sConstant.ALL + ",");
    if (projectList != null && projectList.size() > 0) {
    
      projectList.stream().forEach(projectDTO -> {
        stringBuilder.append(projectDTO.getCode()).append(",");
      });
    
    }
    return stringBuilder.toString();
  }
  @Override
  public String getNamespace() {
    //如果前端传过来all,则查询用户权限
    
    String nameSapce = getNameSpace();
    if (K8sConstant.ALL.equalsIgnoreCase(nameSapce) || StringUtils.isEmpty(nameSapce)) {
      nameSapce = getUserNameSpaceStr();
    }
    return nameSapce;
  }
  
  @Override
  public Boolean isK8sManage() {
    
    if(isNotProd()){
      return true;
    }
    return false;
  }
  
  @Override
  public Boolean isUserNameSapce(String namespace) {
    if (StringUtils.isEmpty(namespace)){
  
      return true;
    }
    
    if (isK8sManage()) {
      return true;
    }
    List<ProjectDTO> projectList = getProjects();
    if (projectList != null && projectList.size() > 0) {
      for (ProjectDTO projectDTO : projectList) {
        if (namespace.equals(projectDTO.getCode())) {
          return true;
        }
      }
      
    }
    return false;
  }
  
  @Override
  public List<ProjectDTO> getProjects() {
    List<ProjectDTO> projectList=new ArrayList<>();
//    if (isNotProd()) {
//      projectList = K8sUtils.generateTestData();
//    } else {
//      String username =SecurityUtils.getCurrentUserName();
//      log.info("当前用户名称："+username);
//      projectList = projectManager.getProjects(username);
//    }
    return projectList;
  }
  
  @Override
  public Discovery getDiscovery() {
    String k8sConfig = getK8sConfig();
   
    return getDiscovery(k8sConfig);
  }
  
  @Override
  public Discovery getDiscovery(String k8sConfig) {
    Discovery discovery = null;
    try {
      discovery = getDiscovery(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
    
    }
    return discovery;
  }
  
  @Override
  public String getK8sConfig() {
    Integer envId = getEnvId();
    String k8sConfig = "";
    if (envId != null) {
      k8sConfig = getK8sConfig(envId);
    } else {
      //如果没有默认数据,则读取默认的配置文件生成.
      InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(K8S_YAML_NAME);
      k8sConfig = IoUtil.read(inputStream, "utf8");
      
    }
    
    return k8sConfig;
  }
//
//  @Override
//  public Boolean isCurrentUserInRole(String authority) {
//    if (StringUtils.isEmpty(authority))
//      return false;
//    BiyiUserAuthDetailDto userAuthDetailDto = userManager.getUserDetails(SecurityUtils.getCurrentUserName());
//    if (userAuthDetailDto != null) {
//      List<String> authoritesList = userAuthDetailDto.getGrantedAuthorities();
//      if (authoritesList != null && authoritesList.size() > 0) {
//        for (String authorites : authoritesList) {
//          if (authority.equals(authorites)) {
//            return true;
//          }
//        }
//      }
//    }
//    return false;
//  }
  
  @Override
  public StorageV1beta1Api getStorageV1beta1Api() {
    StorageV1beta1Api storageV1beta1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      storageV1beta1Api = getStorageV1beta1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    return storageV1beta1Api;
  }
  
  /**
   * 获取资源调度类
   *
   * @return
   */
  @Override
  public AppsV1Api getAppsV1Api() {
    AppsV1Api appsV1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      appsV1Api = getAppsV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return appsV1Api;
  }
  
  @Override
  public StorageV1Api getStorageV1Api() {
    StorageV1Api storageV1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      storageV1Api = getStorageV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return storageV1Api;
  }
  
  @Override
  public ApiextensionsV1Api getApiextensionsV1Api() {
    ApiextensionsV1Api apiextensionsV1Api = null;
    String k8sConfig = getK8sConfig();
    try {
      apiextensionsV1Api = getApiextensionsV1Api(k8sConfig, config.getDebugger());
    } catch (Exception e) {
      log.error(K8S_ERROR, e);
      
    }
    
    return apiextensionsV1Api;
  }
}
