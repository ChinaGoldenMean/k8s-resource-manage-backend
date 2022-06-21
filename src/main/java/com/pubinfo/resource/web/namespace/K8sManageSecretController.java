package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.Secret;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.SecretService;
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
@RequestMapping(value = "/api/secret")
@Api(tags = "保密字典管理")
@Slf4j
public class K8sManageSecretController {
  @Autowired
  SecretService secretService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的保密字典列表")
  public JsonResult<Page<List<Secret>>> listPersistentVolume(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<Secret>> listPage =
        secretService.listSecret(paramVo);
    return JsonResult.success(listPage);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取保密字典")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "保密字典名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Secret> getPodInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    Secret secret =
        secretService.readSecret(nameSpace, name);
    
    return JsonResult.success(secret);
  }
}
