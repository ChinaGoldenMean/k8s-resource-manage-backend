package com.pubinfo.resource.service.cluster.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.cluster.StorageClass;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.StorageClassService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.model.constant.K8sParam;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.StorageV1Api;
import io.kubernetes.client.openapi.models.V1StorageClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StorageClassServiceImpl extends K8sSearch implements StorageClassService {
  
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<List<StorageClass>> listStorageClass(SearchParamDTO paramVo) {
    List<V1StorageClass> items = null;
    StorageV1Api storageV1Api = k8sService.getStorageV1Api();
    
    try {
      items = storageV1Api.listStorageClass(K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_STORAGECLASS_FAIL, K8sUtils.getMessage(e));
    }
    
    List<V1StorageClass> v1ScList = pagingOrder(paramVo, items, V1StorageClass::getMetadata, null);
    List<StorageClass> scList = new ArrayList<>();
    if (v1ScList != null && !v1ScList.isEmpty()) {
      v1ScList.stream().forEach(v1pv -> {
        StorageClass pv = new StorageClass(v1pv);
        scList.add(pv);
      });
    }
    
    Page<List<StorageClass>> page = new Page<>(paramVo, scList, getTotalItem(), scList.size());
    
    return page;
  }
  
  @Override
  public StorageClass readStorageClass(String storageClassName) {
    StorageV1Api storageV1Api = k8sService.getStorageV1Api();
    V1StorageClass v1pv = null;
    try {
      v1pv = storageV1Api.readStorageClass(storageClassName, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_STORAGECLASS_FAIL, K8sUtils.getMessage(e));
    }
    StorageClass pv = new StorageClass(v1pv);
    return pv;
  }
  
}
