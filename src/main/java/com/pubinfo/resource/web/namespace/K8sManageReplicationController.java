package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.Replication;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.ReplicationControllerService;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/replication")
@Api(tags = "副本控制器管理")
@Slf4j
public class K8sManageReplicationController {
  @Autowired
  ReplicationControllerService controllerService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的副本控制器列表")
  public JsonResult<Page<List<Replication>>> list(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<Replication>> list =
        controllerService.listReplicationController(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取副本控制器")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "副本控制器名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Replication> getPodInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    Replication replication =
        controllerService.readReplication(nameSpace, name);
    return JsonResult.success(replication);
  }
}
