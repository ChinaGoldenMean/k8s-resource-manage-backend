package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.ReplicaSet;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.ReplicaSetVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.ReplicaSetService;
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
@RequestMapping(value = "/api/replicaset")
@Api(tags = "副本值管理")
@Slf4j
public class K8sManageReplicaSetController {
  @Autowired
  ReplicaSetService replicaSetService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的副本值列表")
  public JsonResult<Page<ReplicaSetVo>> listReplicaSetController(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<ReplicaSetVo> list =
        replicaSetService.listReplicaSet(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取副本值")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "副本值名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<ReplicaSet> replicaSetControllerInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    ReplicaSet replicaSet =
        replicaSetService.readReplicaSet(nameSpace, name);
    return JsonResult.success(replicaSet);
  }
}
