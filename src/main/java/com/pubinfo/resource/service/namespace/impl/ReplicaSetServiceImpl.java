package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.ReplicaSet;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.ReplicaSetVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ReplicaSetService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@org.springframework.stereotype.Service
public class ReplicaSetServiceImpl extends K8sSearch implements ReplicaSetService {
  @Autowired
  K8sApiService k8sService;
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  @Autowired
  ServiceOfService serviceOfService;
  
  @Override
  public Page<ReplicaSetVo> listReplicaSet(SearchParamDTO paramVo) {
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    List<V1ReplicaSet> items;
    List<ReplicaSet> replicaSets = new ArrayList<>();
    String namespace = k8sService.getNamespace();
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        items = appsV1Api.listReplicaSetForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch
        ).getItems();
        items = filterByNamespaces(items, V1ReplicaSet::getMetadata, namespace);
        
      } else {
        items = appsV1Api.listNamespacedReplicaSet(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      }
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_REPLICASET_FAIL, K8sUtils.getMessage(e));
    }
    List<V1ReplicaSet> replicaSetList = pagingOrder(paramVo, items, V1ReplicaSet::getMetadata, namespace);
    
    if (replicaSetList != null && !replicaSetList.isEmpty()) {
      for (V1ReplicaSet v1beta1ReplicaSet : replicaSetList) {
        replicaSets.add(toReplicaSet(v1beta1ReplicaSet));
      }
    }
    
    ReplicaSetVo replicaSetVo = new ReplicaSetVo(replicaSets, items);
    Page<ReplicaSetVo> page = new Page<>(paramVo, replicaSetVo, getTotalItem(), replicaSets.size());
    return page;
  }
  
  @Override
  public ReplicaSet toReplicaSet(V1ReplicaSet v1beta1ReplicaSet) {
//    V1PodList v1PodList = podService.listV1Pod(v1beta1ReplicaSet.getMetadata(), true, "ReplicaSet");
    
    V1PodList v1PodList = podService.listV1Pod(v1beta1ReplicaSet.getSpec().getSelector(), v1beta1ReplicaSet.getMetadata().getNamespace(), v1beta1ReplicaSet.getMetadata().getName());
    //Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    
    return new ReplicaSet().initReplicaSet(v1beta1ReplicaSet, v1PodList, null);
  }
  
  @Override
  public ReplicaSet readReplicaSet(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    V1ReplicaSet v1beta1ReplicaSet = null;
    try {
      v1beta1ReplicaSet = appsV1Api.readNamespacedReplicaSet(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_REPLICASET_FAIL, K8sUtils.getMessage(e));
    }
    
    V1PodList v1PodList = podService.listV1Pod(v1beta1ReplicaSet.getSpec().getSelector(), v1beta1ReplicaSet.getMetadata().getNamespace(), v1beta1ReplicaSet.getMetadata().getName());
    List<Event> podEventList = eventsService.listNamespacedEventByWarning(v1beta1ReplicaSet.getMetadata(), null);
    List<V1Service> v1ServiceList = serviceOfService.listServiceByLabel(nameSpace,v1beta1ReplicaSet.getSpec().getSelector());
    ReplicaSet replicaSet = new ReplicaSet(v1beta1ReplicaSet, v1PodList, null, podEventList, v1ServiceList);
    return replicaSet;
  }
  
  @Override
  public Builder readBuilder(String nameSpace, String name) {
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    V1ReplicaSet v1beta1ReplicaSet = null;
    try {
      v1beta1ReplicaSet = appsV1Api.readNamespacedReplicaSet(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_REPLICASET_FAIL, K8sUtils.getMessage(e));
    }
    Builder builder = toReplicaSet(v1beta1ReplicaSet);
    return builder;
  }
  
  @Override
  public ReplicaSetVo listByLabel(V1ObjectMeta v1ObjectMeta, String ownerReferencesName) {
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    List<V1ReplicaSet> items;
    List<ReplicaSet> replicaSets = new ArrayList<>();
    
    String namespace = v1ObjectMeta.getNamespace();
    String lables = K8sUtils.generateLabel(v1ObjectMeta);
    try {
      items = appsV1Api.listNamespacedReplicaSet(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, lables, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_REPLICASET_FAIL, K8sUtils.getMessage(e));
    }
    
    if (items != null && !items.isEmpty()) {
      for (V1ReplicaSet v1beta1ReplicaSet : items) {
        V1PodList v1PodList = podService.listV1Pod(v1beta1ReplicaSet.getSpec().getSelector(), v1beta1ReplicaSet.getMetadata().getNamespace(), v1beta1ReplicaSet.getMetadata().getName());
        
        Set<Event> eventList = eventsService.generateEventSet(v1PodList);
        ReplicaSet replicaSet = new ReplicaSet().initReplicaSet(v1beta1ReplicaSet, v1PodList, eventList);
        replicaSets.add(replicaSet);
      }
    }
    ReplicaSetVo replicaSetVo = new ReplicaSetVo();
    replicaSetVo.setReplicaSets(replicaSets);
    return replicaSetVo;
  }
  
  @Override
  public V1ReplicaSetList listByLabel2(V1ObjectMeta v1ObjectMeta, V1LabelSelector v1LabelSelector,String ownerReferencesName) {
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    List<V1ReplicaSet> items;
    String namespace = v1ObjectMeta.getNamespace();
    String labelSelector = K8sUtils.generateMatchLabels(v1LabelSelector);
    try {
      items = appsV1Api.listNamespacedReplicaSet(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
   
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_REPLICASET_FAIL, K8sUtils.getMessage(e));
    }
    V1ReplicaSetList v1ReplicaSetListTo = new V1ReplicaSetList();
    if (items != null && !items.isEmpty()) {
      items.forEach(v1ReplicaSet -> {
        List<V1OwnerReference> list = v1ReplicaSet.getMetadata().getOwnerReferences();
        if (list != null && list.size() > 0) {
          if (ownerReferencesName.equals(list.get(0).getName())) {
            v1ReplicaSetListTo.addItemsItem(v1ReplicaSet);
          }
        }
      });
    }
    return v1ReplicaSetListTo;
  }
  
  @Override
  public V1ReplicaSetList listV1ReplicaSetBySelector(V1LabelSelector v1LabelSelector, String namespace, String ownerReferencesName) {
    Map<String, String> selector = v1LabelSelector.getMatchLabels();
    String labelSelector = K8sUtils.generateString(selector);
    V1ReplicaSetList v1ReplicaSetList = null;
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    try {
      if (!StringUtils.isEmpty(namespace)) {
        
        v1ReplicaSetList = appsV1Api.listNamespacedReplicaSet(namespace,
            ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector,
            labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      } else {
        v1ReplicaSetList = appsV1Api.listReplicaSetForAllNamespaces(
            ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector,
            labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_REPLICASET_FAIL, K8sUtils.getMessage(e));
    }
    V1ReplicaSetList v1ReplicaSetListTo = new V1ReplicaSetList();
    if (v1ReplicaSetList != null && v1ReplicaSetList.getItems().size() > 0) {
      
      v1ReplicaSetList.getItems().forEach(v1ReplicaSet -> {
        List<V1OwnerReference> list = v1ReplicaSet.getMetadata().getOwnerReferences();
        if (list != null && list.size() > 0) {
          if (ownerReferencesName.equals(list.get(0).getName())) {
            v1ReplicaSetListTo.addItemsItem(v1ReplicaSet);
          }
        }
        
      });
    }
    return v1ReplicaSetListTo;
  }
  
}
