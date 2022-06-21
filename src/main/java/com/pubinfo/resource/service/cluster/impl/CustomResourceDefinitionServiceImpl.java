package com.pubinfo.resource.service.cluster.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.cluster.CustomResourceDefinition;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.CustomResourceDefinitionService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.common.K8sService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.ApiextensionsV1beta1Api;
import io.kubernetes.client.openapi.models.V1beta1CustomResourceDefinition;
import io.kubernetes.client.openapi.models.V1beta1CustomResourceDefinitionSpec;
import io.kubernetes.client.openapi.models.V1beta1CustomResourceDefinitionVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class CustomResourceDefinitionServiceImpl extends K8sSearch implements CustomResourceDefinitionService {
  @Autowired
  K8sApiService k8sApiService;
  
  @Autowired
  K8sService k8sService;
  
  @Override
  public Page<List<CustomResourceDefinition>> listCustomResourceDefinition(SearchParamDTO paramVo) {
    ApiextensionsV1beta1Api v1beta1Api = k8sApiService.getApiextensionsV1beta1Api();
    List<V1beta1CustomResourceDefinition> items = null;
    
    try {
      items = v1beta1Api.listCustomResourceDefinition(
          ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CRD_FAIL, K8sUtils.getMessage(e));
    }
    List<V1beta1CustomResourceDefinition> definitionList = pagingOrder(paramVo, items, V1beta1CustomResourceDefinition::getMetadata, null);
    if (definitionList == null) {
      return null;
    }
    
    List<CustomResourceDefinition> customResourceDefinitionList = new ArrayList<>();
    if (definitionList != null && definitionList.size() > 0) {
      
      definitionList.stream().forEach(v1customResourceDefinition -> {
        CustomResourceDefinition customResourceDefinition = new CustomResourceDefinition(v1customResourceDefinition);
        customResourceDefinitionList.add(customResourceDefinition);
      });
      
    }
    Page<List<CustomResourceDefinition>> page = new Page<>(paramVo, customResourceDefinitionList, getTotalItem(), customResourceDefinitionList.size());
    return page;
  }
  
  @Override
  public CustomResourceDefinition readCustomResourceDefinition(String name) {
    ApiextensionsV1beta1Api v1beta1Api = k8sApiService.getApiextensionsV1beta1Api();
    CustomResourceDefinition customResourceDefinition = null;
    try {
      V1beta1CustomResourceDefinition v1beta1CustomResourceDefinition = v1beta1Api.readCustomResourceDefinition(name, ReadParam.pretty, ReadParam.exact, ReadParam.export);
      customResourceDefinition = new CustomResourceDefinition(v1beta1CustomResourceDefinition).initCustomResourceColumnDefinition(v1beta1CustomResourceDefinition);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CRD_FAIL, K8sUtils.getMessage(e));
    }
    return customResourceDefinition;
  }
  
  private JSONObject filterByNamespaces(JSONObject jsonObject, String nameSpaces) {
    
    if (!k8sApiService.isK8sManage()) {
      if (!StringUtils.isEmpty(nameSpaces) && nameSpaces.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        String[] namespacesArray = nameSpaces.split(",");
        
        List<String> namespacesList = new ArrayList<String>(Arrays.asList(namespacesArray));
        namespacesList.remove(0);
        
        if (jsonObject != null) {
          JSONArray itemArray = jsonObject.getJSONArray("items");
          JSONArray filterArray = new JSONArray();
          if (itemArray != null && itemArray.size() > 0) {
            for (int i = 0; i < itemArray.size(); i++) {
              JSONObject subitem = itemArray.getJSONObject(i);
              if (subitem != null) {
                JSONObject metadata = subitem.getJSONObject("metadata");
                String namespace = metadata.getString("namespace");
                if (namespacesList.contains(namespace)) {
                  filterArray.add(subitem);
                }
                
              }
            }
            
          }
          jsonObject.put("items",filterArray);
          
        }
        
      }
    }
    
    return jsonObject;
  }
  
  @Override
  public JSONObject listCustomResourceDefinitionSubitem(String crdName) {
    ApiextensionsV1beta1Api v1beta1Api = k8sApiService.getApiextensionsV1beta1Api();
    V1beta1CustomResourceDefinition v1beta1CustomResourceDefinition = null;
    try {
      v1beta1CustomResourceDefinition = v1beta1Api.readCustomResourceDefinition(crdName, ReadParam.pretty, ReadParam.exact, ReadParam.export);
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CRD_FAIL, K8sUtils.getMessage(e));
    }
    String result = "";
    if (v1beta1CustomResourceDefinition != null) {
      V1beta1CustomResourceDefinitionSpec spec = v1beta1CustomResourceDefinition.getSpec();
      String group = spec.getGroup();
      String plural = spec.getNames().getPlural();
      List<V1beta1CustomResourceDefinitionVersion> versions = spec.getVersions();
      String name = "";
      if (versions != null && versions.size() > 0) {
        name = versions.get(0).getName();
      }
      StringBuilder localVarPath = new StringBuilder(
          "/apis/");
      if (!StringUtils.isEmpty(group)) {
        localVarPath.append(group + "/");
      }
      if (!StringUtils.isEmpty(name)) {
        localVarPath.append(name + "/");
      }
      if (!StringUtils.isEmpty(plural)) {
        localVarPath.append(plural);
      }
      result = k8sService.executeHttpGetBack(localVarPath.toString());
    }
    String namespaces = k8sApiService.getUserNameSpaceStr();
    return filterByNamespaces(JSON.parseObject(result),namespaces);
    
  }
  
}
