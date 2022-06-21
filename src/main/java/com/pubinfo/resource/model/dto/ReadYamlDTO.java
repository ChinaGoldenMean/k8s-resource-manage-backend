package com.pubinfo.resource.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

//@Data
@Getter
@Setter

@NoArgsConstructor(force=true)
@ApiModel(value = "ReadYamlDTO", description = "发布引用参数")
public class ReadYamlDTO extends YamlBaseDTO {
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "yaml对象", required = false)
  private Object deployResource;
  
}
