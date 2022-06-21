package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.PersistentVolumeClaim;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.PersistentVolumeClaimService;
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
@RequestMapping(value = "/api/volumeclaim")
@Api(tags = "持久化存储卷声明管理")
@Slf4j
public class K8sManagePersistentVolumeClaimController {
  @Autowired
  PersistentVolumeClaimService persistentVolumeClaimService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的持久化数据卷声明列表")
  public JsonResult<Page<List<PersistentVolumeClaim>>> listPersistentVolume(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<PersistentVolumeClaim>> listPage =
        persistentVolumeClaimService.listPersistentVolumeClaim(paramVo);
    return JsonResult.success(listPage);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取持久化数据卷声明")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "持久化存储卷声明名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<PersistentVolumeClaim> getPersistentVolumeInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    PersistentVolumeClaim persistentVolumeClaim =
        persistentVolumeClaimService.readPersistentVolumeClaim(nameSpace, name);
    
    return JsonResult.success(persistentVolumeClaim);
  }
}
