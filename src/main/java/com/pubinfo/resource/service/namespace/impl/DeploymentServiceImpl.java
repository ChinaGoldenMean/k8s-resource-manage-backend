package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Deployment;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.ReplicaSet;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.DeploymentVo;
import com.pubinfo.resource.model.vo.ReplicaSetVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.DeploymentService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ReplicaSetService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Slf4j
@org.springframework.stereotype.Service
public class DeploymentServiceImpl extends K8sSearch implements DeploymentService {
  @Autowired
  ReplicaSetService replicaSetService;
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  @Autowired
  ServiceOfService serviceOfService;
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<DeploymentVo> listDeployment(SearchParamDTO paramVo) {
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    String namespace = k8sService.getNamespace();
    List<V1Deployment> items;
    try {
      
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = appsV1Api.listDeploymentForAllNamespaces(K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.pretty, K8sParam.ListParam.resourceVersion, 3000, K8sParam.ListParam.watch).getItems();
        log.info("命名空间:"+namespace);
        log.info("过滤前总条数:"+items.size());
      items = filterByNamespaces(items, V1Deployment::getMetadata, namespace);
  
        log.info("过滤后总条数:"+items.size());
      } else {
        items = appsV1Api.listNamespacedDeployment(namespace, K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, 3000, K8sParam.ListParam.watch).getItems();
      }
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_DEPLOYMENT_FAIL, K8sUtils.getMessage(e));
    }
    //上面已经过滤,所以设置为null
    List<V1Deployment> list = pagingOrder(paramVo, items, V1Deployment::getMetadata, namespace);
  
    log.info("分页后总条数:"+items.size());
    List<Deployment> deploymentList = new ArrayList<>();
    if (list != null && list.size() > 0) {
      list.stream().forEach(v1Deployment -> {
        deploymentList.add(toDeployment(v1Deployment, false));
      });
    }
    DeploymentVo vo = new DeploymentVo(deploymentList, items);
    Page<DeploymentVo> page = new Page<>(paramVo, vo, getTotalItem(), deploymentList.size());
    return page;
  }
  
  @Override
  public Deployment toDeployment(V1Deployment v1Deployment, boolean isSearchEvent) {
    V1PodList v1PodList = podService.listV1Pod(v1Deployment.getSpec().getSelector(), v1Deployment.getMetadata().getNamespace());
    String name = v1Deployment.getMetadata().getName();
    String namespaceD = v1Deployment.getMetadata().getNamespace();
    V1ReplicaSetList v1ReplicaSetList = replicaSetService.listV1ReplicaSetBySelector(v1Deployment.getSpec().getSelector(), namespaceD, name);
    v1PodList = podService.filterOwnerReferences(v1PodList, v1ReplicaSetList);
    Set<Event> eventList = null;
    if (isSearchEvent) {
      eventList = eventsService.generateEventSet(v1PodList);
    }
    return new Deployment().initDeployment(v1Deployment, v1PodList, eventList);
  }
  
  @Override
  public Builder readBuilder(String nameSpace, String name) {
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    Builder builder = null;
    
    V1Deployment v1Deployment = null;
    try {
      v1Deployment = appsV1Api.readNamespacedDeployment(name, nameSpace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.QUERY_DEPLOYMENT_FAIL, K8sUtils.getMessage(e));
    }
    builder = toDeployment(v1Deployment, true);
    return builder;
  }
  
  @Override
  public Deployment readDeployment(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    AppsV1Api appsV1Api = k8sService.getAppsV1Api();
    Deployment deployment = null;
    V1Deployment v1Deployment = null;
    try {
      v1Deployment = appsV1Api.readNamespacedDeployment(name, nameSpace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_DEPLOYMENT_FAIL, K8sUtils.getMessage(e));
    }
    V1ReplicaSetList v1ReplicaSetList1 = replicaSetService.listByLabel2(v1Deployment.getMetadata(), v1Deployment.getSpec().getSelector(),v1Deployment.getMetadata().getName());
    V1PodList v1PodList = new V1PodList();
    List<V1Pod> v1Pods = new ArrayList<>();
    
    List<Event> podEventList = eventsService.listNamespacedEventByWarning(v1Deployment.getMetadata(), null);
    List<ReplicaSet> replicaSets = new ArrayList<>();
    if (v1ReplicaSetList1 != null && !v1ReplicaSetList1.getItems().isEmpty()) {
      for (V1ReplicaSet v1beta1ReplicaSet : v1ReplicaSetList1.getItems()) {
        V1PodList v1PodListByRs = podService.listV1Pod(v1beta1ReplicaSet.getSpec().getSelector(), v1beta1ReplicaSet.getMetadata().getNamespace(), v1beta1ReplicaSet.getMetadata().getName());
        ReplicaSet replicaSet = new ReplicaSet().initReplicaSet(v1beta1ReplicaSet, v1PodListByRs, null);
        replicaSets.add(replicaSet);
        v1Pods.addAll(v1PodListByRs.getItems());
      }
    }
    ReplicaSetVo replicaSetVo = new ReplicaSetVo();
    replicaSetVo.setReplicaSets(replicaSets);
    v1PodList.setItems(v1Pods);
    // Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    deployment = new Deployment(v1Deployment, v1PodList, podEventList, null, replicaSetVo);
    
    return deployment;
  }
  
}
