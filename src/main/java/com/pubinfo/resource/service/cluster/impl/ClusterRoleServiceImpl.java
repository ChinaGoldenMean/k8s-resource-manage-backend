package com.pubinfo.resource.service.cluster.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.cluster.Role;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.ClusterRoleService;
import com.pubinfo.resource.service.common.K8sApiService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClusterRoleServiceImpl extends K8sSearch implements ClusterRoleService {
  
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<Role> listRoleAndClusterRole(SearchParamDTO vo) {
    List<Role.RoleInfo> roleList = searchRoleList();
    
    String sortBy = vo.getSortBy();
    int limit = vo.getItemsPerPage();
    int skip = vo.getSkip();
    if (!StringUtils.isEmpty(vo.getFilterBy())) {
      //
      roleList = roleList.stream().filter(
          roleInfo -> {
            return roleInfo.getMeta_name().contains(vo.getFilterBy());
          }
      ).collect(Collectors.toList());
    }
    totalItem = roleList.size();
    roleList = roleList.stream().sorted((roleInfo1, roleInfo2) -> {
      return sort(roleInfo1, roleInfo2, sortBy);
    }).skip(skip).limit(limit).collect(Collectors.toList());
    
    Role dto = new Role();
    dto.setItems(roleList);
    Page<Role> listPage = new Page<>(vo, dto, totalItem, roleList.size());
    return listPage;
  }
  
  @Override
  public Role readRole(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    RbacAuthorizationV1Api rbacAuthorizationV1Api = k8sService.getRbacAuthorizationV1Api();
    V1Role v1Role = null;
    V1ClusterRole v1ClusterRole = null;
    Role role = null;
    try {
      if (StringUtils.isEmpty(nameSpace)) {
        
        v1ClusterRole = rbacAuthorizationV1Api.readClusterRole(name, K8sParam.ReadParam.pretty);
        
        role = new Role(v1ClusterRole);
      } else {
        v1Role = rbacAuthorizationV1Api.readNamespacedRole(name, nameSpace, K8sParam.ReadParam.pretty);
        role = new Role(v1Role);
      }
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.QUERY_ROLE_FAIL, K8sUtils.getMessage(e));
    }
    return role;
  }
  
  @Override
  public List<Role.RoleInfo> searchRoleList() {
    RbacAuthorizationV1Api rbacAuthorizationV1Api = k8sService.getRbacAuthorizationV1Api();
    List<V1ClusterRole> clusterRoleItems = null;
    List<V1Role> roleItems = null;
    try {
      
      clusterRoleItems = rbacAuthorizationV1Api.listClusterRole(K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      
      roleItems = rbacAuthorizationV1Api.listRoleForAllNamespaces(K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.pretty, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_ROLE_FAIL, K8sUtils.getMessage(e));
    }
    
    List<Role.RoleInfo> roleList = new ArrayList<>();
    
    if (!clusterRoleItems.isEmpty()) {
      clusterRoleItems.forEach(clusterRole -> {
        roleList.add(new Role.RoleInfo(clusterRole.getMetadata(), "clusterrole"));
      });
      
    }
    if (!roleItems.isEmpty()) {
      roleItems.forEach(role -> {
        roleList.add(new Role.RoleInfo(role.getMetadata(), "role"));
      });
      
    }
    return roleList;
  }
}
