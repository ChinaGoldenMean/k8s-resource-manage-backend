package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.DaemonSet;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.V1DaemonSet;

import java.util.List;

public interface DaemonSetService {
  /**
   * 根据查询对象获取守护进程集
   *
   * @param paramVo
   * @return
   */
  Page<List<DaemonSet>> listDaemonSet(SearchParamDTO paramVo);
  
  /**
   * 转换守护进程集
   *
   * @param v1DaemonSet
   * @return
   */
  DaemonSet toDaemonSet(V1DaemonSet v1DaemonSet);
  
  /**
   * 读取创建者
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Builder readBuilder(String nameSpace, String name);
  
  /**
   * 读取守护进程集
   *
   * @param nameSpace
   * @param name
   * @return
   */
  DaemonSet readDaemonSet(String nameSpace, String name);
}
