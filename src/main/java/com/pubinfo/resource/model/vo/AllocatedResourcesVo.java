package com.pubinfo.resource.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
//@NoArgsConstructor(force=true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "分配的资源数据")
public class AllocatedResourcesVo implements Serializable {
  private static final long serialVersionUID = -1167996532264948835L;
  @ApiModelProperty(value = "cpu限制值")
  private BigDecimal cpuLimits = new BigDecimal("0");
  @ApiModelProperty(value = "cpu请求值")
  private BigDecimal cpuRequests = new BigDecimal("0");
  @ApiModelProperty(value = "内存限制值")
  private BigDecimal memoryLimits = new BigDecimal("0");
  @ApiModelProperty(value = "内存请求值")
  private BigDecimal memoryRequests = new BigDecimal("0");
  
  private void add(List<V1Container> containersList) {
    if (containersList != null && !containersList.isEmpty()) {
      for (V1Container container : containersList) {
        V1ResourceRequirements resourceRequirements = container.getResources();
        Map<String, Quantity> limits = resourceRequirements.getLimits();
        Map<String, Quantity> requests = resourceRequirements.getRequests();
        if (limits != null && !limits.isEmpty()) {
          Quantity cpu = limits.get("cpu");
          Quantity memory = limits.get("memory");
          if (cpu != null) {
            cpuLimits = cpuLimits.add(cpu.getNumber());
          }
          if (memory != null) {
            memoryLimits = memoryLimits.add(memory.getNumber());
          }
        }
        if (requests != null && !requests.isEmpty()) {
          Quantity cpu = requests.get("cpu");
          Quantity memory = requests.get("memory");
          if (cpu != null) {
            cpuRequests = cpuRequests.add(cpu.getNumber());
          }
          if (memory != null) {
            memoryRequests = memoryRequests.add(memory.getNumber());
          }
        }
      }
      
    }
  }
  
  public AllocatedResourcesVo(List<V1Pod> podList) {
    if (podList != null && !podList.isEmpty()) {
      for (V1Pod pod : podList) {
        
        List<V1Container> containersList = pod.getSpec().getContainers();
        add(containersList);
      }
      
    }
  }
}
