package com.pubinfo.resource.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

//@Data
@Getter
@Setter
@NoArgsConstructor(force=true)
@ApiModel(value = "K8sCreateDTO", description = "创建yaml参数")
public class K8sCreateDTO {
  @ApiModelProperty(value = "yamlOrJson字符串,保留换行符", required = true)
  String yamlJson;
}
