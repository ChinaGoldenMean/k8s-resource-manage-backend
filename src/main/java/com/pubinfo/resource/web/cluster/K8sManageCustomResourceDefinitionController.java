package com.pubinfo.resource.web.cluster;

import com.alibaba.fastjson.JSONObject;

import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.cluster.CustomResourceDefinition;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.CustomResourceDefinitionService;
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
@RequestMapping(value = "/api/CustomResourceDefinition")
@Api(tags = "自定义资源管理")
@Slf4j
public class K8sManageCustomResourceDefinitionController {
  @Autowired
  CustomResourceDefinitionService crdService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的自定义资源列表")
  public JsonResult<Page<List<CustomResourceDefinition>>> listCustomResourceDefinition(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<CustomResourceDefinition>> listPage = crdService.listCustomResourceDefinition(paramVo);
    return JsonResult.success(listPage);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("根据名称查询自定义资源")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "自定义资源名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<CustomResourceDefinition> readCustomResourceDefinition(@RequestParam("name") String name) {
    log.debug("name: {} ", name);
    CustomResourceDefinition customResourceDefinition =
        crdService.readCustomResourceDefinition(name);
    return JsonResult.success(customResourceDefinition);
  }
  
  
  @GetMapping(value = "/subitem")
  @ApiOperation("根据名称查询自定义资源子项列表")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "自定义资源名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<JSONObject> listCustomResourceDefinitionSubitem(@RequestParam("name") String name) {
    log.debug("name: {} ", name);
    JSONObject jsonObject =
        crdService.listCustomResourceDefinitionSubitem(name);
    return JsonResult.success(jsonObject);
  }
}
