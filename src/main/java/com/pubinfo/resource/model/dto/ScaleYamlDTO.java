package com.pubinfo.resource.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

//@Data
@Getter
@Setter

@NoArgsConstructor(force=true)
@ApiModel(value = "ScaleYamlVo", description = "扩缩容参数")
public class ScaleYamlDTO extends YamlBaseDTO {
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "扩缩容后大小", required = true)
  private Integer scaleSize;
}
