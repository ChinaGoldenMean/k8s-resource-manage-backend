package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Builder;
import io.kubernetes.client.openapi.models.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "副本集")
public class ReplicaSet extends Builder {
  
  private static final long serialVersionUID = 3886601240081575338L;
  @ApiModelProperty(value = "服务集合")
  private List<Service> serviceList = new ArrayList<>();
  
  @ApiModelProperty(value = "容器组集合")
  private List<Pod> podList = new ArrayList<>();
  
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = new ArrayList<>();
  
  @ApiModelProperty(value = "选择器")
  private Map<String, String> selector = null;
  
  public ReplicaSet initReplicaSet(V1ReplicaSet v1beta1ReplicaSet, V1PodList v1PodList, Set<Event> eventList) {
    initBase(v1beta1ReplicaSet.getKind(), v1beta1ReplicaSet, V1ReplicaSet::getMetadata);
    V1ReplicaSetSpec status = v1beta1ReplicaSet.getSpec();
    Integer replicas = 0;
    if (status != null) {
      replicas = status.getReplicas();
    }
    this.podInfo = new PodInfo(v1PodList, eventList, replicas);
    setContainer(v1beta1ReplicaSet.getSpec(), V1ReplicaSetSpec::getTemplate);
    return this;
  }
  
  public ReplicaSet(V1ReplicaSet v1beta1ReplicaSet, V1PodList v1PodList, Set<Event> eventList, List<Event> podEventList, List<V1Service> v1ServiceList) {
    initReplicaSet(v1beta1ReplicaSet, v1PodList, eventList);
    
    if (CollectionUtils.isNotEmpty(v1PodList.getItems())) {
      v1PodList.getItems().stream().forEach(v1Pod -> {
        Pod pod = new Pod().initPod(v1Pod);
        podList.add(pod);
      });
    }
    this.eventList = podEventList;
    this.selector = v1beta1ReplicaSet.getSpec().getSelector().getMatchLabels();
    if (CollectionUtils.isNotEmpty(v1ServiceList)) {
      v1ServiceList.stream().forEach(v1Service -> {
        Service service = new Service(v1Service);
        serviceList.add(service);
      });
    }
    
  }
  
}