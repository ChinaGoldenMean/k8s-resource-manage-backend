package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.StatefulSet;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.V1StatefulSet;

import java.util.List;

public interface StatefulSetService {
  /**
   * 根据查询对象获取有状态副本集集合
   *
   * @param paramVo
   * @return
   */
  Page<List<StatefulSet>> listStatefulSet(SearchParamDTO paramVo);
  
  /**
   * 转换有状态副本集
   *
   * @param v1beta1StatefulSet
   * @return
   */
  StatefulSet toStatefulSet(V1StatefulSet v1beta1StatefulSet);
  
  /**
   * 读取创建者
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Builder readBuilder(String nameSpace, String name);
  
  /**
   * 读取有状态副本集
   *
   * @param namespace
   * @param name
   * @return
   */
  StatefulSet readStatefulSet(String namespace, String name);
  
}
