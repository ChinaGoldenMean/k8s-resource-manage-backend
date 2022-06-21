package com.pubinfo.resource.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

//@Data
@Getter
@Setter
@NoArgsConstructor(force=true)
@ApiModel(value = "ManageEnvParam", description = "环境参数")
public class ManageEnvParam {
  @ApiModelProperty(value = "环境名称")
  private String envName;
  @ApiModelProperty(value = "是否是生产环境")
  private Integer isProd;
  @ApiModelProperty(value = "k8s配置")
  private String k8sConfig;
}
