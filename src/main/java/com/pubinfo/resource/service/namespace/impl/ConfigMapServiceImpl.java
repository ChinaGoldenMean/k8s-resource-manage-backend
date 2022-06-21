package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.namespace.ConfigMap;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.ConfigMapService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConfigMapServiceImpl extends K8sSearch implements ConfigMapService {
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<List<ConfigMap>> listConfigMap(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    String nameSpace = k8sService.getNamespace();
    List<V1ConfigMap> items = null;
    try {
      if (nameSpace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = coreV1Api.listConfigMapForAllNamespaces(K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.pretty, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
        //items.stream().filter();
      } else {
        items = coreV1Api.listNamespacedConfigMap(nameSpace, K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      }
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CONFIG_MAP_FAIL, K8sUtils.getMessage(e));
    }
    K8sSearch search = new K8sSearch();
    List<V1ConfigMap> list = pagingOrder(paramVo, items, V1ConfigMap::getMetadata, nameSpace);
    List<ConfigMap> configMaps = new ArrayList<>();
    if (list != null && list.size() > 0) {
      list.stream().forEach(v1ConfigMap -> {
        ConfigMap configMap = new ConfigMap().initConfigMap(v1ConfigMap);
        configMaps.add(configMap);
      });
    }
    
    Page<List<ConfigMap>> page = new Page<>(paramVo, configMaps, getTotalItem(), configMaps.size());
    return page;
  }
  
  @Override
  public ConfigMap readConfigMap(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    ConfigMap configMap = null;
    try {
      V1ConfigMap v1ConfigMap = coreV1Api.readNamespacedConfigMap(name, nameSpace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
      configMap = new ConfigMap(v1ConfigMap);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CONFIG_MAP_FAIL, K8sUtils.getMessage(e));
    }
    return configMap;
  }
}
