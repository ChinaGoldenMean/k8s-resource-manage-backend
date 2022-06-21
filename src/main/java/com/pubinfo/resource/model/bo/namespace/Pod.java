package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Base;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.Condition;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodStatus;
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
@ApiModel(value = "容器组")
public class Pod extends Base {
  
  private static final long serialVersionUID = -7668494006524928872L;
  
  private PodStatus podStatus;
  @ApiModelProperty(value = "重启次数")
  private int restartCount = 0;
  @ApiModelProperty(value = "警告事件集合")
  private List<Event> warnings = new ArrayList<>();
  @ApiModelProperty(value = "节点名称")
  private String nodeName;
  @ApiModelProperty(value = "容器集合")
  private List<Container> containers = new ArrayList<>();
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = null;//需要手动
  
  @ApiModelProperty(value = "创建者")
  private Builder controller = null;
  @ApiModelProperty(value = "现状集合")
  private List<Condition> conditions = null;
  
  public Pod initPod(V1Pod v1Pod) {
    initBase(v1Pod.getKind(), v1Pod, V1Pod::getMetadata);
    this.nodeName = v1Pod.getSpec().getNodeName();
    V1PodStatus v1Status = v1Pod.getStatus();
    if (v1Status != null && v1Status.getContainerStatuses() != null) {
      v1Status.getContainerStatuses().forEach(s -> {
        restartCount = +s.getRestartCount();
      });
    }
    this.podStatus = new PodStatus(v1Pod.getStatus());
    List<V1Container> v1Containers = v1Pod.getSpec().getContainers();
    if (CollectionUtils.isNotEmpty(v1Containers)) {
      v1Containers.stream().forEach(v1Container -> {
        Container container = new Container(v1Container);
        containers.add(container);
      });
    }
    return this;
  }
  
  public Pod(V1Pod v1Pod) {
    
    initPod(v1Pod);
    this.conditions = new Condition(v1Pod.getStatus().getConditions()).getConditions();
    
  }
  
  //@Data
@Getter
@Setter
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @NoArgsConstructor
  @ApiModel(value = "容器组状态")
  public static class PodStatus implements Serializable {
    private static final long serialVersionUID = 3886609240081575338L;
    @ApiModelProperty(value = "运行状态")
    private String status = "Failed";
    @ApiModelProperty(value = "容器状态")
    private String podPhase;
    @ApiModelProperty(value = "IP")
    private String podIP;
    @ApiModelProperty(value = "QoS 等级")
    private String qosClass = null;
    
    //  @ApiModelProperty(value = "")
    //  List<V1ContainerState> containerStates = new ArrayList<>();
    
    public PodStatus(V1PodStatus v1PodStatus) {
      this.podPhase = v1PodStatus.getPhase();
      this.podIP = v1PodStatus.getPodIP();
      this.qosClass = v1PodStatus.getQosClass();
      List<V1ContainerStatus> statusList = v1PodStatus.getContainerStatuses();
      if (CollectionUtils.isNotEmpty(statusList)) {
        statusList.forEach(cs -> {
          if (cs.getState().getRunning() != null) {
            this.status = v1PodStatus.getPhase();
          }
          // containerStates.add(cs.getState());
          
        });
      }
      
    }
    
  }
  
}
