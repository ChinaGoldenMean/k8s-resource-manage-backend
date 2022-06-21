package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.Replication;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ReplicationControllerService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1ReplicationController;
import io.kubernetes.client.openapi.models.V1Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
public class ReplicationControllerServiceImpl extends K8sSearch implements ReplicationControllerService {
  
  @Autowired
  K8sApiService k8sService;
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  
  @Autowired
  ServiceOfService serviceOfService;
  
  @Override
  public Page<List<Replication>> listReplicationController(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1ReplicationController> items = null;
    List<Replication> replicationControllerList = new ArrayList<>();
    String namespace = k8sService.getNamespace();
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = coreV1Api.listReplicationControllerForAllNamespaces(
            K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.pretty, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      } else {
        items = coreV1Api.listNamespacedReplicationController(namespace, K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_REPLICATION_CONTROLLER_ERROR, K8sUtils.getMessage(e));
    }
    List<V1ReplicationController> replicaSetList = pagingOrder(paramVo, items, V1ReplicationController::getMetadata, namespace);
    
    if (replicaSetList != null && !replicaSetList.isEmpty()) {
      for (V1ReplicationController v1ReplicationController : replicaSetList) {
        
        replicationControllerList.add(toReplication(v1ReplicationController));
      }
    }
    
    Page<List<Replication>> page = new Page<>(paramVo, replicationControllerList, getTotalItem(), replicationControllerList.size());
    return page;
  }
  
  @Override
  public Replication toReplication(V1ReplicationController v1ReplicationController) {
    V1PodList v1PodList = podService.listV1Pod(v1ReplicationController.getMetadata(), true, "ReplicaSet");
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    return new Replication().initReplication(v1ReplicationController, v1PodList, eventList);
  }
  
  @Override
  public Builder readBuilder(String nameSpace, String name) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1ReplicationController v1ReplicationController = null;
    Builder builder = null;
    
    try {
      v1ReplicationController = coreV1Api.readNamespacedReplicationController(name, nameSpace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_REPLICATION_CONTROLLER_ERROR, K8sUtils.getMessage(e));
    }
    
    V1PodList v1PodList = podService.listV1Pod(v1ReplicationController.getSpec().getSelector(), v1ReplicationController.getMetadata().getNamespace());
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    builder = new Replication().initReplication(v1ReplicationController, v1PodList, eventList);
    
    return builder;
  }
  
  @Override
  public Replication readReplication(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1ReplicationController v1ReplicationController = null;
    
    try {
      v1ReplicationController = coreV1Api.readNamespacedReplicationController(name, nameSpace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_REPLICATION_CONTROLLER_ERROR, K8sUtils.getMessage(e));
    }
    V1PodList v1PodList = podService.listV1Pod(v1ReplicationController.getSpec().getSelector(), v1ReplicationController.getMetadata().getNamespace());
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    List<Event> podEventList = eventsService.listNamespacedEventByWarning(v1ReplicationController.getMetadata(), null);
    List<V1Service> v1ServiceList = serviceOfService.listServiceByLabel(v1ReplicationController.getMetadata());
    Replication replication = new Replication(v1ReplicationController, v1PodList, eventList, podEventList, v1ServiceList);
    return replication;
  }
  
}
