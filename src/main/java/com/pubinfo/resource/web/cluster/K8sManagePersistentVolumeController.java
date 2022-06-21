package com.pubinfo.resource.web.cluster;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.cluster.PersistentVolume;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.PersistentVolumeService;
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
@RequestMapping(value = "/api/volume")
@Api(tags = "持久化数据卷管理")
@Slf4j
public class K8sManagePersistentVolumeController {
  @Autowired
  PersistentVolumeService persistentVolumeService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的持久化数据卷列表")
  public JsonResult<Page<List<PersistentVolume>>> listPersistentVolume(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<PersistentVolume>> persistentVolumeList =
        persistentVolumeService.listPersistentVolume(paramVo);
    return JsonResult.success(persistentVolumeList);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取持久化数据卷")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "persistentVolumeName", value = "持久化数据卷名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<PersistentVolume> getPersistentVolumeInfo(@RequestParam("persistentVolumeName") String persistentVolumeName) {
    log.debug("persistentVolumeName: {} ", persistentVolumeName);
    PersistentVolume persistentVolume =
        persistentVolumeService.readPersistentVolume(persistentVolumeName);
    return JsonResult.success(persistentVolume);
  }
}
