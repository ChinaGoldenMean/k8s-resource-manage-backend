package com.pubinfo.resource.service.cluster;

import com.pubinfo.resource.model.bo.cluster.Role;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface ClusterRoleService {
  /**
   * 角色与集群角色合并查询
   *
   * @param paramVo 查询对象
   * @return
   */
  Page<Role> listRoleAndClusterRole(SearchParamDTO paramVo);
  
  /**
   * 读取角色
   *
   * @param nameSpace 命名空间
   * @param name      角色名称
   * @return
   */
  Role readRole(String nameSpace, String name);
  
  /**
   * 查询角色信息集合
   *
   * @return
   */
  List<Role.RoleInfo> searchRoleList();
}
