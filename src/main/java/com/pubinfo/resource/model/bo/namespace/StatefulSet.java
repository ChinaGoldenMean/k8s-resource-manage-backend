package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Builder;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.openapi.models.V1StatefulSetSpec;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "有状态副本集")
public class StatefulSet extends Builder {
  
  private static final long serialVersionUID = 3886609240081575338L;
  
  @ApiModelProperty(value = "容器组集合")
  private List<Pod> podList = new ArrayList<>();
  
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = new ArrayList<>();
  
  public StatefulSet initStatefulSet(V1StatefulSet v1StatefulSet, V1PodList v1PodList, Set<Event> eventList) {
    initBase(v1StatefulSet.getKind(), v1StatefulSet, V1StatefulSet::getMetadata);
    V1StatefulSetSpec spec = v1StatefulSet.getSpec();
    Integer replicas = 0;
    if (spec != null) {
      replicas = spec.getReplicas();
    }
    this.podInfo = new PodInfo(v1PodList, eventList, replicas);
    setContainer(v1StatefulSet.getSpec(), V1StatefulSetSpec::getTemplate);
    return this;
  }
  
  public StatefulSet(V1StatefulSet v1beta1ReplicaSet, V1PodList v1PodList, Set<Event> eventList, List<Event> podEventList) {
    initStatefulSet(v1beta1ReplicaSet, v1PodList, eventList);
    if (CollectionUtils.isNotEmpty(v1PodList.getItems())) {
      v1PodList.getItems().stream().forEach(v1Pod -> {
        Pod pod = new Pod().initPod(v1Pod);
        podList.add(pod);
      });
    }
    this.eventList = podEventList;
    
  }
  
}