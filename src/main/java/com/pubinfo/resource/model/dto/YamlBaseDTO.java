package com.pubinfo.resource.model.dto;

import io.kubernetes.client.openapi.models.V1OwnerReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor(force=true)
@Getter
@Setter
public class YamlBaseDTO implements Serializable {
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "资源名称", required = true)
  public String name;
  @ApiModelProperty(value = "命名空间", required = false)
  public String nameSpace = "default";
  @ApiModelProperty(value = "资源类型", required = true)
  public String kind;
  
  @ApiModelProperty(value = "版本", required = false)
  private String apiVersion;
  public YamlBaseDTO(V1OwnerReference v1OwnerReference, String nameSpace) {
    this.name = v1OwnerReference.getName();
    this.nameSpace = nameSpace;
    this.kind = v1OwnerReference.getKind();
  }
}
