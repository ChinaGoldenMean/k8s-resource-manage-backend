package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.constant.K8sStatus;
import io.kubernetes.client.openapi.models.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "状态")
public class Status implements Serializable {
  
  private static final long serialVersionUID = 3886609240082575338L;
  @ApiModelProperty(value = "运行数")
  protected Integer running = 0;
  @ApiModelProperty(value = "挂起数")
  protected Integer pending = 0;
  @ApiModelProperty(value = "失败数")
  protected Integer failed = 0;
  @ApiModelProperty(value = "成功数")
  protected Integer succeeded = 0;
  
  public Status(Collection<V1Deployment> deployments) {
    if (CollectionUtils.isNotEmpty(deployments)) {
      deployments.forEach(v1Deployment -> {
        List<V1DeploymentCondition> list = v1Deployment.getStatus().getConditions();
        if (CollectionUtils.isNotEmpty(list)) {
          Integer flag = 0;
          for (V1DeploymentCondition v1DeploymentCondition : list) {
            if ("true".equalsIgnoreCase(v1DeploymentCondition.getStatus())) {
              flag++;
            }
          }
          if (flag == list.size()) {
            this.running++;
          } else {
            this.failed++;
          }
        }
      });
    }
  }
  
  public Status(V1DeploymentList v1DeploymentList) {
    if (CollectionUtils.isNotEmpty(v1DeploymentList.getItems())) {
      v1DeploymentList.getItems().forEach(v1Deployment -> {
        List<V1DeploymentCondition> list = v1Deployment.getStatus().getConditions();
        if (CollectionUtils.isNotEmpty(list)) {
          Integer flag = 0;
          for (V1DeploymentCondition v1DeploymentCondition : list) {
            if ("true".equalsIgnoreCase(v1DeploymentCondition.getStatus())) {
              flag++;
            }
          }
          if (flag == list.size()) {
            this.running++;
          } else {
            this.failed++;
          }
        }
      });
    }
  }
  
  public Status(List<V1Pod> v1PodList) {
    
    if (CollectionUtils.isNotEmpty(v1PodList)) {
      for (V1Pod v1Pod : v1PodList) {
        add(v1Pod.getStatus());
        
      }
    }
  }
  
  private void add(V1PodStatus v1PodStatus) {
    String phase = v1PodStatus.getPhase();
   
    if (K8sStatus.PodPhase.PENDING.getName().equals(phase)) {
      this.pending++;
      return;
    }
    
    if (K8sStatus.PodPhase.RUNNING.getName().equals(phase)) {
      List<V1ContainerStatus> containerStatuses =v1PodStatus.getContainerStatuses();
      Integer ready= 0;
      if(containerStatuses!=null&&containerStatuses.size()>0){
        for(V1ContainerStatus v1ContainerStatus: containerStatuses){
            if(v1ContainerStatus.getReady()!=null&&v1ContainerStatus.getReady()==true){
              ready++;
            }
        }
        if(ready==containerStatuses.size()){
          this.running++;
          return;
        }
      }else{
        this.running++;
        return;
      }
      
    }
    if (K8sStatus.PodPhase.FAILED.getName().equals(phase)) {
      this.failed++;
      return;
    }
    if (K8sStatus.PodPhase.SUCCEEDED.getName().equals(phase)) {
      this.succeeded++;
      return;
    }
    this.failed++;
  }
  
  public Status(V1PodList v1PodList) {
    this(v1PodList.getItems());
  }
  
}
