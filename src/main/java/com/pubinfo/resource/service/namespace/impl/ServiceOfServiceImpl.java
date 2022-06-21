package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.Service;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.EndpointsVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.EndpointService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceOfServiceImpl extends K8sSearch implements ServiceOfService {
  
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  @Autowired
  ServiceOfService serviceOfService;
  @Autowired
  K8sApiService k8sService;
  @Autowired
  EndpointService v1EndpointService;
  
  @Override
  public Page<List<Service>> listService(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    String namespace = k8sService.getNamespace();
    List<V1Service> items = null;
    try {
      if ( namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        items = coreV1Api.listServiceForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      } else {
        items = coreV1Api.listNamespacedService(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_POD_FAIL, K8sUtils.getMessage(e));
    }
    
    List<V1Service> v1Services = pagingOrder(paramVo, items, V1Service::getMetadata, namespace);
    List<Service> services = new ArrayList<>();
    
    if (v1Services != null && !v1Services.isEmpty()) {
      v1Services.stream().forEach(v1service -> {
        EndpointsVo endpointsVo = v1EndpointService.readEndpoint(v1service.getMetadata().getNamespace(), v1service.getMetadata().getName());
        Service service = new Service().initService(v1service, endpointsVo);
        services.add(service);
      });
    }
    Page<List<Service>> page = new Page<>(paramVo, services, getTotalItem(), services.size());
    return page;
    
  }
  
  @Override
  public List<V1Service> listServiceByLabel(V1ObjectMeta v1ObjectMeta) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1ServiceList V1ServiceList = null;
    try {
      
      String namespace = v1ObjectMeta.getNamespace();
      String labelSelector = null;
      labelSelector = K8sUtils.generateLabel(v1ObjectMeta);
      if (!StringUtils.isEmpty(namespace)) {
        V1ServiceList = coreV1Api.listNamespacedService(namespace,
            ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      } else {
        V1ServiceList = coreV1Api.listServiceForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_SERVICE_FAIL, K8sUtils.getMessage(e));
    }
    
    return V1ServiceList.getItems();
  }
  
  @Override
  public List<V1Service> listServiceByLabel(String nameSpace,V1LabelSelector v1LabelSelector) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1ServiceList V1ServiceList = null;
    try {
     
      String labelSelector = null;
      labelSelector = K8sUtils.generateMatchLabels(v1LabelSelector);
      if (!StringUtils.isEmpty(nameSpace)) {
        V1ServiceList = coreV1Api.listNamespacedService(nameSpace,
            ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
      
      } else {
        V1ServiceList = coreV1Api.listServiceForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch);
      
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_SERVICE_FAIL, K8sUtils.getMessage(e));
    }
  
    return V1ServiceList.getItems();
  }
  
  @Override
  public Service readService(String nameSpace, String name) {
    if(!k8sService.isUserNameSapce(nameSpace)){
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1Service> items = null;
    Service service;
    try {
      V1Service v1Service = coreV1Api.readNamespacedService(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
      
      V1PodList v1PodList = podService.listV1PodByLabelSelector(v1Service.getSpec().getSelector(), nameSpace);
      List<Event> podEventList = eventsService.listNamespacedEventByWarning(v1Service.getMetadata(), null);
      EndpointsVo endpointsVo = v1EndpointService.readEndpoint(nameSpace, name);
      service = new Service(v1Service, podEventList, v1PodList, endpointsVo);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_SERVICE_FAIL, K8sUtils.getMessage(e));
    }
    return service;
  }
}
