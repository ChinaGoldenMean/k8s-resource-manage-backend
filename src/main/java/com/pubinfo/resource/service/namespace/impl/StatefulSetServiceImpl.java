package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.StatefulSet;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.StatefulSetService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class StatefulSetServiceImpl extends K8sSearch implements StatefulSetService {
  @Autowired
  K8sApiService k8sService;
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  
  @Override
  public Page<List<StatefulSet>> listStatefulSet(SearchParamDTO paramVo) {
    AppsV1Api appsV1beta1Api = k8sService.getAppsV1Api();
    List<V1StatefulSet> items;
    List<StatefulSet> statefulSets = new ArrayList<>();
    String namespace = k8sService.getNamespace();
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = appsV1beta1Api.listStatefulSetForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      } else {
        items = appsV1beta1Api.listNamespacedStatefulSet(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      }
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_STATEFULSET_FAIL, K8sUtils.getMessage(e));
    }
    List<V1StatefulSet> replicaSetList = pagingOrder(paramVo, items, V1StatefulSet::getMetadata, namespace);
    
    if (replicaSetList != null && !replicaSetList.isEmpty()) {
      for (V1StatefulSet v1beta1StatefulSet : replicaSetList) {
        
        statefulSets.add(toStatefulSet(v1beta1StatefulSet));
      }
    }
    
    return new Page<>(paramVo, statefulSets, getTotalItem(), statefulSets.size());
  }
  
  @Override
  public StatefulSet toStatefulSet(V1StatefulSet v1beta1StatefulSet) {
    V1PodList v1PodList = podService.listV1Pod(v1beta1StatefulSet.getSpec().getSelector(), v1beta1StatefulSet.getMetadata().getNamespace(), v1beta1StatefulSet.getMetadata().getName());
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    return new StatefulSet().initStatefulSet(v1beta1StatefulSet, v1PodList, eventList);
  }
  
  @Override
  public Builder readBuilder(String nameSpace, String name) {
    AppsV1Api appsV1beta1Api = k8sService.getAppsV1Api();
    V1StatefulSet v1StatefulSet = null;
    Builder builder = null;
    
    try {
      v1StatefulSet = appsV1beta1Api.readNamespacedStatefulSet(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_STATEFULSET_FAIL, K8sUtils.getMessage(e));
    }
    builder = toStatefulSet(v1StatefulSet);
    
    return builder;
  }
  
  @Override
  public StatefulSet readStatefulSet(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    AppsV1Api appsV1beta1Api = k8sService.getAppsV1Api();
    V1StatefulSet v1StatefulSet;
    try {
      v1StatefulSet = appsV1beta1Api.readNamespacedStatefulSet(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_STATEFULSET_FAIL, K8sUtils.getMessage(e));
    }
    V1PodList v1PodList = null;
    List<Event> podEventList = null;
    StatefulSet statefulSet = new StatefulSet();
    if (v1StatefulSet != null) {
      v1PodList = podService.listV1Pod(v1StatefulSet.getSpec().getSelector(), v1StatefulSet.getMetadata().getNamespace(), v1StatefulSet.getMetadata().getName());
      podEventList = eventsService.listNamespacedEventByWarning(v1StatefulSet.getMetadata(), null);
      Set<Event> eventList = eventsService.generateEventSet(v1PodList);
      
      statefulSet = new StatefulSet(v1StatefulSet, v1PodList, eventList, podEventList);
    }
    
    return statefulSet;
  }
  
}
