package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.Secret;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface SecretService {
  /**
   * 根据查询对象获取保密字典集合
   *
   * @param paramVo
   * @return
   */
  Page<List<Secret>> listSecret(SearchParamDTO paramVo);
  
  /**
   * 读取保密字典
   *
   * @param namespace
   * @param name
   * @return
   */
  Secret readSecret(String namespace, String name);
}
