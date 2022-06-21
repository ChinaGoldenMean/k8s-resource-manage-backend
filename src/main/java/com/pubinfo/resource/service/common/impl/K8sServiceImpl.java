package com.pubinfo.resource.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.utils.MyJsonUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.config.ApplicationConfig;
import com.pubinfo.resource.model.constant.ApiVersionEnum;
import com.pubinfo.resource.model.constant.K8sObject;
import com.pubinfo.resource.model.constant.KindEnum;
import com.pubinfo.resource.model.dto.K8sConfigDTO;
import com.pubinfo.resource.model.dto.K8sYamlDTO;
import com.pubinfo.resource.model.dto.ScaleYamlDTO;
import com.pubinfo.resource.model.dto.YamlBaseDTO;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.common.K8sService;
import com.google.gson.Gson;
import com.pubinfo.resource.model.constant.K8sParam;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.*;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

//import com.pubinfo.resource.repository.ManageEnvRepository;

@Slf4j
@Service
public class K8sServiceImpl extends K8sSearch implements K8sService {
  //
//  @Autowired
//  ManageEnvRepository envMapper;
  @Autowired
  K8sApiService k8sApi;
  
  @Autowired
  ApplicationConfig applicationConfig;
  
  /**
   * 根据yaml文件生成的vo类发布内容
   *
   * @param k8SYamlDTO
   * @return
   */
  
