package com.pubinfo.resource.web.cluster;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.cluster.Namespace;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.cluster.NamespaceService;
import com.pubinfo.resource.service.common.K8sApiService;
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

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/api/namespace")
@Api(tags = "命名空间管理")
@Slf4j
public class K8sManageNamespaceController {
  
  @Autowired
  private NamespaceService namespaceResource;
  
  @Autowired
  EventsService eventsService;
  
  @Autowired
  K8sApiService apiService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的namespace列表")
  public JsonResult<Page<List<Namespace>>> getNamespaceList(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<Namespace>> namespacePageList = namespaceResource.getNamespaceList(paramVo);
    return JsonResult.success(namespacePageList);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("根据名称查询namespace")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "命名空间名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Namespace> readNameSpacesByName(@RequestParam("name") @NotNull(message = "name不能为空") String name) {
    log.debug("name: {} ", name);
    Namespace vo =
        namespaceResource.nameSpacesByName(name);
    return JsonResult.success(vo);
  }
  
  
  @GetMapping(value = "/events")
  @ApiOperation("根据名称查询事件集体")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "命名空间名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Namespace> listEvents(@RequestParam("name") String name, SearchParamDTO paramVo) {
    log.debug("name: {} ", name);
    log.debug("SearchParamDTO: {} ", paramVo);
    
    paramVo.validateParams();
    
    Namespace vo =
        eventsService.listEventByParam(name, paramVo);
    return JsonResult.success(vo);
  }
  
}
