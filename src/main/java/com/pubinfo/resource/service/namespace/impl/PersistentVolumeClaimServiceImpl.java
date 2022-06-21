package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.namespace.PersistentVolumeClaim;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.PersistentVolumeClaimService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PersistentVolumeClaimServiceImpl extends K8sSearch implements PersistentVolumeClaimService {
  
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<List<PersistentVolumeClaim>> listPersistentVolumeClaim(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1PersistentVolumeClaim> items = null;
    String namespace = k8sService.getNamespace();
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = coreV1Api.listPersistentVolumeClaimForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
        
      } else {
        items = coreV1Api.listNamespacedPersistentVolumeClaim(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
        
      }
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_PERSISTENT_VOLUME_CLAIM_FAIL, K8sUtils.getMessage(e));
    }
    List<V1PersistentVolumeClaim> list = pagingOrder(paramVo, items, V1PersistentVolumeClaim::getMetadata, namespace);
    
    List<PersistentVolumeClaim> persistentVolumeClaims = new ArrayList<>();
    if (list != null && !list.isEmpty()) {
      list.stream().forEach(v1PersistentVolumeClaim -> {
        
        PersistentVolumeClaim volumeClaim = new PersistentVolumeClaim(v1PersistentVolumeClaim);
        persistentVolumeClaims.add(volumeClaim);
      });
      
    }
    Page<List<PersistentVolumeClaim>> page = new Page<>(paramVo, persistentVolumeClaims, getTotalItem(), persistentVolumeClaims.size());
    return page;
  }
  
  @Override
  public PersistentVolumeClaim readPersistentVolumeClaim(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1PersistentVolumeClaim v1PersistentVolumeClaim = null;
    
    try {
      v1PersistentVolumeClaim = coreV1Api.readNamespacedPersistentVolumeClaim(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_PERSISTENT_VOLUME_CLAIM_FAIL, K8sUtils.getMessage(e));
    }
    
    PersistentVolumeClaim volumeClaim = new PersistentVolumeClaim(v1PersistentVolumeClaim);
    
    return volumeClaim;
  }
}
