package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.CronJob;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.namespace.CronJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cronjob")
@Api(tags = "定时任务管理")
@Slf4j
public class K8sManageCronJobController {
  
  @Autowired
  CronJobService cronJobService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的定时任务列表")
  public JsonResult<Page<List<CronJob>>> listConfigMap(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<List<CronJob>> list =
        cronJobService.listCronJob(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取定时任务")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "定时任务名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<CronJob> getCronJobInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    CronJob cronJob =
        cronJobService.readCronJob(nameSpace, name);
    return JsonResult.success(cronJob);
  }
  
  @PostMapping(value = "/job")
  @ApiOperation("执行一次定时任务")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "job名称", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "selfLink", value = "访问path", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Boolean> createJob(@RequestParam("name") String name, @RequestParam("selfLink") String selfLink) {
   
    return JsonResult.success(cronJobService.createJob(name, selfLink));
  }
}
