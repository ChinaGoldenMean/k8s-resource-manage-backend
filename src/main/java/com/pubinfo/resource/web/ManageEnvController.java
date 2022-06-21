package com.pubinfo.resource.web;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.config.ApplicationConfig;
import com.pubinfo.resource.domain.ManageEnv;

import com.pubinfo.resource.model.dto.ManageEnvParam;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.model.vo.base.PageParamBase;
import com.pubinfo.resource.service.ManageEnvService;
import com.pubinfo.resource.service.common.K8sApiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing ManageEnv.
 *
 * @author ctsi-biyi-generator
 */
@RestController
@RequestMapping("/api")
@Api(tags = "环境管理")
@Slf4j
public class ManageEnvController {
  
  @Autowired
  ApplicationConfig applicationConfig;
  @Autowired
  K8sApiService k8sApiService;
  
  
  private static final String ENTITY_NAME = "manageEnv";
  private final ManageEnvService manageEnvService;
  
  public ManageEnvController(ManageEnvService manageEnvService) {
    this.manageEnvService = manageEnvService;
  }
  
  
  @ApiOperation("创建环境")
  @PostMapping("/manageEnvs")
  public JsonResult<Boolean> createManageEnv(@RequestBody ManageEnvParam manageEnvParam) {
    log.debug("REST request to save ManageEnv : {}", manageEnvParam);
    
    Boolean status = manageEnvService.createManageEnv(manageEnvParam);
    
      return JsonResult.success(status);
   
  }
  @ApiOperation("是否为管理员")
  @GetMapping("/isManage")
  public JsonResult<Boolean> isManage() {
    return JsonResult.success(k8sApiService.isK8sManage());
  }
  
  @PutMapping("/manageEnvs")
  @ApiOperation("修改环境")
  public JsonResult<Boolean> updateManageEnv(@RequestBody ManageEnv manageEnv) {
    log.debug("REST request to update ManageEnv : {}", manageEnv);
    
    return JsonResult.success( manageEnvService.updateById(manageEnv));
  }
  
  
  @GetMapping("/manageEnvsList")
  @ApiOperation("查询环境列表")
  public JsonResult<Page<List<ManageEnv>>>
   getManageEnvsList(PageParamBase manageEnvExample) {
    log.debug("REST request to get ManageEnvsList");
    
    return JsonResult.success(new Page<>(manageEnvService.list()));
  }
  
  
  @GetMapping("/manageEnvs/{id}")
  @ApiOperation("根据id查询环境信息")
  public JsonResult<ManageEnv> getManageEnv(@PathVariable Integer id) {
    log.debug("REST request to get ManageEnv : {}", id);
    ManageEnv manageEnv = manageEnvService.getById(id);
    return JsonResult.success(manageEnv);
  }
  
  
  @PostMapping(value = "/manageEnvs/check/{id}")
  @ApiOperation("是否可用")
  public JsonResult<Boolean> check(@PathVariable Integer id) {
    log.debug("id: {} ", id);
    
    return JsonResult.success(manageEnvService.check(id));
  }
  
  
  @ApiOperation("根据id删除环境")
  @DeleteMapping("/manageEnvs/{id}")
  public JsonResult<String> deleteManageEnv(@PathVariable Integer id) {
    log.debug("REST request to delete ManageEnv : {}", id);
    manageEnvService.removeById(id);
    return  JsonResult.success("ok");
  }
  
}
