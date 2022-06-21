package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.DaemonSet;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.DaemonSetService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1DaemonSet;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
public class DaemonSetServiceImpl extends K8sSearch implements DaemonSetService {
  @Autowired
  K8sApiService k8sService;
  
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  @Autowired
  ServiceOfService serviceOfService;
  
  @Override
  public Page<List<DaemonSet>> listDaemonSet(SearchParamDTO paramVo) {
    
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    List<V1DaemonSet> items = null;
    String nameSpace = k8sService.getNamespace();
    try {
      if (nameSpace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = appsV1Api.listDaemonSetForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
       items = filterByNamespaces(items, V1DaemonSet::getMetadata, nameSpace);
      } else {
        items = appsV1Api.listNamespacedDaemonSet(nameSpace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      }
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_DAEMON_SET_FAIL, K8sUtils.getMessage(e));
    }
    List<V1DaemonSet> list = pagingOrder(paramVo, items, V1DaemonSet::getMetadata, nameSpace);
    List<DaemonSet> daemonSets = new ArrayList<>();
    if (list != null && list.size() > 0) {
      list.stream().forEach(v1DaemonSet -> {
        
        daemonSets.add(toDaemonSet(v1DaemonSet));
      });
    }
    Page<List<DaemonSet>> page = new Page<>(paramVo, daemonSets, getTotalItem(), daemonSets.size());
    return page;
  }
  
  @Override
  public DaemonSet toDaemonSet(V1DaemonSet v1DaemonSet) {
    V1PodList v1PodList = podService.listV1Pod(v1DaemonSet.getSpec().getSelector(), v1DaemonSet.getMetadata().getNamespace(), v1DaemonSet.getMetadata().getName());
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    
    return new DaemonSet().initDaemonSet(v1DaemonSet, v1PodList, eventList);
  }
  
  @Override
  public Builder readBuilder(String nameSpace, String name) {
    
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    Builder builder = null;
    V1DaemonSet v1DaemonSet = null;
    try {
      v1DaemonSet = appsV1Api.readNamespacedDaemonSet(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.QUERY_DAEMON_SET_FAIL, K8sUtils.getMessage(e));
    }
    builder = toDaemonSet(v1DaemonSet);
    return builder;
  }
  
  @Override
  public DaemonSet readDaemonSet(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    DaemonSet daemonSet = null;
    
    V1DaemonSet v1DaemonSet = null;
    try {
      v1DaemonSet = appsV1Api.readNamespacedDaemonSet(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_DAEMON_SET_FAIL, K8sUtils.getMessage(e));
    }
    V1PodList v1PodList = podService.listV1Pod(v1DaemonSet.getSpec().getSelector(), v1DaemonSet.getMetadata().getNamespace(), v1DaemonSet.getMetadata().getName());
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    List<Event> podEventList = eventsService.listNamespacedEventByWarning(v1DaemonSet.getMetadata(), null);
    List<V1Service> v1ServiceList = serviceOfService.listServiceByLabel(v1DaemonSet.getMetadata());
    daemonSet = new DaemonSet(v1DaemonSet, v1PodList, podEventList, eventList, v1ServiceList);
    
    return daemonSet;
    
  }
}
