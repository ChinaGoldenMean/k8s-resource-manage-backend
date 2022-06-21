package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.ConfigMap;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.ConfigMapService;
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
@RequestMapping(value = "/api/configmap")
@Api(tags = "字典管理")
@Slf4j
public class K8sManageConfigMapController {
  @Autowired
  ConfigMapService configMapService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的字典列表")
  public JsonResult<Page<List<ConfigMap>>> listConfigMap(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<ConfigMap>> list =
        configMapService.listConfigMap(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取字典")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "字典名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<ConfigMap> getConfigMapInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("name nameSpace: {} ", name, nameSpace);
    ConfigMap configMap =
        configMapService.readConfigMap(nameSpace, name);
    
    return JsonResult.success(configMap);
  }
}
