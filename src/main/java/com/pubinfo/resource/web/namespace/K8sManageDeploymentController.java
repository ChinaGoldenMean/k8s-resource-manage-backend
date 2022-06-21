package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.Deployment;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.DeploymentVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.DeploymentService;
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
@RequestMapping(value = "/api/deployment")
@Api(tags = "部署管理")
@Slf4j
public class K8sManageDeploymentController {
  
  @Autowired
  DeploymentService deploymentService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的部署列表")
  public JsonResult<Page<DeploymentVo>> list(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<DeploymentVo> list =
        deploymentService.listDeployment(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取部署")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "部署名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Deployment> readDeploymentInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    Deployment deployment =
        deploymentService.readDeployment(nameSpace, name);
    return JsonResult.success(deployment);
  }
}
