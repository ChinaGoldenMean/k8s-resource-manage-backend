package com.pubinfo.resource.model.bo;

import com.alibaba.fastjson.annotation.JSONField;
import io.kubernetes.client.openapi.models.V1NodeCondition;
import io.kubernetes.client.openapi.models.V1PodCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@ApiModel(value = "现状")
//@Data
@Getter
@Setter
@NoArgsConstructor
public class Condition implements Serializable {
  private static final long serialVersionUID = 7533263759635322150L;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "最近心跳")
  private Date lastProbeTime;
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "最近更改")
  private Date lastTransitionTime;
  @ApiModelProperty(value = "消息")
  private String message;
  @ApiModelProperty(value = "原因")
  private String reason;
  @ApiModelProperty(value = "状态")
  private String status;
  @ApiModelProperty(value = "类型")
  private String type;
  private List<Condition> conditions = new ArrayList<>();
  
  public Condition(String message, String reason, String status, String type, DateTime lastTransitionDateTime, DateTime lastProbeDateTime) {
    this.message = message;
    this.reason = reason;
    this.status = status;
    this.type = type;
    //如果为空,则设置为null
    Date lastProbeTimePart = null;
    Date lastTransitionTimePart = null;
    if (lastProbeDateTime != null) {
      lastProbeTimePart = new Date(lastProbeDateTime.getMillis());
    }
    if (lastTransitionDateTime != null) {
      lastTransitionTimePart = new Date(lastTransitionDateTime.getMillis());
    }
    this.lastTransitionTime = lastTransitionTimePart;
    this.lastProbeTime = lastProbeTimePart;
  }
  
  public Condition(V1PodCondition v1Pod) {
    this(v1Pod.getMessage(), v1Pod.getReason(), v1Pod.getStatus(), v1Pod.getType(), v1Pod.getLastTransitionTime(), v1Pod.getLastProbeTime());
  }
  
  public Condition(V1NodeCondition v1Node) {
    this(v1Node.getMessage(), v1Node.getReason(), v1Node.getStatus(), v1Node.getType(), v1Node.getLastTransitionTime(), v1Node.getLastHeartbeatTime());
  }
  
  public Condition(Collection<V1NodeCondition> v1NodeConditionList) {
    if (CollectionUtils.isNotEmpty(v1NodeConditionList)) {
      v1NodeConditionList.stream().forEach(v1NodeCondition -> {
        Condition nodeCondition = new Condition(v1NodeCondition);
        this.conditions.add(nodeCondition);
      });
    }
    
  }
  
  public Condition(List<V1PodCondition> v1PodConditionList) {
    if (v1PodConditionList != null && v1PodConditionList.size() > 0) {
      conditions = new ArrayList<>();
      v1PodConditionList.stream().forEach(podCondition -> {
        Condition condition = new Condition(podCondition);
        this.conditions.add(condition);
      });
    }
  }
}
