package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.ConfigMap;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface ConfigMapService {
  /**
   * 根据查询对象获取配置字典
   *
   * @param paramVo
   * @return
   */
  Page<List<ConfigMap>> listConfigMap(SearchParamDTO paramVo);
  
  /**
   * 读取配置字典
   *
   * @param nameSpace
   * @param name
   * @return
   */
  ConfigMap readConfigMap(String nameSpace, String name);
}
