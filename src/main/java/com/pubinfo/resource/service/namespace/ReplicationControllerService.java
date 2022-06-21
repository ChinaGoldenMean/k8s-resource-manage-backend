package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Replication;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.V1ReplicationController;

import java.util.List;

public interface ReplicationControllerService {
  /**
   * 根据查询对象获取副本控制器集合
   *
   * @param paramVo
   * @return
   */
  Page<List<Replication>> listReplicationController(SearchParamDTO paramVo);
  
  /**
   * 转换副本控制器
   *
   * @param v1ReplicationController
   * @return
   */
  Replication toReplication(V1ReplicationController v1ReplicationController);
  
  /**
   * 读取创建者
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Builder readBuilder(String nameSpace, String name);
  
  /**
   * 读取副本控制器
   *
   * @param namespace
   * @param name
   * @return
   */
  Replication readReplication(String namespace, String name);
  
}
