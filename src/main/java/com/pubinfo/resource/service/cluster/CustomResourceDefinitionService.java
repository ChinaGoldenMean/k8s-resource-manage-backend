package com.pubinfo.resource.service.cluster;

import com.alibaba.fastjson.JSONObject;
import com.pubinfo.resource.model.bo.cluster.CustomResourceDefinition;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface CustomResourceDefinitionService {
  /**
   * 查询自定义资源集合
   *
   * @param paramVo
   * @return
   */
  Page<List<CustomResourceDefinition>> listCustomResourceDefinition(SearchParamDTO paramVo);
  
  /**
   * 读取自定义资源集合
   *
   * @param name 自定义资源名称
   * @return
   */
  CustomResourceDefinition readCustomResourceDefinition(String name);
  
  JSONObject listCustomResourceDefinitionSubitem(String name);
  
}
