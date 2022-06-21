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
import java.util.Set;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "任务")
public class Job extends Builder {
  
  private static final long serialVersionUID = -7462207632586801201L;
  @ApiModelProperty(value = "资源类型")
  private V1JobStatus status = null;
  @ApiModelProperty(value = "容器组集合")
  private List<Pod> podList = new ArrayList<>();
  @ApiModelProperty(value = "并行数")
  private Integer parallelism = null;
  @ApiModelProperty(value = "完成数")
  private Integer completions = null;
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = new ArrayList<>();
  
  public Job initJob(V1Job v1Job, V1PodList v1PodList, Set<Event> eventList) {
    initBase(v1Job.getKind(), v1Job, V1Job::getMetadata);
    setContainer(v1Job.getSpec(), V1JobSpec::getTemplate);
    this.podInfo = new PodInfo(v1PodList, eventList);
    this.parallelism = v1Job.getSpec().getParallelism();
    return this;
  }
  
  public Job(V1Job v1Job, V1PodList v1PodList,
             Set<Event> eventList, List<Event> podEventList) {
    initJob(v1Job, v1PodList, eventList);
    this.eventList = podEventList;
    this.completions = v1Job.getSpec().getCompletions();
    
    this.status = v1Job.getStatus();
    List<V1Pod> items = v1PodList.getItems();
    if (CollectionUtils.isNotEmpty(items)) {
      items.stream().forEach(v1Pod -> {
        Pod pod = new Pod().initPod(v1Pod);
        podList.add(pod);
      });
    }
    
  }
}
