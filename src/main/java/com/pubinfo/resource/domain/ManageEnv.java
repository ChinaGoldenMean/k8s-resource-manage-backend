package com.pubinfo.resource.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.pubinfo.resource.model.dto.ManageEnvParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author ctsi-biyi-generator
 */

@Getter
@Setter
@ApiModel(value = "环境信息")
@NoArgsConstructor
@Table(name = "manage_env")
public class ManageEnv implements Serializable {
  
  private static final long serialVersionUID = -1167996532264948835L;
  @ApiModelProperty(value = "环境id")
  @Id
  @GeneratedValue
  private Integer id;
  
  @ApiModelProperty(value = "环境名称")
  private String envName;
  
  @ApiModelProperty(value = "是否是生产环境")
  private Integer isProd;
  
  @ApiModelProperty(value = "创建时间")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private transient ZonedDateTime createTime;
  
  @ApiModelProperty(value = "修改时间")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private transient ZonedDateTime updateTime;
  
  @ApiModelProperty(value = "k8s配置")
  private String k8sConfig;
  
  public ManageEnv(ManageEnvParam manageEnvParam) {
    
    this.createTime = ZonedDateTime.now();
    this.updateTime = ZonedDateTime.now();
    this.envName = manageEnvParam.getEnvName();
    this.isProd = manageEnvParam.getIsProd();
    this.k8sConfig = manageEnvParam.getK8sConfig();
  }
}