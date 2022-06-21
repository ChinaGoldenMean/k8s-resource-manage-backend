package com.pubinfo.resource.web.namespace;

import com.alibaba.fastjson.JSONObject;

import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.Ingress;
import com.pubinfo.resource.model.constant.KindEnum;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.dto.YamlBaseDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.common.K8sService;
import com.pubinfo.resource.service.namespace.IngressService;
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
@RequestMapping(value = "/api/ingress")
@Api(tags = "访问权管理")
@Slf4j
public class K8sManageIngressController {
  
  @Autowired
  K8sService k8sService;
  @Autowired
  IngressService ingressService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的访问权列表")
  public JsonResult<Page<List<Ingress>>> list(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<Ingress>> list =
        ingressService.listIngress(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取访问权")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "访问权名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<JSONObject> readIngressInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    YamlBaseDTO yamlBaseDTO = new YamlBaseDTO();
    yamlBaseDTO.setKind(KindEnum.INGRESS.getKind());
    yamlBaseDTO.setName(name);
    yamlBaseDTO.setNameSpace(nameSpace);
    JSONObject json =
        k8sService.getResourceByNameUseOKHttp(yamlBaseDTO);
    return JsonResult.success(json);
  }
}
