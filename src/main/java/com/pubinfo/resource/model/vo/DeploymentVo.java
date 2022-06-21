package com.pubinfo.resource.model.vo;

import com.pubinfo.resource.model.bo.namespace.Deployment;
import com.pubinfo.resource.model.bo.namespace.Status;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@ApiModel(value = "部署列表集合")
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeploymentVo implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  
  //@SerializedName("cumulativeMetrics")
  // private String[] cumulativeMetrics;
  
  @ApiModelProperty(value = "部署集合")
  private List<Deployment> deployments = new ArrayList<>();
  
  private Status status;
  
  public DeploymentVo(List<Deployment> deployments, List<V1Deployment> v1Deployments) {
    this.deployments = deployments;
    this.status = new Status(v1Deployments);
  }
}
