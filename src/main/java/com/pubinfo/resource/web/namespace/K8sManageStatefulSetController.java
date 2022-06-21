package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.StatefulSet;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.StatefulSetService;
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
@RequestMapping(value = "/api/statefulset")
@Api(tags = "有状态副本集管理")
@Slf4j
public class K8sManageStatefulSetController {
  @Autowired
  StatefulSetService statefulSetService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的有状态副本集列表")
  public JsonResult<Page<List<StatefulSet>>> list(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<StatefulSet>> list =
        statefulSetService.listStatefulSet(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取有状态副本集")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "副本集名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<StatefulSet> statefulSetInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    StatefulSet statefulSet =
        statefulSetService.readStatefulSet(nameSpace, name);
    return JsonResult.success(statefulSet);
  }
}
