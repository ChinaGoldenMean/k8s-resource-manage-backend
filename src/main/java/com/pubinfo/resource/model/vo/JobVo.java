package com.pubinfo.resource.model.vo;

import com.pubinfo.resource.model.bo.namespace.Job;
import com.pubinfo.resource.model.bo.namespace.Status;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@ApiModel(value = "任务列表集合")
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobVo implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  
  @ApiModelProperty(value = "任务集合")
  private List<Job> jobs = new ArrayList<>();
  
  private Status status = new Status();
  
  public JobVo(List<Job> jobs, List<V1Job> v1jobs) {
    this.jobs = jobs;
    setStatus(v1jobs);
  }
  private void setStatus(List<V1Job> v1jobs) {
    if (CollectionUtils.isNotEmpty(v1jobs)) {
      Integer running = 0;
      Integer failed = 0;
      for (V1Job v1Job : v1jobs) {
        V1JobStatus jobStatus = v1Job.getStatus();
        if (jobStatus == null){
          running++;
        }else{
          Integer succeeded = jobStatus.getSucceeded();
          if(succeeded != null && succeeded > 0){
            running++;
          }else{
            failed++;
          }
        }
       
      }
      this.status.setRunning(running);
      this.status.setFailed(failed);
    }
  }
}
