package com.pubinfo.resource.model.bo.namespace;

import com.alibaba.fastjson.annotation.JSONField;
import com.pubinfo.resource.model.bo.Base;
import com.pubinfo.resource.model.vo.ActiveJobVo;
import io.kubernetes.client.openapi.models.V1beta1CronJob;
import io.kubernetes.client.openapi.models.V1beta1CronJobStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "定时任务")
public class CronJob extends Base {
  @ApiModelProperty(value = "计划")
  private String schedule;
  @ApiModelProperty(value = "是否延迟")
  private Boolean suspend;
  @ApiModelProperty(value = "活动个数")
  private Integer active = 0;
  
  @ApiModelProperty(value = "最后调度时间")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastSchedule;
  @ApiModelProperty(value = "警告策略")
  private String concurrencyPolicy;
  @ApiModelProperty(value = "访问url")
  private String selfLink;
  
  @ApiModelProperty(value = "启动期限秒数")
  private Long startingDeadlineSeconds = null;
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = new ArrayList<>();
  @ApiModelProperty(value = "任务集合")
  private ActiveJobVo activeJobs;
  
  public CronJob initCronJob(V1beta1CronJob v1beta1CronJob) {
    initBase(v1beta1CronJob.getKind(), v1beta1CronJob, V1beta1CronJob::getMetadata);
    this.schedule = v1beta1CronJob.getSpec().getSchedule();
    this.suspend = v1beta1CronJob.getSpec().getSuspend();
    V1beta1CronJobStatus status = v1beta1CronJob.getStatus();
    if (CollectionUtils.isNotEmpty(status.getActive())) {
      this.active = status.getActive().size();
    }
    this.selfLink = v1beta1CronJob.getMetadata().getSelfLink();
    if (status.getLastScheduleTime() != null) {
      this.lastSchedule = new Date(status.getLastScheduleTime().getMillis());
    }
    
    return this;
  }
  
  public CronJob(V1beta1CronJob v1beta1CronJob, List<Job> jobList, List<Event> eventList) {
    initCronJob(v1beta1CronJob);
    this.eventList = eventList;
    this.concurrencyPolicy = v1beta1CronJob.getSpec().getConcurrencyPolicy();
    this.startingDeadlineSeconds = v1beta1CronJob.getSpec().getStartingDeadlineSeconds();
    this.activeJobs = new ActiveJobVo(jobList);
  }
}
