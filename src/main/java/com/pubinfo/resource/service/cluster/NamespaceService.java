package com.pubinfo.resource.service.cluster;

import com.pubinfo.resource.model.bo.cluster.Namespace;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface NamespaceService {
  
  /**
   * 获取namespace数据列表
   *
   * @param paramVo
   * @return
   */
  Page<List<Namespace>> getNamespaceList(SearchParamDTO paramVo);
  
  /**
   * 根据名称查询命名空间
   *
   * @param name
   * @return
   */
  Namespace nameSpacesByName(String name);
  
}
