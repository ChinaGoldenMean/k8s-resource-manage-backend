package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.namespace.Secret;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.SecretService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SecretServiceImpl extends K8sSearch implements SecretService {
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<List<Secret>> listSecret(SearchParamDTO paramVo) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    List<V1Secret> items = null;
    String namespace = k8sService.getNamespace();
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        items = coreV1Api.listSecretForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      } else {
        items = coreV1Api.listNamespacedSecret(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
        
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_SECRET_FAIL, K8sUtils.getMessage(e));
    }
    
    List<V1Secret> v1Secrets = pagingOrder(paramVo, items, V1Secret::getMetadata, namespace);
    List<Secret> secrets = new ArrayList<>();
    if (v1Secrets != null && !v1Secrets.isEmpty()) {
      v1Secrets.stream().forEach(v1Secret -> {
        Secret secret = new Secret().initSecret(v1Secret);
        secrets.add(secret);
      });
    }
    Page<List<Secret>> page = new Page<>(paramVo, secrets, getTotalItem(), secrets.size());
    return page;
  }
  
  @Override
  public Secret readSecret(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    Secret secret;
    try {
      V1Secret v1Secret = coreV1Api.readNamespacedSecret(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
      secret = new Secret(v1Secret);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_SECRET_FAIL, K8sUtils.getMessage(e));
    }
    return secret;
  }
}
