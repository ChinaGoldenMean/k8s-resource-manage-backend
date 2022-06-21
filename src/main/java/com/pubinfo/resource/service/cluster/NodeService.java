package com.pubinfo.resource.service.cluster;

import com.pubinfo.resource.model.bo.cluster.Node;
import com.pubinfo.resource.model.dto.NodeDTO;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface NodeService {
  /**
   * 修改节点标签
   *
   * @param nodeDTO
   * @param isDelete
   * @return
   */
  boolean patchNodeLables(NodeDTO nodeDTO, boolean isDelete);
  
  /**
   * 隔离与恢复节点
   *
   * @param nodeName
   * @param isSchedule
   * @return
   */
  boolean scheduleNode(String nodeName, boolean isSchedule);
  
  /**
   * 根据查询对象获取节点集合
   *
   * @param vo
   * @return
   */
  Page<List<Node>> listNode(SearchParamDTO vo);
  
  /**
   * 查询节点
   *
   * @param nodeName
   * @return
   */
  Node readNode(String nodeName);
}
