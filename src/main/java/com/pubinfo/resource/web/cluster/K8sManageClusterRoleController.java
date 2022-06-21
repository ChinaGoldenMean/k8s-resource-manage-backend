package com.pubinfo.resource.web.cluster;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.cluster.Role;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.ClusterRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/role")
@Api(tags = "角色管理")
@Slf4j
public class K8sManageClusterRoleController {
  @Autowired
  ClusterRoleService clusterRole;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的角色列表")
  public JsonResult<Page<Role>> volume(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    
    Page<Role> dto = clusterRole.listRoleAndClusterRole(paramVo);
    return JsonResult.success(dto);
  }
  
  
  @GetMapping(value = "/role")
  @ApiOperation("根据命名空间与名称查询角色")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "角色名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Role> readRoleByNameSpaceAndName(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("name nameSpace: {} ", name, nameSpace);
    Role role =
        clusterRole.readRole(nameSpace, name);
    return JsonResult.success(role);
  }
  
  
  @GetMapping(value = "/clusterRole")
  @ApiOperation("根据名称查询集群角色")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "角色名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Role> readClusterRoleByName(@RequestParam("name") String name) {
    log.debug("name : {} ", name);
    Role role =
        clusterRole.readRole(null, name);
    return JsonResult.success(role);
  }
}
