package com.pubinfo.resource.web.cluster;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.cluster.StorageClass;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.StorageClassService;
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
@RequestMapping(value = "/api/storage")
@Api(tags = "存储类管理")
@Slf4j
public class K8sManageStorageClassController {
  
  @Autowired
  private StorageClassService storageClassService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的存储类列表")
  public JsonResult<Page<List<StorageClass>>> listStorageClass(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<StorageClass>> list =
        storageClassService.listStorageClass(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取持久化数据卷")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "storageClassName", value = "存储类名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<StorageClass> getStorageClassInfo(@RequestParam("storageClassName") String storageClassName) {
    log.debug("storageClassName: {} ", storageClassName);
    StorageClass storageClass =
        storageClassService.readStorageClass(storageClassName);
    return JsonResult.success(storageClass);
  }
}
