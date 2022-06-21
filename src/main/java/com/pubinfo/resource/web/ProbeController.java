package com.pubinfo.resource.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pubinfo.resource.common.vo.JsonResult;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.common.K8sService;
import io.kubernetes.client.Discovery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * REST controller for managing ManageEnv.
 *
 * @author ctsi-biyi-generator
 */
@RestController
@RequestMapping("/api")
@Api(tags = "探针专用")
@Slf4j
public class ProbeController {
  @Autowired
  K8sService k8sService;
  @Autowired
  K8sApiService k8sApiService;
  
  @GetMapping("/probe")
  @ApiOperation("探针")
  public JsonResult<String> probe() {
    return JsonResult.success("我还活着");
  }
  
  @GetMapping("/test")
  @ApiOperation("url测试")
  public JsonResult<JSONObject> test(String url) {
    String result = k8sService.executeHttpGetBack(url);
    
    return JsonResult.success(JSON.parseObject(result));
  }
  
  @SneakyThrows
  @GetMapping("/apiTest")
  @ApiOperation("api测试")
  public JsonResult<Object> test() {
    Discovery discovery = k8sApiService.getDiscovery();
    Set<Discovery.APIResource> set = discovery.findAll();
    
    return JsonResult.success(JSONObject.toJSONString(set));
  }
  @SneakyThrows
  @GetMapping("/apiTestCrd")
  @ApiOperation("api测试")
  public JsonResult<JSONObject> testCrd() {
    String url = "/apis/";
    url+="";
    String result = k8sService.executeHttpGetBack(url);
    return JsonResult.success(JSON.parseObject(result));
  }
  @SneakyThrows
  @GetMapping("/apiTestDelete")
  @ApiOperation("测试删除")
  public JsonResult<String> testCrd(String url) {
    
    url+="";
    String result = k8sService.crdResourceDelete(url);
    return JsonResult.success(result);
  }
//
//  @GetMapping("/token")
//  @ApiOperation("权限测试")
//  public JsonResult<CscpUserDetail> tokenTest() {
//    BiyiUserAuthDetailDto userAuthDetailDto = userManager.getUserDetails(SecurityUtils.getCurrentUserName());
////    BiyiUserAuthDetailDto userAuthDetailDto = userManager.getUserDetails("wanglei1");
//    log.info("userAuthDetailDto:" + userAuthDetailDto.toString());
//
//    List<ProjectDTO>  dtoList = k8sApiService.getProjects();
//    if(dtoList.size()>0){
//      for (ProjectDTO dto : dtoList) {
//        System.out.println("ProjectDTO:"+dto.getName());
//      }
//    }
//    String namespace = k8sApiService.getNamespace();
//    log.info("当前命名空间:" +namespace);
//
//    SecurityContext securityContext = SecurityContextHolder.getContext();
//    Authentication authentication = securityContext.getAuthentication();
//    String role = "";
//    Collection<? extends GrantedAuthority> list = authentication.getAuthorities();
//    list.stream().forEach(grantedAuthority -> {
//      log.info("当前用户角色" + ((GrantedAuthority) grantedAuthority).getAuthority());
//    });
//    log.info("当前用户id：" + SecurityUtils.getCurrentUserId());
//    log.info("当前用户名称：" + SecurityUtils.getCurrentUserName());
//
//    Optional<CscpUserDetail> cscpUserDetail = SecurityUtils.getCurrentUser();
//    log.info("当前用户认证" + SecurityUtils.isAuthenticated());
//
//
//
//    return JsonResult.success(cscpUserDetail.get());
//  }
  
}
