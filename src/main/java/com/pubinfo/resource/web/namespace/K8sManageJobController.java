package com.pubinfo.resource.web.namespace;


import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.model.bo.namespace.Job;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.JobVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.common.K8sService;
import com.pubinfo.resource.service.namespace.JobService;
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
@RequestMapping(value = "/api/job")
@Api(tags = "任务管理")
@Slf4j
public class K8sManageJobController {
  @Autowired
  JobService jobService;
  @Autowired
  K8sService k8sService;
  
  
  @GetMapping(value = "/list")
  @ApiOperation("获取所有的任务列表")
  public JsonResult<Page<JobVo>> listJob(SearchParamDTO paramVo) {
    log.debug("SearchParamDTO: {} ", paramVo);
    paramVo.validateParams();
    Page<JobVo> list =
        jobService.listJob(paramVo);
    return JsonResult.success(list);
  }
  
  
  @GetMapping(value = "/detail")
  @ApiOperation("读取任务")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "nameSpace", value = "命名空间", required = true, paramType = "query", dataType = "String"),
      @ApiImplicitParam(name = "name", value = "任务名称", required = true, paramType = "query", dataType = "String")
  })
  public JsonResult<Job> getJobInfo(@RequestParam("nameSpace") String nameSpace, @RequestParam("name") String name) {
    log.debug("nameSpace name: {} ", nameSpace, name);
    
    Job job =
        jobService.readJob(nameSpace, name);
    
    return JsonResult.success(job);
  }
  
}
