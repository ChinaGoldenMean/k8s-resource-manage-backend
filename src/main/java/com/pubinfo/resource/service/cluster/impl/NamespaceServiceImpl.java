package com.pubinfo.resource.service.cluster.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.cluster.Namespace;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.constant.K8sParam.Search;
import com.pubinfo.resource.model.dto.ProjectDTO;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.cluster.NamespaceService;
import com.pubinfo.resource.service.common.K8sApiService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Event;
import io.kubernetes.client.openapi.models.V1Namespace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class NamespaceServiceImpl extends K8sSearch implements NamespaceService {
  
  @Autowired
  K8sApiService k8sService;
  @Autowired
  EventsService eventsService;
  
  public static void setEvent(List<V1Event> eventList, List<Event> events) {
    
    if (!eventList.isEmpty()) {
      for (V1Event v1Event : eventList) {
        Event event = new Event(v1Event);
        
        events.add(event);
      }
    }
    
  }
  
  @Override
  public Page<List<Namespace>> getNamespaceList(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1Namespace> items = null;
    
//    String nameSpace = k8sService.getNamespace();
    try {
      items = coreV1Api.listNamespace(ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      log.debug("查询后总数: {} ", items.size());
      if (!k8sService.isK8sManage()) {
        log.debug("当前不是管理员: {} ");
        items = filterNamespace(items);
      }
      log.debug("过滤后总数: {} ", items.size());
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_NAMESPACE_FAIL, K8sUtils.getMessage(e));
    }
    List<V1Namespace> list = pagingOrder(paramVo, items, V1Namespace::getMetadata, null);
    List<Namespace> namespaceVos = new ArrayList<>();
    Integer itemsPerPage = 0;
    if (list != null && list.size() > 0) {
      list.stream().forEach(v1Namespace -> {
        Namespace vo = new Namespace(v1Namespace);
        namespaceVos.add(vo);
      });
      itemsPerPage = list.size();
    }
    
    Page<List<Namespace>> page = new Page<>(paramVo, namespaceVos, getTotalItem(), itemsPerPage);
    return page;
  }
  
  private List<V1Namespace> filterNamespace(List<V1Namespace> list) {
    List<V1Namespace> v1NamespaceList = new ArrayList<>();
    if (list != null && list.size() > 0) {
      List<ProjectDTO> projectList = k8sService.getProjects();
      if (projectList != null && projectList.size() > 0) {
        for (V1Namespace v1Namespace : list) {
          for (ProjectDTO projectDTO : projectList) {
            if (projectDTO.getCode().equals(v1Namespace.getMetadata().getName())) {
              v1NamespaceList.add(v1Namespace);
              
            }
          }
        }
      }
    }
    
    return v1NamespaceList;
    
  }
  
  @Override
  public Namespace nameSpacesByName(String name) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    
    V1Namespace namespace = null;
    try {
      namespace = coreV1Api.readNamespace(name, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_NAMESPACE_FAIL, K8sUtils.getMessage(e));
    }
    
    Namespace vo = new Namespace(namespace);
    
    SearchParamDTO paramVo = new SearchParamDTO();
    List<Event> events = eventsService.listEventByParam(name, paramVo).getEvents();
    vo.setEvents(events);
    Map<String, Integer> listMeta = new HashMap<>();
    listMeta.put(Search.totalItems, events.size());
    vo.setListMeta(listMeta);
    return vo;
  }
}
