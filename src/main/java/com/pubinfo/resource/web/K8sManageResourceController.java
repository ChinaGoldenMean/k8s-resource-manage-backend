package com.pubinfo.resource.web;

import com.alibaba.fastjson.JSONObject;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.config.ApplicationConfig;

import com.pubinfo.resource.model.dto.K8sCreateDTO;
import com.pubinfo.resource.model.dto.K8sYamlDTO;
import com.pubinfo.resource.model.dto.ScaleYamlDTO;
import com.pubinfo.resource.model.dto.YamlBaseDTO;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.common.K8sService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/resource")
@Api(tags = "资源管理")
@Slf4j
public class K8sManageResourceController {
  @Autowired
  K8sService k8sService;
  
  @Autowired
  ApplicationConfig config;
  @Autowired
  K8sApiService k8sApi;
  
  
  @PostMapping(value = "/create/json")
  @ApiOperation(value = "通过json创建资源")
  public JsonResult<String> createByJson(@RequestBody K8sCreateDTO dto) {
    List<K8sYamlDTO> k8sYamlDTOList = new ArrayList<>();
    String yamlJson = dto.getYamlJson();
    log.debug("yamlOrJson:  {} ", yamlJson);
    if (StringUtils.isEmpty(yamlJson)) {
      throw new ServiceException(ResultCode.REQUEST_PARA_ERROR);
    }
    if (JSONObject.isValidObject(yamlJson)) {
      JSONObject json = JSONObject.parseObject(yamlJson);
      K8sYamlDTO k8SYamlDTO = K8sUtils.transJson2Vo(json, k8sApi.getNamespace());
      k8sYamlDTOList.add(k8SYamlDTO);
    } else {
      String[] yamlStrs = yamlJson.split("---");
      if (yamlStrs != null) {
        for (int i = 0; i < yamlStrs.length; i++) {
          Yaml yaml = new Yaml();
          Map<String, Object> obj = yaml.load(yamlStrs[i]);
          JSONObject json = new JSONObject(obj);
          K8sYamlDTO k8SYamlDTO = K8sUtils.transJson2Vo(json, k8sApi.getNamespace());
          
          k8sYamlDTOList.add(k8SYamlDTO);
        }
      }
    }
    int success = 0;
    int fail = 0;
    if (!k8sYamlDTOList.isEmpty()) {
      for (K8sYamlDTO k8sYamlDTO : k8sYamlDTOList) {
        if (k8sYamlDTO == null) {
          return JsonResult.error(ResultCode.VALIDATE_FAILED);
        }
        if (k8sService.createResource(k8sYamlDTO)) {
          success++;
        } else {
          fail++;
        }
      }
      
    }
    
    if (success > 0) {
      return JsonResult.success("发布成功");
    } else {
      return JsonResult.error(0, "发布失败");
    }
    
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取资源")
  public JsonResult<JSONObject> readResourceInfo(YamlBaseDTO yamlBaseDTO) {
    log.debug("yamlBaseDTO: {} ", yamlBaseDTO);
    return JsonResult.success(k8sService.getResourceByNameUseOKHttp(yamlBaseDTO));
  }
  
  
  @PostMapping(value = "/update")
  @ApiOperation("更新资源")
  public JsonResult<String> putResourceInfo(@RequestBody JSONObject yamlJson) {
    log.debug("yamlJson: {} ", yamlJson);
    K8sYamlDTO k8SYamlDTO = K8sUtils.transJson2Vo(yamlJson);
    k8sService.updateResource(k8SYamlDTO);
    return JsonResult.success("成功!");
  }
  
  
  @DeleteMapping(value = "/delete")
  @ApiOperation("删除资源")
  public JsonResult<String> deleteResourceInfo(YamlBaseDTO yamlBaseDTO) {
    log.debug("yamlBaseDTO: {} ", yamlBaseDTO);
    k8sService.deleteNamespacedResource(yamlBaseDTO);
    return JsonResult.success("成功!");
  }
  
  @DeleteMapping(value = "/delete/crd")
  @ApiOperation("删除CRD资源")
  public JsonResult<String> deleteCRDResourceInfo(String url) {
    log.debug("url: {} ", url);
    k8sService.crdResourceDelete(url);
    return JsonResult.success("成功!");
  }
  
  
  @PutMapping(value = "/scale")
  @ApiOperation("对资源进行扩缩容")
  public JsonResult<Integer> scaleResourceResouse(@RequestBody ScaleYamlDTO scaleYamlVo) {
    log.debug("scaleYamlVo: {} ", scaleYamlVo);
    return JsonResult.success(
        k8sService.scaleModuleSize(scaleYamlVo));
  }
}
