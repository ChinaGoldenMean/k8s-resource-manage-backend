package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.Ingress;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface IngressService {
  /**
   * 根据查询对象获取访问权集合
   *
   * @param paramVo
   * @return
   */
  Page<List<Ingress>> listIngress(SearchParamDTO paramVo);
  
  /**
   * 读取访问权
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Ingress readIngress(String nameSpace, String name);
}
