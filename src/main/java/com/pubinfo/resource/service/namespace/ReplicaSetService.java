package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.ReplicaSet;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.ReplicaSetVo;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetList;

public interface ReplicaSetService {
  /**
   * 根据查询对象获取副本值集合
   *
   * @param paramVo
   * @return
   */
  Page<ReplicaSetVo> listReplicaSet(SearchParamDTO paramVo);
  
  /**
   * 转换副本值
   *
   * @param v1Job
   * @return
   */
  ReplicaSet toReplicaSet(V1ReplicaSet v1Job);
  
  /**
   * 读取副本值
   *
   * @param namespace
   * @param name
   * @return
   */
  ReplicaSet readReplicaSet(String namespace, String name);
  
  /**
   * 读取创建者
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Builder readBuilder(String nameSpace, String name);
  
  /**
   * 根据元数据查询副本值集合
   *
   * @param v1ObjectMeta
   * @return
   */
  ReplicaSetVo listByLabel(V1ObjectMeta v1ObjectMeta, String ownerReferencesName);
  
  /**
   * 根据元数据查询副本值集合
   *
   * @param v1ObjectMeta
   * @return
   */
  V1ReplicaSetList listByLabel2(V1ObjectMeta v1ObjctMetea,V1LabelSelector v1LabelSelector, String ownerReferencesName);
  
  /**
   * 根据选择器查询
   *
   * @param v1LabelSelector
   * @param namespace
   * @return
   */
  V1ReplicaSetList listV1ReplicaSetBySelector(V1LabelSelector v1LabelSelector, String namespace, String ownerReferencesName);
}
