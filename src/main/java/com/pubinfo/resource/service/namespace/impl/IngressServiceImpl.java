package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.namespace.Ingress;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.IngressService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class IngressServiceImpl extends K8sSearch implements IngressService {
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<List<Ingress>> listIngress(SearchParamDTO paramVo) {
    ExtensionsV1beta1Api v1beta1Api = k8sService.getExtensionsV1beta1Api();
    List<ExtensionsV1beta1Ingress> items = null;
    String nameSpace = k8sService.getNamespace();
    try {
      if (nameSpace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = v1beta1Api.listIngressForAllNamespaces(K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.pretty, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      } else {
        items = v1beta1Api.listNamespacedIngress(nameSpace, K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      }
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.GET_INGRESS_FAIL, K8sUtils.getMessage(e));
    }
    K8sSearch search = new K8sSearch();
    List<ExtensionsV1beta1Ingress> list = pagingOrder(paramVo, items, ExtensionsV1beta1Ingress::getMetadata, nameSpace);
    List<Ingress> ingresses = new ArrayList<>();
    if (list != null && list.size() > 0) {
      list.stream().forEach(beta1Ingress -> {
        
        Ingress daemonSet = new Ingress().initIngress(beta1Ingress);
        ingresses.add(daemonSet);
      });
    }
    Page<List<Ingress>> page = new Page<>(paramVo, ingresses, getTotalItem(), ingresses.size());
    return page;
  }
  
  @Override
  public Ingress readIngress(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    ExtensionsV1beta1Api v1beta1Api = k8sService.getExtensionsV1beta1Api();
    Ingress ingress = null;
    
    ExtensionsV1beta1Ingress beta1Ingress = null;
    try {
      beta1Ingress = v1beta1Api.readNamespacedIngress(name, nameSpace, K8sParam.ReadParam.pretty, K8sParam.ReadParam.exact, K8sParam.ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.GET_INGRESS_FAIL, K8sUtils.getMessage(e));
    }
    ingress = new Ingress(beta1Ingress);
    
    return ingress;
  }
}
