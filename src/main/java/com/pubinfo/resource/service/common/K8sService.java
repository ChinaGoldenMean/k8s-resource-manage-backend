package com.pubinfo.resource.service.common;

import com.alibaba.fastjson.JSONObject;
import com.pubinfo.resource.model.dto.K8sYamlDTO;
import com.pubinfo.resource.model.dto.ScaleYamlDTO;
import com.pubinfo.resource.model.dto.YamlBaseDTO;

public interface K8sService {
  
  Integer scaleModuleSize(ScaleYamlDTO scaleYamlVo);
  
  boolean createResource(K8sYamlDTO k8SYamlDTO);
  
  boolean createResourceCrd(K8sYamlDTO k8SYamlDTO);
  String executeHttpGetBack(String url);
  
  void deleteNamespacedResource(YamlBaseDTO yamlBaseDTO);
  
  JSONObject getResourceByNameUseOKHttp(YamlBaseDTO readYamlVo);
  
  void updateResource(K8sYamlDTO yamlVo);
  String crdResourcePut(K8sYamlDTO yamlVo);
  String crdResourceDelete(String url);
}
