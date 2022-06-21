package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.Pod;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.AllocatedResourcesVo;
import com.pubinfo.resource.model.vo.PodVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.common.K8sService;
import com.pubinfo.resource.service.namespace.*;
import com.pubinfo.resource.service.namespace.*;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import okhttp3.Call;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PodServiceImpl extends K8sSearch implements PodService {
  @Autowired
  DeploymentService deploymentService;
  @Autowired
  JobService jobService;
  @Autowired
  ReplicaSetService replicaSetService;
  @Autowired
  K8sApiService apiService;
  @Autowired
  EventsService eventsService;
  @Autowired
  StatefulSetService statefulSetService;
  @Autowired
  K8sService k8sService;
  @Autowired
  ReplicationControllerService replicationControllerService;
  @Autowired
  DaemonSetService daemonSetService;
  
  @Override
  public Page<PodVo> listPod(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = apiService.getCoreV1Api();
    List<V1Pod> items = null;
    String namespace = apiService.getNamespace();
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        items = coreV1Api.listPodForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
        
        items = filterByNamespaces(items, V1Pod::getMetadata, namespace);
      } else {
        items = coreV1Api.listNamespacedPod(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_POD_FAIL, K8sUtils.getMessage(e));
    }
    return toPodPage(items, paramVo, namespace);
  }
  
  @Override
  public Page<PodVo> toPodPage(List<V1Pod> items, SearchParamDTO paramVo, String namespace) {
    List<V1Pod> list = pagingOrder(paramVo, items, V1Pod::getMetadata, namespace);
    List<Pod> pods = new ArrayList<>();
    if (list != null && !list.isEmpty()) {
      list.stream().forEach(v1Pod -> {
        List<Event> warnings = eventsService.listNamespacedEventByWarning(v1Pod.getMetadata(), null);
        Pod pod = new Pod(v1Pod);
        pod.setWarnings(warnings);
        pods.add(pod);
      });
    }
    PodVo vo = new PodVo(new AllocatedResourcesVo(list), pods, items);
    
    Page<PodVo> page = new Page<>(paramVo, vo, getTotalItem(), list==null?0:list.size());
    return page;
  }
  
  @Override
  public V1PodList listV1PodByNode(String nodeName,Boolean isFilter) {
    CoreV1Api coreV1Api = apiService.getCoreV1Api();
    String fieldSelector = "";
    String namespaces = apiService.getNamespace();
    if (!StringUtils.isEmpty(nodeName)) {
      fieldSelector += "spec.nodeName=" + nodeName;
    }
    fieldSelector += ",status.phase!=Failed,status.phase!=Succeeded";
    V1PodList list = null;
    try {
      list = coreV1Api.listPodForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
      if(isFilter){
        list.setItems(filterByNamespaces(list.getItems(), V1Pod::getMetadata, namespaces));
      }
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.QUERY_POD_FAIL, K8sUtils.getMessage(e));
    }
    return list;
  }
  
  @Override
  public Page<PodVo> listPodByNode(SearchParamDTO paramVo, String nodeName) {
    String namespace = apiService.getNamespace();
    List<V1Pod> items = listV1PodByNode(nodeName,true).getItems();
    return toPodPage(items, paramVo, namespace);
  }
  
  @Override
  public V1PodList listV1Pod(V1ObjectMeta v1ObjectMeta, boolean isLabel, String kind) {
    V1PodList v1PodList = null;
    CoreV1Api coreV1Api = apiService.getCoreV1Api();
    try {
      String namespace = v1ObjectMeta.getNamespace();
      String fieldSelector = null;
      String labelSelector = null;
      if (isLabel) {
        labelSelector = K8sUtils.generateLabel(v1ObjectMeta);
      } else {
        fieldSelector = K8sUtils.generateSelector(v1ObjectMeta, kind);
      }
      if (!StringUtils.isEmpty(namespace)) {
        v1PodList = coreV1Api.listNamespacedPod(namespace,
            ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, fieldSelector,
            labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      } else {
        v1PodList = coreV1Api.listPodForAllNamespaces(
            
            ListParam.allowWatchBookmarks, ListParam._continue, fieldSelector,
            labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_POD_FAIL, K8sUtils.getMessage(e));
    }
    //过滤掉父类不是pod的项.
    if (!"Deployment".equalsIgnoreCase(kind)) {
      v1PodList.setItems(v1PodList.getItems().stream().filter(v1Pod -> v1Pod.getMetadata().getOwnerReferences().get(0).getName().equals(v1ObjectMeta.getName())).collect(Collectors.toList()));
    }
    
    return v1PodList;
  }
  
  @Override
  public V1PodList listV1Pod(V1LabelSelector v1LabelSelector, String namespace, String ownerReferencesName) {
    Map<String, String> selector = v1LabelSelector.getMatchLabels();
    V1PodList v1PodList = listV1Pod(selector, namespace);
    return filterOwnerReferences(v1PodList, ownerReferencesName);
  }
  
  @Override
  public V1PodList listV1Pod(V1LabelSelector v1LabelSelector, String namespace) {
    Map<String, String> selector = v1LabelSelector.getMatchLabels();
    return listV1Pod(selector, namespace);
  }
  
  @Override
  public V1PodList listV1Pod(Map<String, String> selector, String namespace) {
    String labelSelector = K8sUtils.generateString(selector);
    V1PodList v1PodList = null;
    CoreV1Api coreV1Api = apiService.getCoreV1Api();
    try {
      if (!StringUtils.isEmpty(namespace)) {
        v1PodList = coreV1Api.listNamespacedPod(namespace,
            ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector,
            labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      } else {
        
        v1PodList = coreV1Api.listPodForAllNamespaces(
            
            ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector,
            labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      }
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.QUERY_POD_FAIL, K8sUtils.getMessage(e));
    }
    return v1PodList;
  }
  
  @Override
  public V1PodList filterOwnerReferences(V1PodList v1PodList, String ownerReferencesName) {
    V1PodList podList = new V1PodList();
    if (v1PodList != null && v1PodList.getItems().size() > 0) {
      v1PodList.getItems().forEach(v1Pod -> {
        List<V1OwnerReference> v1OwnerReferences = v1Pod.getMetadata().getOwnerReferences();
        if (v1OwnerReferences != null && v1OwnerReferences.size() > 0) {
          if (ownerReferencesName.equals(v1OwnerReferences.get(0).getName())) {
            podList.addItemsItem(v1Pod);
            
          }
        }
      });
    }
    return podList;
  }
  
  @Override
  public V1PodList filterOwnerReferences(V1PodList v1PodList, V1ReplicaSetList v1ReplicaSetList) {
    V1PodList podList = new V1PodList();
    if (v1ReplicaSetList != null && v1ReplicaSetList.getItems().size() > 0) {
      v1ReplicaSetList.getItems().forEach(v1ReplicaSet -> {
        String ownerMetaName = v1ReplicaSet.getMetadata().getName();
        if (v1PodList != null && v1PodList.getItems().size() > 0) {
          v1PodList.getItems().forEach(v1Pod -> {
            List<V1OwnerReference> v1OwnerReferences = v1Pod.getMetadata().getOwnerReferences();
            if (v1OwnerReferences != null && v1OwnerReferences.size() > 0) {
              if (ownerMetaName.equals(v1OwnerReferences.get(0).getName())) {
                podList.addItemsItem(v1Pod);
              }
            }
          });
        }
      });
    }
    return podList;
  }
  
  @Override
  public Pod readPod(String nameSpace, String name) {
    if (!apiService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    CoreV1Api coreV1Api = apiService.getCoreV1Api();
    V1Pod v1Pod = null;
    try {
      v1Pod = coreV1Api.readNamespacedPod(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      if (e.getCode() == 404) {
        throw new ServiceException(ResultCode.NOT_FOUND_SOURCE, K8sUtils.getMessage(e));
      } else {
        throw new ServiceException(ResultCode.UNKNOWN_EXCEPTION, K8sUtils.getMessage(e));
      }
      
    }
    Pod pod = new Pod(v1Pod);
    List<Event> podEventList = eventsService.listNamespacedEvent(v1Pod.getMetadata(), null);
    pod.setEventList(podEventList);
    
    List<V1OwnerReference> ownerReferences = v1Pod.getMetadata().getOwnerReferences();
    Builder builder = new Builder();
    if (CollectionUtils.isNotEmpty(ownerReferences)) {
      V1OwnerReference or = ownerReferences.get(0);
      if ("job".equalsIgnoreCase(or.getKind())) {
        builder = jobService.readBuilder(nameSpace, or.getName());
      } else if ("Deployment".equalsIgnoreCase(or.getKind())) {
        builder = deploymentService.readBuilder(nameSpace, or.getName());
      } else if ("ReplicaSet".equalsIgnoreCase(or.getKind())) {
        builder = replicaSetService.readBuilder(nameSpace, or.getName());
      } else if ("ReplicationController".equalsIgnoreCase(or.getKind())) {
        builder = replicationControllerService.readBuilder(nameSpace, or.getName());
      } else if ("StatefulSet".equalsIgnoreCase(or.getKind())) {
        builder = statefulSetService.readBuilder(nameSpace, or.getName());
      } else if ("DaemonSet".equalsIgnoreCase(or.getKind())) {
        builder = daemonSetService.readBuilder(nameSpace, or.getName());
      }
    }
    pod.setController(builder);
    
    return pod;
  }
  
  @Override
  public Call namespacedPodLogCall(Integer envId, String nameSpace, String podName, String containerName) {
    CoreV1Api coreV1Api = apiService.getCoreV1Api(envId);
    Call call;
    try {
      if (StringUtils.isEmpty(nameSpace) || StringUtils.isEmpty(podName) || StringUtils.isEmpty(containerName)) {
        return null;
      }
      call = coreV1Api.readNamespacedPodLogCall(podName, nameSpace, containerName,
          null, null, 1048576, null, null,
          null, null, null, null);
    } catch (ApiException e) {
      throw new ServiceException("获取命名空间:" + nameSpace + " 容器组名称" + podName + "容器名称:" + containerName + "日志失败!!");
    }
    return call;
  }
  
  @Override
  public V1PodList listV1PodByLabelSelector(Map<String, String> selectorMap, String nameSpace) {
    V1PodList v1PodList = null;
    CoreV1Api coreV1Api = apiService.getCoreV1Api();
    try {
      String selector = K8sUtils.generateString(selectorMap);
      if (!StringUtils.isEmpty(nameSpace)) {
        v1PodList = coreV1Api.listNamespacedPod(nameSpace,
            ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, selector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_POD_FAIL, K8sUtils.getMessage(e));
    }
    
    return v1PodList;
  }
  
}
