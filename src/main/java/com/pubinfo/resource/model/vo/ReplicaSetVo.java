package com.pubinfo.resource.model.vo;

import com.pubinfo.resource.model.bo.namespace.ReplicaSet;
import com.pubinfo.resource.model.bo.namespace.Status;
import io.kubernetes.client.openapi.models.V1ReplicaSet;
import io.kubernetes.client.openapi.models.V1ReplicaSetStatus;
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
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class ReplicaSetVo implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  
  @ApiModelProperty(value = "副本集集合")
  private List<ReplicaSet> replicaSets = new ArrayList<>();
  private Status status = new Status();
  
  public ReplicaSetVo(List<ReplicaSet> replicaSets, List<V1ReplicaSet> v1ReplicaSets) {
    this.replicaSets = replicaSets;
    setStatus(v1ReplicaSets);
  }
  
  private void setStatus(List<V1ReplicaSet> v1ReplicaSets) {
    if (CollectionUtils.isNotEmpty(v1ReplicaSets)) {
      Integer running = 0;
      Integer failed = 0;
      for (V1ReplicaSet v1ReplicaSet : v1ReplicaSets) {
        
        V1ReplicaSetStatus setStatus = v1ReplicaSet.getStatus();
        if (setStatus != null) {
          Integer readyReplicas = setStatus.getReadyReplicas();
          Integer replicas = setStatus.getReplicas();
          
          if ((replicas != null&&replicas.equals(0))||(readyReplicas != null && readyReplicas.equals(replicas))){
            running++;
          } else {
            failed++;
          }
        }
        
      }
      this.status.setRunning(running);
      this.status.setFailed(failed);
    }
  }
}
