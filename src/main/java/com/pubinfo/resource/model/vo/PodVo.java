package com.pubinfo.resource.model.vo;

import com.pubinfo.resource.model.bo.namespace.Pod;
import com.pubinfo.resource.model.bo.namespace.Status;
import io.kubernetes.client.openapi.models.V1Pod;
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
@ApiModel(value = "容器组列表集合")
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class PodVo implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  
  private AllocatedResourcesVo allocatedResources;
  //@SerializedName("cumulativeMetrics")
  // private String[] cumulativeMetrics;
  
  @ApiModelProperty(value = "容器组集合")
  private List<Pod> pods = new ArrayList<>();
  
  private Status status;
  
  public PodVo(AllocatedResourcesVo allocatedResources, List<Pod> pods, List<V1Pod> v1PodList) {
    this.allocatedResources = allocatedResources;
    this.pods = pods;
    this.status = new Status(v1PodList);
  }
  
}