  @Override
  public boolean createResource(K8sYamlDTO k8SYamlDTO) {
    if (k8SYamlDTO != null) {
      log.info("k8sYamlVO为:{}", new Gson().toJson(k8SYamlDTO));
      String kind = k8SYamlDTO.getKind();
      String apiVersion = k8SYamlDTO.getApiVersion();
      String namespace = k8SYamlDTO.getNamespace();
      
      if (StringUtils.isBlank(namespace)) {
        k8SYamlDTO.setNamespace(K8sObject.NAMESPACE);
      }
      
      
      KindEnum kindTypeEnum = KindEnum.getEnumByType(kind);
      //如果不是管理 员,操作集群资源,则返回错误
      if(!k8sApi.isK8sManage()&&(KindEnum.isK8sCluster(kindTypeEnum)||KindEnum.NAMESPACE==kindTypeEnum)){
        throw new ServiceException(ResultCode.FORBIDDEN);
      }
      ApiVersionEnum apiversionTypeEnum = ApiVersionEnum.getEnumByType(apiVersion);
      ApiextensionsV1Api apiextensionsV1Api = k8sApi.getApiextensionsV1Api();
      ApiextensionsV1beta1Api apiextensionsV1beta1Api = k8sApi.getApiextensionsV1beta1Api();
      if (kindTypeEnum != null && apiversionTypeEnum != null) {
        try {
          switch (apiversionTypeEnum) {
            case EXTENSION_API_V1BETA1:
              V1beta1CustomResourceDefinition v1beta1ApiCustomResourceDefinition = apiextensionsV1beta1Api.createCustomResourceDefinition(K8sUtils.toObject(k8SYamlDTO.getO(), V1beta1CustomResourceDefinition.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
              if (v1beta1ApiCustomResourceDefinition != null)
                return true;
            case EXTENSION_API_V1:
              V1CustomResourceDefinition v1CustomResourceDefinition = apiextensionsV1Api.createCustomResourceDefinition(K8sUtils.toObject(k8SYamlDTO.getO(), V1CustomResourceDefinition.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
              if (v1CustomResourceDefinition != null)
                return true;
              //  case APP_V1:
            case COREAPI_V1:
              return coreV1ApiDeploy(k8SYamlDTO, kindTypeEnum);
            
            case EXTENSION_API:
              return extensionApiDeploy(k8SYamlDTO, kindTypeEnum);
            
            case APP_V1:
              
              return appsV1ApiDeploy(k8SYamlDTO, kindTypeEnum);
            
            case RBAC_AUTHORIZATION_K8S_IO_V1:
              return rbacAuthorizationK8sIoV1Deploy(k8SYamlDTO, kindTypeEnum);
            case RBAC_AUTHORIZATION_K8S_IO_V1BETA1:
              return rbacAuthorizationK8sIoBetaV1Deploy(k8SYamlDTO, kindTypeEnum);
            case STORAGE_K8S_IO_V1:
              StorageV1Api storageV1Api = k8sApi.getStorageV1Api();
              V1StorageClass storageClass = storageV1Api.createStorageClass(K8sUtils.toObject(k8SYamlDTO.getO(), V1StorageClass.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
              if (storageClass != null) {
                return true;
              }
              return false;
            case STORAGE_K8S_IO_V1BETA1:
              StorageV1beta1Api storageV1beta1Api = k8sApi.getStorageV1beta1Api();
              V1beta1StorageClass beta1StorageClass = storageV1beta1Api.createStorageClass(K8sUtils.toObject(k8SYamlDTO.getO(), V1beta1StorageClass.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
              if (beta1StorageClass != null) {
                return true;
              }
              return false;
            case BATCH_V2ALPHA1:
            case BATCH_V1:
            case BATCH_V1BETA1:
              switch (kindTypeEnum) {
                case JOB:
                  BatchV1Api batchV1Api = k8sApi.getBatchV1Api();
                  V1Job job = batchV1Api.createNamespacedJob(k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(), V1Job.class),
                      K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
                  if (job != null) {
                    return true;
                  }
                  return false;
                case CRON_JOB:
                  BatchV1beta1Api batchV1beta1Api = k8sApi.getBatchV1beta1Api();
                  V1beta1CronJob v1beta1CronJob = batchV1beta1Api.createNamespacedCronJob(k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(), V1beta1CronJob.class),
                      K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
                  if (v1beta1CronJob != null) {
                    return true;
                  }
                  return false;
                default:
                  break;
              }
            
            default:
              break;
            
          }
        
        } catch (ApiException e) {
          throw new ServiceException(ResultCode.RESOURCE_CREATE_FAIL, K8sUtils.getMessage(e));
        }
      }
      return createResourceCrd(k8SYamlDTO);
    }
    return false;
  }
  
  @Override
  public boolean createResourceCrd(K8sYamlDTO yamlVo) {
    String k8sConfig = k8sApi.getK8sConfig();
    String url ="";
    StringBuilder localVarPath = new StringBuilder(
        "/apis");
    if(StringUtils.isNotBlank(yamlVo.getApiVersion())){
      localVarPath.append("/"+yamlVo.getApiVersion());
    }
    if(StringUtils.isNotBlank(yamlVo.getNamespace())){
      localVarPath.append("/namespaces/"+yamlVo.getNamespace());
    }
    if(StringUtils.isNotBlank(yamlVo.getKind())){
      localVarPath.append("/"+yamlVo.getKind().toLowerCase()+"s");
    }
    
    url = localVarPath.toString();
    K8sConfigDTO configDTO = MyJsonUtils.parse(k8sConfig);
    OkHttpClient httpClient = K8sUtils.getHttpClient(configDTO);
    String result = "ok";
    RequestBody body;
    ApiClient apiClient = k8sApi.getApiClient();
    try {
      if (configDTO != null && httpClient != null) {
        body =apiClient.serialize(yamlVo.getO(),"application/json");
        Request request = new Request.Builder()
            .url(configDTO.getServerUrl() + url).post(body)
            .build();
        Response response =  httpClient.newCall(request).execute();
        result = response.body().string();
      }
    } catch (IOException e) {
      throw new ServiceException(e.getMessage());
    }catch (ApiException apiException){
      throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL, K8sUtils.getMessage(apiException));
    }
    //验证是否有异常
      K8sUtils.getMessage(result);
    return true;
  }
  
  private Boolean appsV1ApiDeploy(K8sYamlDTO k8SYamlDTO, KindEnum kindTypeEnum) throws ApiException {
    AppsV1Api appsV1Api = k8sApi.getAppsV1Api();
    if (appsV1Api != null) {
      switch (kindTypeEnum) {
        case DEPLOYMENT:
          V1Deployment deployment = appsV1Api
              .createNamespacedDeployment(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1Deployment.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (deployment != null) {
            return true;
          }
          break;
        
        case STATEFUL_SET:
          V1StatefulSet statefulSet = appsV1Api
              .createNamespacedStatefulSet(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1StatefulSet.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (statefulSet != null) {
            return true;
          }
          break;
        case DAEMON_SET:
          V1DaemonSet v1beta2DaemonSet = appsV1Api
              .createNamespacedDaemonSet(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1DaemonSet.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1beta2DaemonSet != null) {
            return true;
          }
          break;
        case REPLICA_SET:
          V1ReplicaSet v1beta2ReplicaSet = appsV1Api
              .createNamespacedReplicaSet(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1ReplicaSet.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1beta2ReplicaSet != null) {
            return true;
          }
          break;
        default:
          break;
      }
    }
    return false;
  }
  
  private Boolean rbacAuthorizationK8sIoBetaV1Deploy(K8sYamlDTO k8SYamlDTO, KindEnum kindTypeEnum) throws ApiException {
    RbacAuthorizationV1beta1Api rbacAuthorizationV1beta1Api = k8sApi.getRbacAuthorizationV1beta1Api();
    if (rbacAuthorizationV1beta1Api != null) {
      switch (kindTypeEnum) {
        case CLUSTER_ROLE:
          V1beta1ClusterRole v1beta1ClusterRole = rbacAuthorizationV1beta1Api
              .createClusterRole(K8sUtils.toObject(k8SYamlDTO.getO(), V1beta1ClusterRole.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1beta1ClusterRole != null) {
            return true;
          }
        case ROLE:
          V1beta1Role v1beta1Role = rbacAuthorizationV1beta1Api.createNamespacedRole(k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(), V1beta1Role.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1beta1Role != null) {
            return true;
          }
        case CLUSTER_ROLE_BINDING:
          V1beta1ClusterRoleBinding clusterRoleBinding = rbacAuthorizationV1beta1Api.createClusterRoleBinding(K8sUtils.toObject(k8SYamlDTO.getO(), V1beta1ClusterRoleBinding.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (clusterRoleBinding != null) {
            return true;
          }
          break;
        default:
          return false;
      }
      
    }
    return true;
  }
  
  private Boolean rbacAuthorizationK8sIoV1Deploy(K8sYamlDTO k8SYamlDTO, KindEnum kindTypeEnum) throws ApiException {
    RbacAuthorizationV1Api rbacAuthorizationV1Api = k8sApi.getRbacAuthorizationV1Api();
    
    CoreV1Api coreV1Api = k8sApi.getCoreV1Api();
    if (rbacAuthorizationV1Api != null) {
      switch (kindTypeEnum) {
        case CLUSTER_ROLE:
          V1ClusterRole clusterRole = rbacAuthorizationV1Api
              .createClusterRole(K8sUtils.toObject(k8SYamlDTO.getO(), V1ClusterRole.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (clusterRole != null) {
            return true;
          }
          break;
        case ROLE:
          V1Role v1Role = rbacAuthorizationV1Api.createNamespacedRole(k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(), V1Role.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Role != null) {
            return true;
          }
          break;
        
        case CLUSTER_ROLE_BINDING:
          V1ClusterRoleBinding clusterRoleBinding = rbacAuthorizationV1Api.createClusterRoleBinding(K8sUtils.toObject(k8SYamlDTO.getO(), V1ClusterRoleBinding.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (clusterRoleBinding != null) {
            return true;
          }
          break;
        default:
          return false;
      }
    }
    return true;
  }
  
  private Boolean extensionApiDeploy(K8sYamlDTO k8SYamlDTO, KindEnum kindTypeEnum) throws ApiException {
    ExtensionsV1beta1Api extensionApi = k8sApi.getExtensionsV1beta1Api();
    if (extensionApi != null) {
      switch (kindTypeEnum) {
        
        case DEPLOYMENT:
          //兼容老版本.
          log.info("发布deployment");
          AppsV1Api appsV1Api = k8sApi.getAppsV1Api();
          V1Deployment deployment = appsV1Api
              .createNamespacedDeployment(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1Deployment.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (deployment != null) {
            return true;
          }
          break;
        case INGRESS:
          log.info("发布rs");
          ExtensionsV1beta1Ingress v1beta1Ingress =
              extensionApi.createNamespacedIngress(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), ExtensionsV1beta1Ingress.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1beta1Ingress != null) {
            return true;
          }
          break;
        default:
          break;
      }
    }
    return false;
  }
  
  private Boolean coreV1ApiDeploy(K8sYamlDTO k8SYamlDTO, KindEnum kindTypeEnum) throws ApiException {
    CoreV1Api coreV1Api = k8sApi.getCoreV1Api();
    if (coreV1Api != null) {
      switch (kindTypeEnum) {
        case NAMESPACE:
          V1Namespace v1Namespace = coreV1Api.createNamespace(
              K8sUtils.toObject(k8SYamlDTO.getO(), V1Namespace.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Namespace != null) {
            return true;
          }
          break;
        case POD:
          
          V1Pod v1Pod = coreV1Api.createNamespacedPod(k8SYamlDTO.getNamespace(),
              K8sUtils.toObject(k8SYamlDTO.getO(), V1Pod.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Pod != null) {
            return true;
          }
          
          break;
        case SERVICE:
          V1Service v1Service = coreV1Api.createNamespacedService(
              k8SYamlDTO.getNamespace(),
              K8sUtils.toObject(k8SYamlDTO.getO(), V1Service.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Service != null) {
            return true;
          }
          break;
        case REPLICATION_CONTROLLER:
          V1ReplicationController replicationController =
              coreV1Api.createNamespacedReplicationController(
                  k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(),
                      V1ReplicationController.class),
                  K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (replicationController != null) {
            return true;
          }
          break;
        case CONFIG_MAP:
          V1ConfigMap v1ConfigMap =
              coreV1Api.createNamespacedConfigMap(k8SYamlDTO.getNamespace(),
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1ConfigMap.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1ConfigMap != null) {
            return true;
          }
          
          break;
        case NODE:
          V1Node v1Node =
              coreV1Api.createNode(
                  K8sUtils.toObject(k8SYamlDTO.getO(), V1Node.class), K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Node != null) {
            return true;
          }
          break;
        case ENDPOINTS:
          V1Endpoints v1Endpoints = coreV1Api.createNamespacedEndpoints(
              k8SYamlDTO.getNamespace(),
              K8sUtils.toObject(k8SYamlDTO.getO(), V1Endpoints.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Endpoints != null) {
            return true;
          }
          break;
        case SECRET:
          V1Secret v1Secret = coreV1Api.createNamespacedSecret(
              k8SYamlDTO.getNamespace(),
              K8sUtils.toObject(k8SYamlDTO.getO(), V1Secret.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1Secret != null) {
            return true;
          }
          break;
        case PERSISTENT_VOLUME:
          V1PersistentVolume v1PersistentVolume = coreV1Api.createPersistentVolume(
              K8sUtils.toObject(k8SYamlDTO.getO(), V1PersistentVolume.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1PersistentVolume != null) {
            return true;
          }
          break;
        case PERSISTENT_VOLUME_CLAIM:
          V1PersistentVolumeClaim v1PersistentVolumeClaim = coreV1Api.createNamespacedPersistentVolumeClaim(k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(), V1PersistentVolumeClaim.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (v1PersistentVolumeClaim != null) {
            return true;
          }
        case SERVICE_ACCOUNT:
          V1ServiceAccount namespacedServiceAccount = coreV1Api.createNamespacedServiceAccount(k8SYamlDTO.getNamespace(), K8sUtils.toObject(k8SYamlDTO.getO(), V1ServiceAccount.class),
              K8sParam.CreateParam.pretty, K8sParam.CreateParam.dryRun, K8sParam.CreateParam.fieldManager);
          if (namespacedServiceAccount != null) {
            return true;
          }
          break;
        default:
          break;
      }
    }
    return false;
  }
  
  /**
   * 做的扩缩容
   *
   * @return 返回的是原deployment的副本数
   */
  @Override
  public Integer scaleModuleSize(ScaleYamlDTO scaleYamlVo) {
    String deploymentName = scaleYamlVo.getName();
    String namespace = scaleYamlVo.getNameSpace();
    Integer modulePodSize = scaleYamlVo.getScaleSize();
    Integer replaceReplicas = 0;//修改后数量
    AppsV1Api v1beta1Api = k8sApi.getAppsV1Api();
    CoreV1Api coreV1Api = k8sApi.getCoreV1Api();
    AppsV1Api appsV1Api = k8sApi.getAppsV1Api();
    KindEnum enumByType = KindEnum.getEnumByType(scaleYamlVo.getKind());
    if (v1beta1Api != null && coreV1Api != null && appsV1Api != null) {
      
      log.info("开始进行扩缩容");
      
      V1Scale v1Scale;
      try {
        switch (enumByType) {
          case REPLICA_SET:
            
            v1Scale = v1beta1Api.readNamespacedReplicaSetScale(deploymentName, namespace, K8sParam.ReadParam.pretty);
            
            v1Scale.getSpec().setReplicas(modulePodSize);
            replaceReplicas = v1beta1Api.replaceNamespacedReplicaSetScale(deploymentName,
                namespace, v1Scale, K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager).getSpec().getReplicas();
            break;
          
          case REPLICATION_CONTROLLER:
            v1Scale = coreV1Api.readNamespacedReplicationControllerScale(deploymentName, namespace, K8sParam.ReadParam.pretty);
            
            v1Scale.getSpec().setReplicas(modulePodSize);
            replaceReplicas = coreV1Api.replaceNamespacedReplicationControllerScale(deploymentName, namespace, v1Scale, K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager).getSpec().getReplicas();
            break;
          case DEPLOYMENT:
            v1Scale = v1beta1Api.readNamespacedDeploymentScale(deploymentName, namespace, K8sParam.ReadParam.pretty);
            
            v1Scale.getSpec().setReplicas(modulePodSize);
            replaceReplicas = v1beta1Api.replaceNamespacedDeploymentScale(deploymentName,
                namespace, v1Scale, K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager).getSpec().getReplicas();
            break;
          case STATEFUL_SET:
            v1Scale = appsV1Api.readNamespacedStatefulSetScale(deploymentName, namespace, K8sParam.ReadParam.pretty);
            
            v1Scale.getSpec().setReplicas(modulePodSize);
            replaceReplicas = appsV1Api.replaceNamespacedStatefulSetScale(deploymentName,
                namespace, v1Scale, K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager).getSpec().getReplicas();
            break;
          default:
            break;
        }
      } catch (ApiException e) {
        
        throw new ServiceException(ResultCode.RESOURCE_SCALE_FAIL, K8sUtils.getMessage(e));
      }
    }
    log.info("当前的replicas为:{}", replaceReplicas);
    
    return replaceReplicas;
  }
  
  /**
   * 下线对应的资源  删除资源 并删除级联的全部依赖譬如deployment即删除rs等资源
   *
   * @return
   */
  @Override
  public void deleteNamespacedResource(YamlBaseDTO yamlBaseDTO) {
    String sourceName = yamlBaseDTO.getName();
    
    String namespace = yamlBaseDTO.getNameSpace();
    log.info("开始下线某个应用: {}", sourceName);
    KindEnum enumByType = KindEnum.getEnumByType(yamlBaseDTO.getKind());
    //设置3s的优雅下线机制
    //如果不是管理 员,操作集群资源,则返回错误
    //如果不是管理员,操作集群资源 是操作别人的命名空间,则返回错误
    if(!k8sApi.isK8sManage()&&(KindEnum.isK8sCluster(enumByType)||(KindEnum.NAMESPACE==enumByType&&!k8sApi.isUserNameSapce(sourceName)))){
    
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    Long gracePeriodSeconds = 3L;
    if (enumByType != null) {
      V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
      v1DeleteOptions.setGracePeriodSeconds(gracePeriodSeconds);
      //v1DeleteOptions.setPropagationPolicy("Foreground");//删除控制器之前，所管理的资源对象必须先删除
      CoreV1Api coreV1Api = k8sApi.getCoreV1Api();
      RbacAuthorizationV1Api rbacAuthorizationV1Api = k8sApi.getRbacAuthorizationV1Api();
      BatchV1beta1Api batchV1beta1Api = k8sApi.getBatchV1beta1Api();
      ApiextensionsV1beta1Api apiextensionsV1beta1Api = k8sApi.getApiextensionsV1beta1Api();
      AppsV1Api appsV1Api = k8sApi.getAppsV1Api();
      if (coreV1Api != null && appsV1Api != null) {
        try {
          okhttp3.Call call = null;
          switch (enumByType) {
            case NAMESPACE:
              
              call = coreV1Api.deleteNamespaceCall(sourceName, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              
              break;
            case DEPLOYMENT:
              //先将副本数设置为0 在进行后续的操作
              call = appsV1Api.deleteNamespacedDeploymentCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case POD:
              call = coreV1Api.deleteNamespacedPodCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case SERVICE:
              call = coreV1Api.deleteNamespacedServiceCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case DAEMON_SET:
              call = appsV1Api.deleteNamespacedDaemonSetCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case REPLICA_SET:
              call = appsV1Api.deleteNamespacedReplicaSetCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case REPLICATION_CONTROLLER:
              call = coreV1Api.deleteNamespacedReplicationControllerCall(sourceName,
                  namespace,
                  K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case INGRESS:
              ExtensionsV1beta1Api v1beta1Api = k8sApi.getExtensionsV1beta1Api();
              call = v1beta1Api.deleteNamespacedIngressCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case CONFIG_MAP:
              call = coreV1Api.deleteNamespacedConfigMapCall(sourceName, namespace,
                  K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case NODE:
              
              call = coreV1Api.deleteNodeCall(sourceName,
                  K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              
              break;
            case STATEFUL_SET:
              
              call = appsV1Api.deleteNamespacedStatefulSetCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              
              break;
            case ENDPOINTS:
              call = coreV1Api.deleteNamespacedEndpointsCall(sourceName, namespace,
                  K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case JOB:
              BatchV1Api batchV1Api = k8sApi.getBatchV1Api();
              
              call = batchV1Api.deleteNamespacedJobCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case ROLE:
              call = rbacAuthorizationV1Api.deleteNamespacedRoleCall(sourceName, namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case CLUSTER_ROLE:
              call = rbacAuthorizationV1Api.deleteClusterRoleCall(sourceName, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case SECRET:
              call = coreV1Api.deleteNamespacedSecretCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            
            case PERSISTENT_VOLUME:
              call = coreV1Api.deletePersistentVolumeCall(sourceName, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case CRON_JOB:
              call = batchV1beta1Api.deleteNamespacedCronJobCall(sourceName,
                  namespace, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            
            case CUSTOM_RESOURCE_DEFINITION:
              
              call = apiextensionsV1beta1Api.deleteCustomResourceDefinitionCall(sourceName, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case STORAGE_CLASS:
              StorageV1Api storageV1Api = k8sApi.getStorageV1Api();
              call = storageV1Api.deleteStorageClassCall(sourceName, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case PERSISTENT_VOLUME_CLAIM:
              call = coreV1Api.deleteNamespacedPersistentVolumeClaimCall(sourceName, namespace,
                  K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case SERVICE_ACCOUNT:
              call = coreV1Api.deleteNamespacedServiceAccountCall(sourceName, namespace,
                  K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            case CLUSTER_ROLE_BINDING:
              call = rbacAuthorizationV1Api.deleteClusterRoleBindingCall(sourceName, K8sParam.DeleteParam.pretty, K8sParam.DeleteParam.dryRun, K8sParam.DeleteParam.gracePeriodSeconds, K8sParam.DeleteParam.orphanDependents, K8sParam.DeleteParam.propagationPolicy, v1DeleteOptions, null);
              break;
            default:
              break;
          }
          if(call!=null){
  
            call.execute();
          }
          
          
        } catch (ApiException e) {
          throw new ServiceException(ResultCode.RESOURCE_DELETE_FAIL, K8sUtils.getMessage(e));
        } catch (IOException e) {
          throw new ServiceException(ResultCode.RESOURCE_DELETE_FAIL, e.getMessage());
        }
      }
    }
  }
  
  /**
   * 根据资源名称获取资源的详细内容通过okHttp的方式
   *
   * @return
   */
  @Override
  public JSONObject getResourceByNameUseOKHttp(YamlBaseDTO readYamlVo) {
    String resourceName = readYamlVo.getName();
    String namespace = readYamlVo.getNameSpace();
    
    String yamlJsonBack = "";
    JSONObject object = null;
    if (StringUtils.isNotBlank(resourceName)) {
      if (StringUtils.isBlank(namespace)) {
        namespace = K8sObject.NAMESPACE;
      }
      KindEnum enumByType = KindEnum.getEnumByType(readYamlVo.getKind());
      StringBuilder url = new StringBuilder();
      okhttp3.Call call = null;
      AppsV1Api appsV1Api = k8sApi.getAppsV1Api();
      CoreV1Api coreV1Api = k8sApi.getCoreV1Api();
      ExtensionsV1beta1Api extensionsV1beta1Api = k8sApi.getExtensionsV1beta1Api();
      BatchV1beta1Api batchV1beta1Api = k8sApi.getBatchV1beta1Api();
      BatchV1Api batchV1Api = k8sApi.getBatchV1Api();
      RbacAuthorizationV1Api rbacAuthorizationV1Api = k8sApi.getRbacAuthorizationV1Api();
      ApiextensionsV1beta1Api apiextensionsV1Api = k8sApi.getApiextensionsV1beta1Api();
      StorageV1Api storageV1Api = k8sApi.getStorageV1Api();
      try {
        if (enumByType != null) {
          
          switch (enumByType) {
            case NAMESPACE:
              
              call = coreV1Api.readNamespaceCall(resourceName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              
              break;
            case DEPLOYMENT:
              
              call = appsV1Api.readNamespacedDeploymentCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              
              break;
            case POD:
              call = coreV1Api.readNamespacedPodCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case SERVICE:
              call = coreV1Api.readNamespacedServiceCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case DAEMON_SET:
              
              call = appsV1Api.readNamespacedDaemonSetCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case REPLICA_SET:
              call = appsV1Api.readNamespacedReplicaSetCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case REPLICATION_CONTROLLER:
              call = coreV1Api.readNamespacedReplicationControllerCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case INGRESS:
              call = extensionsV1beta1Api.readNamespacedIngressCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case CONFIG_MAP:
              call = coreV1Api.readNamespacedConfigMapCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case NODE:
              call = coreV1Api.readNodeCall(resourceName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              
              break;
            case STATEFUL_SET:
              call = appsV1Api.readNamespacedStatefulSetCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              
              break;
            case ENDPOINTS:
              call = coreV1Api.readNamespacedEndpointsCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case JOB:
              
              call = batchV1Api.readNamespacedJobCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case ROLE:
              
              call = rbacAuthorizationV1Api.readNamespacedRoleCall(resourceName, namespace, K8sParam.ReadParam.pretty, null);
              
              break;
            case CLUSTER_ROLE:
              call = rbacAuthorizationV1Api.readClusterRoleCall(resourceName, K8sParam.ReadParam.pretty, null);
              break;
            case SECRET:
              call = coreV1Api.readNamespacedSecretCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case PERSISTENT_VOLUME:
              call = coreV1Api.readPersistentVolumeCall(resourceName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case CRON_JOB:
              call = batchV1beta1Api.readNamespacedCronJobCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case CUSTOM_RESOURCE_DEFINITION:
              call = apiextensionsV1Api.readCustomResourceDefinitionCall(resourceName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case STORAGE_CLASS:
              call = storageV1Api.readStorageClassCall(resourceName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case PERSISTENT_VOLUME_CLAIM:
              call = coreV1Api.readNamespacedPersistentVolumeClaimCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case SERVICE_ACCOUNT:
              call = coreV1Api.readNamespacedServiceAccountCall(resourceName, namespace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export, null);
              break;
            case CLUSTER_ROLE_BINDING:
              call = rbacAuthorizationV1Api.readClusterRoleBindingCall(resourceName, K8sParam.ReadParam.pretty, null);
              break;
            default:
              break;
          }
          
          if (call != null) {
            Response response = null;
            
            response = call.execute();
            
            if (response.body() != null) {
              yamlJsonBack = response.body().string();
            }
          }
          log.info("ok_http 获取资源url:{} back:{}", url.toString(), yamlJsonBack);
          if (StringUtils.isNotBlank(yamlJsonBack)) {
            //判断是否为404的状态来确定资源是否找到
            object = com.alibaba.fastjson.JSON.parseObject(yamlJsonBack);
            
          }
        }
      } catch (ApiException e) {
        
        throw new ServiceException(ResultCode.RESOURCE_DETAIL_FAIL, K8sUtils.getMessage(e));
      } catch (IOException e) {
        
        throw new ServiceException(ResultCode.RESOURCE_DETAIL_FAIL);
      }
    }
    return object;
  }
  
  @Override
  public void updateResource(K8sYamlDTO k8SYamlDTO) {
    Object obj = k8SYamlDTO.getO();
    String name = k8SYamlDTO.getMetadataName();
    String nameSpace = k8SYamlDTO.getNamespace();
    String kind = k8SYamlDTO.getKind();
    if (StringUtils.isNotBlank(kind) && StringUtils.isNotBlank(name)) {
      if (StringUtils.isBlank(nameSpace)) {
        nameSpace = K8sObject.NAMESPACE;
      }
      KindEnum enumByType = KindEnum.getEnumByType(kind);
      //如果不是管理员,操作集群资源 是操作别人的命名空间,则返回错误
      if(!k8sApi.isK8sManage()&&(KindEnum.isK8sCluster(enumByType)||(KindEnum.NAMESPACE==enumByType&&!k8sApi.isUserNameSapce(name)))){
        
        throw new ServiceException(ResultCode.FORBIDDEN);
      }
      CoreV1Api coreV1Api = k8sApi.getCoreV1Api();
      BatchV1Api batchV1Api = k8sApi.getBatchV1Api();
      ExtensionsV1beta1Api extensionsV1beta1Api = k8sApi.getExtensionsV1beta1Api();
      AppsV1Api appsV1Api = k8sApi.getAppsV1Api();
      ApiClient apiClient = k8sApi.getApiClient();
      ApiextensionsV1beta1Api v1beta1Api = k8sApi.getApiextensionsV1beta1Api();
      RbacAuthorizationV1Api rbacAuthorizationV1Api = k8sApi.getRbacAuthorizationV1Api();
      BatchV1beta1Api batchV1beta1Api = k8sApi.getBatchV1beta1Api();
      StorageV1Api storageV1Api = k8sApi.getStorageV1Api();
      
      try {
        if (enumByType != null) {
          switch (enumByType) {
            case NAMESPACE:
              
              coreV1Api.replaceNamespace(name, K8sUtils.toObject(obj, V1Namespace.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              
              return;
            case DEPLOYMENT: //部署
              
              appsV1Api.replaceNamespacedDeployment(name, nameSpace, K8sUtils.toObject(obj, V1Deployment.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case POD://Pod
              coreV1Api.replaceNamespacedPod(name, nameSpace, K8sUtils.toObject(obj, V1Pod.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case SERVICE:  //服务
              coreV1Api.replaceNamespacedService(name, nameSpace, K8sUtils.toObject(obj, V1Service.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case DAEMON_SET://守护进程集
              
              appsV1Api.replaceNamespacedDaemonSet(name, nameSpace, K8sUtils.toObject(obj, V1DaemonSet.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case REPLICA_SET: //副本集
              
              appsV1Api.replaceNamespacedReplicaSet(name, nameSpace, K8sUtils.toObject(obj, V1ReplicaSet.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case REPLICATION_CONTROLLER://副本控制器
              coreV1Api.replaceNamespacedReplicationController(name, nameSpace, K8sUtils.toObject(obj, V1ReplicationController.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              
              return;
            
            case INGRESS://访问权
              extensionsV1beta1Api.replaceNamespacedIngress(name, nameSpace, K8sUtils.toObject(obj, ExtensionsV1beta1Ingress.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case CONFIG_MAP://配置字典
              coreV1Api.replaceNamespacedConfigMap(name, nameSpace, K8sUtils.toObject(obj, V1ConfigMap.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            
            case NODE://节点
              coreV1Api.replaceNode(name, K8sUtils.toObject(obj, V1Node.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case STATEFUL_SET://有状态应用副本集
              
              appsV1Api.replaceNamespacedStatefulSet(name, nameSpace, K8sUtils.toObject(obj, V1StatefulSet.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            
            case ENDPOINTS:
              coreV1Api.replaceNamespacedEndpoints(name, nameSpace, K8sUtils.toObject(obj, V1Endpoints.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case JOB: //任务
              batchV1Api.replaceNamespacedJob(name, nameSpace, K8sUtils.toObject(obj, V1Job.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case ROLE://角色
              rbacAuthorizationV1Api.replaceNamespacedRole(name, nameSpace, K8sUtils.toObject(obj, V1Role.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case CLUSTER_ROLE://集群角色
              rbacAuthorizationV1Api.replaceClusterRole(name, K8sUtils.toObject(obj, V1ClusterRole.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case SECRET://保密字典
              coreV1Api.replaceNamespacedSecret(name, nameSpace, K8sUtils.toObject(obj, V1Secret.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            
            case PERSISTENT_VOLUME://持久化存储卷
              coreV1Api.replacePersistentVolume(name, K8sUtils.toObject(obj, V1PersistentVolume.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case CRON_JOB:
              batchV1beta1Api.replaceNamespacedCronJob(name, nameSpace, K8sUtils.toObject(obj, V1beta1CronJob.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            
            case CUSTOM_RESOURCE_DEFINITION://自定义资源
              v1beta1Api.replaceCustomResourceDefinition(name, K8sUtils.toObject(obj, V1beta1CustomResourceDefinition.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            
            case STORAGE_CLASS:
              storageV1Api.replaceStorageClass(name, K8sUtils.toObject(obj, V1StorageClass.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case PERSISTENT_VOLUME_CLAIM:
              coreV1Api.replaceNamespacedPersistentVolumeClaim(name, nameSpace, K8sUtils.toObject(obj, V1PersistentVolumeClaim.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case SERVICE_ACCOUNT:
              coreV1Api.replaceNamespacedServiceAccount(name, nameSpace, K8sUtils.toObject(obj, V1ServiceAccount.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            case CLUSTER_ROLE_BINDING:
              rbacAuthorizationV1Api.replaceClusterRoleBinding(name, K8sUtils.toObject(obj, V1ClusterRoleBinding.class), K8sParam.ReplaceParam.pretty, K8sParam.ReplaceParam.dryRun, K8sParam.ReplaceParam.fieldManager);
              return;
            default:
              return;
          }
          
        }
        crdResourcePut(k8SYamlDTO);
      } catch (ApiException e) {
        
        throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL, K8sUtils.getMessage(e));
      }
    }
  }
//
//  @Override
//  public void crdResource(K8sYamlDTO yamlVo,String method) {
//    ApiClient apiClient = k8sApi.getApiClient();
//    Object localVarPostBody =K8sUtils.toObject(yamlVo.getO(), Object.class) ;
//    String localVarPath = yamlVo.getSelfLink();
//    List<Pair> localVarQueryParams = new ArrayList<Pair>();
//    List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
//    if (ReplaceParam.pretty != null) {
//      localVarQueryParams.addAll(apiClient.parameterToPair("pretty", ReplaceParam.pretty));
//    }
//
//    if (ReplaceParam.dryRun != null) {
//      localVarQueryParams.addAll(apiClient.parameterToPair("dryRun", ReplaceParam.dryRun));
//    }
//
//    if (ReplaceParam.fieldManager != null) {
//      localVarQueryParams.addAll(apiClient.parameterToPair("fieldManager", ReplaceParam.fieldManager));
//    }
//
//    Map<String, String> localVarHeaderParams = new HashMap<String, String>();
//    Map<String, String> localVarCookieParams = new HashMap<String, String>();
//    Map<String, Object> localVarFormParams = new HashMap<String, Object>();
//    final String[] localVarAccepts = {
//        "application/json", "application/yaml", "application/vnd.kubernetes.protobuf"
//    };
//    final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
//    if (localVarAccept != null) {
//      localVarHeaderParams.put("Accept", localVarAccept);
//    }
//
//    final String[] localVarContentTypes = {};
//
//    final String localVarContentType =
//        apiClient.selectHeaderContentType(localVarContentTypes);
//    localVarHeaderParams.put("Content-Type", localVarContentType);
//    String[] localVarAuthNames = new String[] {"BearerToken"};
//    try {
//      apiClient.buildCall(
//          localVarPath,
//          method,
//          localVarQueryParams,
//          localVarCollectionQueryParams,
//          localVarPostBody,
//          localVarHeaderParams,
//          localVarCookieParams,
//          localVarFormParams,
//          localVarAuthNames,
//          null);
//    } catch (ApiException e) {
//      throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL, K8sUtils.getMessage(e));
//    }
//  }
//
  @Override
  public String crdResourcePut(K8sYamlDTO yamlVo) {
    String k8sConfig = k8sApi.getK8sConfig();
    String url =yamlVo.getSelfLink();
    K8sConfigDTO configDTO = MyJsonUtils.parse(k8sConfig);
    OkHttpClient httpClient = K8sUtils.getHttpClient(configDTO);
    String result = "ok";
    RequestBody body;
    ApiClient apiClient = k8sApi.getApiClient();
    try {
    if (configDTO != null && httpClient != null) {
      body =apiClient.serialize(yamlVo.getO(),"application/json");
      Request request = new Request.Builder()
          .url(configDTO.getServerUrl() + url).put(body)
          .build();
      Response response =  httpClient.newCall(request).execute();
        result = response.body().string();
   
    }
    } catch (IOException e) {
      throw new ServiceException(e.getMessage());
    }catch (ApiException apiException){
      throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL, K8sUtils.getMessage(apiException));
    }
    return K8sUtils.getMessage(result);
  
  }
  @Override
  public String crdResourceDelete(String url) {
    String k8sConfig = k8sApi.getK8sConfig();
    if(StringUtils.isBlank(url))
      throw new ServiceException(ResultCode.REQUEST_PARA_ERROR);
    
    K8sConfigDTO configDTO = MyJsonUtils.parse(k8sConfig);
    ApiClient apiClient = k8sApi.getApiClient();
    OkHttpClient httpClient = K8sUtils.getHttpClient(configDTO);
    String result = "ok";
    V1DeleteOptions v1DeleteOptions = new V1DeleteOptions();
    v1DeleteOptions.setGracePeriodSeconds(3L);
    Request request =null;
    try {
      RequestBody  body =apiClient.serialize(v1DeleteOptions,"application/json");
        request = new Request.Builder()
          .url(configDTO.getServerUrl() +url)
          .delete(body)
          .build();
      Response response =  httpClient.newCall(request).execute();
      result = response.body().string();
    } catch (IOException e) {
      throw new ServiceException(e.getMessage());
    }  catch (ApiException apiException){
      throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL, K8sUtils.getMessage(apiException));
    }
    return K8sUtils.getMessage(result);
  }
  
  
  @Override
  public String executeHttpGetBack(String url) {
    String k8sConfig = k8sApi.getK8sConfig();
    K8sConfigDTO configDTO = MyJsonUtils.parse(k8sConfig);
    OkHttpClient httpClient = K8sUtils.getHttpClient(configDTO);
    String back = null;
    if (configDTO != null && httpClient != null) {
      Request request = new Request.Builder()
          .url(configDTO.getServerUrl() + url)
          .build();
      
      Response response = null;
      try {
        response = httpClient.newCall(request).execute();
        
        back = response.body().string();
      } catch (IOException e) {
        throw new ServiceException(e.getMessage());
        
      }
      
    }
    return back;
  }
  
}
