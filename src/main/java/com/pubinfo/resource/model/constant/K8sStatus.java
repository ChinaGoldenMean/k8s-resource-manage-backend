package com.pubinfo.resource.model.constant;

import org.springframework.util.StringUtils;

public class K8sStatus {
  
  public static enum PodPhase {
    /*
    等待中
     */
    PENDING("Pending"),
    /*
    运行中
     */
    RUNNING("Running"),
    /*
    错误
     */
    FAILED("Failed"),
    /*
    未知的
     */
    UNKNOWN("Unknown"),
    /*
    成功的
     */
    SUCCEEDED("Succeeded");
    private String name;
    
    public static boolean isExists(String str) {
      boolean flag = false;
      if (StringUtils.isEmpty(str)) {
        return flag;
      }
      
      for (PodPhase podStatus : values()) {
        if (str.equals(podStatus.getName())) {
          flag = true;
        }
      }
      return flag;
    }
    
    PodPhase(String nameVo) {
      this.name = nameVo;
    }
    
    public String getName() {
      return name;
    }
    
    private void setName(String name) {
      this.name = name;
    }
  }
}
