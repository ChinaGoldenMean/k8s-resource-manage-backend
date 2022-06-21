package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.Pod;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.PodVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.PodService;
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
@RequestMapping(value = "/api/pod")
@Api(tags = "容器组管理")
@Slf4j
public class K8sManagePodController {
  
  @Autowired
  PodService podService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的容器组列表")
  public JsonResult<Page<PodVo>> listPod(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<PodVo> list =
        podService.listPod(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/node/list")
  @ApiOperation("获取某个节点的所有的容器组列表")
  @ApiImplicitParam(name = "nodeName", value = "节点名称", required = true, paramType = "query", dataType = "String")
  public JsonResult<Page<PodVo>> listPodByNode(SearchParamDTO paramVo, String nodeName) {
    log.debug("paramVo: {} ", paramVo);
    paramVo.validateParams();
    Page<PodVo> list =
        podService.listPodByNode(paramVo, nodeName);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取容器组")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "容器组名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Pod> getPodInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    Pod pod =
        podService.readPod(nameSpace, name);
    
    return JsonResult.success(pod);
  }
  
}
