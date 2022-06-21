package com.pubinfo.resource.model.bo.namespace;

import com.alibaba.fastjson.annotation.JSONField;
import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.openapi.models.V1Event;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.Date;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)

@ApiModel(value = "事件")
public class Event extends Base {
  
  private static final long serialVersionUID = -4914500121993031567L;
  @ApiModelProperty(value = "来源")
  private String component = null;
  @ApiModelProperty(value = "ip")
  private String host = null;
  @ApiModelProperty(value = "原因")
  private String reason;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "最早出现时间")
  private Date firstSeen = null;
  
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  @ApiModelProperty(value = "最近出现时间")
  private Date lastSeen;
  @ApiModelProperty(value = "事件类型")
  private String type;
  @ApiModelProperty(value = "消息")
  private String message;
  @ApiModelProperty(value = "子对象")
  private String object;
  @ApiModelProperty(value = "总数")
  private Integer count;
  
  public Event(V1Event v1Event) {
    initBase(v1Event.getKind(), v1Event, V1Event::getMetadata);
    this.component = v1Event.getSource().getComponent();
    this.host = v1Event.getSource().getHost();
    this.reason = v1Event.getReason();
    this.type = v1Event.getType();
    if (v1Event.getFirstTimestamp() != null)
      this.firstSeen = new Date(v1Event.getFirstTimestamp().getMillis());
    
    if (v1Event.getLastTimestamp() != null)
      this.lastSeen = new Date(v1Event.getLastTimestamp().getMillis());
    this.message = v1Event.getMessage();
    this.object = v1Event.getInvolvedObject().getFieldPath();
    this.count = v1Event.getCount();
  }
  
}

