package com.pubinfo.resource.service.cluster.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.cluster.Namespace;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl extends K8sSearch implements EventsService {
  
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public List<Event> listNamespacedEvent(V1ObjectMeta v1ObjectMeta, String kind) {
    List<Event> events = new ArrayList<>();
    String namespace = v1ObjectMeta.getNamespace();
    String fieldSelector = K8sUtils.generateSelector(v1ObjectMeta, kind);
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1Event> eventList = null;
    try {
      if (org.springframework.util.StringUtils.isEmpty(namespace)) {
        
        eventList = coreV1Api.listEventForAllNamespaces(K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, fieldSelector, K8sParam.ListParam.labelSelector, 20, K8sParam.ListParam.pretty, K8sParam.ListParam.resourceVersion, 20000, K8sParam.ListParam.watch).getItems();
        
      } else {
        eventList = coreV1Api.listNamespacedEvent(namespace, K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, fieldSelector, K8sParam.ListParam.labelSelector, 20, K8sParam.ListParam.resourceVersion, 20000, K8sParam.ListParam.watch).getItems();
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_EVENT_FAIL, K8sUtils.getMessage(e));
    }
    NamespaceServiceImpl.setEvent(eventList, events);
    return events;
  }
  
  @Override
  public Namespace listEventByParam(String name, SearchParamDTO paramVo) {
    
    Namespace vo = new Namespace();
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1EventList v1eventList = null;
    try {
      v1eventList = coreV1Api.listNamespacedEvent(name, K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_EVENT_FAIL, K8sUtils.getMessage(e));
    }
    List<Event> events = new ArrayList<>();
    vo.setV1Events(v1eventList);
    events = pagingOrder(vo.getEvents(), paramVo);
    Integer totalItems = 0;
    Map<String, Integer> listMeta = new HashMap<>();
    if (events != null && !events.isEmpty()) {
      totalItems = events.size();
    }
    
    listMeta.put(K8sParam.Search.totalItems, totalItems);
    vo.setListMeta(listMeta);
    
    return vo;
  }
  
  @Override
  public List<Event> listNamespacedEventByWarning(V1ObjectMeta v1ObjectMeta, String kind) {
    List<Event> list = listNamespacedEvent(v1ObjectMeta, kind);
    list = list.stream().filter(e -> {
      return e.getType().contains(K8sParam.WARNING);
    }).collect(Collectors.toList());
    return list;
  }
  
  @Override
  public Set<Event> generateEventSet(List<V1Pod> v1PodList) {
    Set<Event> eventList = new HashSet<>();
    if (v1PodList != null && !v1PodList.isEmpty()) {
      for (V1Pod v1Pod : v1PodList) {
        List<Event> eventListItem = listNamespacedEventByWarning(v1Pod.getMetadata(), null);
        eventList.addAll(eventListItem);
      }
    }
    return eventList;
  }
  
  @Override
  public Set<Event> generateEventSet(V1PodList v1PodList) {
    return generateEventSet(v1PodList.getItems());
  }
  
  private List<Event> pagingOrder(List<Event> events, SearchParamDTO vo) {
    
    String filterName = vo.getFilterBy();
    String sortBy = vo.getSortBy();
    int limit = vo.getItemsPerPage();
    int skip = (vo.getCurrentPage() <= 0 ? 0 : vo.getCurrentPage() - 1) * limit;
    
    if (!org.springframework.util.StringUtils.isEmpty(filterName)) {
      
      events = events.stream().skip(skip).limit(limit).sorted((s1, s2) -> {
        
        Date data1 = s1.getMeta_creationTimestamp();
        Date data2 = s2.getMeta_creationTimestamp();
        String name1 = s1.getMeta_name();
        String name2 = s2.getMeta_name();
        if (!org.springframework.util.StringUtils.isEmpty(sortBy)) {
          String[] sorts = sortBy.split(",");
          if (K8sParam.Search.createTimeStamp.equals(sorts[1])) {
            if ("d".equals(sorts[0])) {
              return data2.compareTo(data1);
            } else {
              return data1.compareTo(data2);
            }
            
          } else {
            if ("d".equals(sorts[0])) {
              return name2.compareTo(name1);
            } else {
              return name1.compareTo(name2);
            }
          }
        } else {
          return data2.compareTo(data1);
        }
      }).collect(Collectors.toList());
    }
    
    return events;
  }
  
}
