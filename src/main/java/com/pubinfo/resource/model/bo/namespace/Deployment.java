package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.vo.ReplicaSetVo;
import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.models.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "部署")
public class Deployment extends Builder {
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "策略")
  private String strategy;
  @ApiModelProperty(value = "最小就绪秒数")
  private Integer minReadySeconds;
  @ApiModelProperty(value = "容器组集合")
  private List<Pod> podList = null;
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = new ArrayList<>();
  @ApiModelProperty(value = "选择器")
  private Map<String, String> selector = null;
  @ApiModelProperty(value = "滚动更新策略")
  private RollingUpdateStrategy rollingUpdateStrategy = null;
  @ApiModelProperty(value = "历史版本限制值")
  private Integer revisionHistoryLimit;
  
  @ApiModelProperty(value = "新副本集")
  ReplicaSetVo newReplicaSet = null;
  @ApiModelProperty(value = "状态")
  private StatusInfo statusInfo;
  @ApiModelProperty(value = "镜像")
  private String image;
  
  public Deployment(V1Deployment v1Deployment, V1PodList v1PodList, List<Event> podEventList, Set<Event> eventList, ReplicaSetVo replicaSetVo) {
    initDeployment(v1Deployment, v1PodList, eventList);
    rollingUpdateStrategy = new RollingUpdateStrategy(v1Deployment.getSpec().getStrategy().getRollingUpdate());
    this.selector = v1Deployment.getSpec().getSelector().getMatchLabels();
    this.eventList = podEventList;
    this.strategy = v1Deployment.getSpec().getStrategy().getType();
    this.minReadySeconds = v1Deployment.getSpec().getMinReadySeconds();
    this.revisionHistoryLimit = v1Deployment.getSpec().getRevisionHistoryLimit();
    this.statusInfo = new StatusInfo(v1Deployment.getStatus());
    
    this.podInfo = null;
    this.newReplicaSet = replicaSetVo;
    if (CollectionUtils.isNotEmpty(v1PodList.getItems())) {
      podList = new ArrayList<>();
      v1PodList.getItems().stream().forEach(v1Pod -> {
        Pod pod = new Pod().initPod(v1Pod);
        podList.add(pod);
      });
    }
  }
  
  public Deployment initDeployment(V1Deployment v1Deployment, V1PodList v1PodList, Set<Event> eventList) {
    initBase(v1Deployment.getKind(), v1Deployment, V1Deployment::getMetadata);
    V1DeploymentSpec spec = v1Deployment.getSpec();
    Integer replicas = 0;
    if (spec != null) {
      replicas = spec.getReplicas();
    }
    this.podInfo = new PodInfo(v1PodList, eventList, replicas);
    setContainer(v1Deployment.getSpec(), V1DeploymentSpec::getTemplate);
    return this;
  }
  
  //@Data
@Getter
@Setter
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @NoArgsConstructor
  @ApiModel(value = "滚动更新策略")
  public static class RollingUpdateStrategy implements Serializable {
    private static final long serialVersionUID = -8850122726908958580L;
    @ApiModelProperty(value = "最大激增数")
    private String maxSurge;
    @ApiModelProperty(value = "最大无效数")
    private String maxUnavailable;
    
    public RollingUpdateStrategy(V1RollingUpdateDeployment v1RollingUpdateDeployment) {
      if (v1RollingUpdateDeployment != null) {
        IntOrString maxSurgePart = v1RollingUpdateDeployment.getMaxSurge();
        this.maxSurge = maxSurgePart.isInteger() ? maxSurgePart.getIntValue().toString() : maxSurgePart.getStrValue();
        IntOrString unavailable = v1RollingUpdateDeployment.getMaxUnavailable();
        this.maxUnavailable = unavailable.isInteger() ? unavailable.getIntValue().toString() : unavailable.getStrValue();
      }
      
    }
  }
  
  //@Data
@Getter
@Setter
  @NoArgsConstructor
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModel(value = "状态")
  public static class StatusInfo implements Serializable {
    private static final long serialVersionUID = 1949169660072040580L;
    @ApiModelProperty(value = "共计")
    private Integer replicas;
    @ApiModelProperty(value = "已更新")
    private Integer updated;
    @ApiModelProperty(value = "可用的")
    private Integer available;
    @ApiModelProperty(value = "不可用")
    private Integer unavailable;
    
    public StatusInfo(V1DeploymentStatus v1DeploymentStatus) {
      this.available = v1DeploymentStatus.getAvailableReplicas();
      this.unavailable = v1DeploymentStatus.getUnavailableReplicas();
      this.replicas = v1DeploymentStatus.getReplicas();
      this.updated = v1DeploymentStatus.getUpdatedReplicas();
    }
    
  }
}
