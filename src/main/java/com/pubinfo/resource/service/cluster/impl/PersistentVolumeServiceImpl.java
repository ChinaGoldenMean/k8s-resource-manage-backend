package com.pubinfo.resource.service.cluster.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.cluster.PersistentVolume;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.PersistentVolumeService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.model.constant.K8sParam;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersistentVolumeServiceImpl extends K8sSearch implements PersistentVolumeService {
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<List<PersistentVolume>> listPersistentVolume(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1PersistentVolume> items = null;
    try {
      items = coreV1Api.listPersistentVolume(K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_PERSISTENT_VOLUME_FAIL, K8sUtils.getMessage(e));
    }
    List<V1PersistentVolume> v1PvList = pagingOrder(paramVo, items, V1PersistentVolume::getMetadata, null);
    List<PersistentVolume> persistentVolumeList = new ArrayList<>();
    if (v1PvList != null && !v1PvList.isEmpty()) {
      v1PvList.stream().forEach(v1PersistentVolume -> {
        PersistentVolume pv = new PersistentVolume(v1PersistentVolume);
        persistentVolumeList.add(pv);
        
      });
    }
    Page<List<PersistentVolume>> listPage = new Page<>(paramVo, persistentVolumeList, getTotalItem(), persistentVolumeList.size());
    return listPage;
  }
  
  @Override
  public PersistentVolume readPersistentVolume(String persistentVolumeName) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    V1PersistentVolume v1pv = null;
    try {
      v1pv = coreV1Api.readPersistentVolume(persistentVolumeName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_PERSISTENT_VOLUME_FAIL, K8sUtils.getMessage(e));
    }
    PersistentVolume pv = new PersistentVolume(v1pv);
    
    return pv;
  }
  
}
