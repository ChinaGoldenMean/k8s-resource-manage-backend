package com.pubinfo.resource.service.common;

import com.pubinfo.resource.model.dto.ProjectDTO;
import io.kubernetes.client.Discovery;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.*;

import java.util.List;

public interface K8sApiService {
  ApiextensionsV1beta1Api getApiextensionsV1beta1Api();
  
  Boolean isNotProd();
  
  String getNamespace();
  
  Boolean isK8sManage();
  String getUserNameSpaceStr();
  Discovery getDiscovery();
  Discovery getDiscovery(String k8sConfig);
  Boolean isUserNameSapce(String namespace);
  
  String getK8sConfig();
  
//  Boolean isCurrentUserInRole(String authority);
//
  String getK8sConfig(Integer id);
  
  List<ProjectDTO> getProjects();
  
  StorageV1beta1Api getStorageV1beta1Api();
  
  RbacAuthorizationV1Api getRbacAuthorizationV1Api();
  
  BatchV2alpha1Api getBatchV2alpha1Api();
  
  ExtensionsV1beta1Api getExtensionsV1beta1Api();
  
  RbacAuthorizationV1beta1Api getRbacAuthorizationV1beta1Api();
  
  CoreV1Api getCoreV1Api();
  
  CoreV1Api getCoreV1Api(Integer envId);
  
  StorageV1Api getStorageV1Api();
  
  ApiextensionsV1Api getApiextensionsV1Api();
  
  AppsV1Api getAppsV1Api();
  
  EventsV1beta1Api getEventsV1beta1Api();
  
  BatchV1Api getBatchV1Api();
 ApiClient getApiClient();
  BatchV1beta1Api getBatchV1beta1Api();
  
}
