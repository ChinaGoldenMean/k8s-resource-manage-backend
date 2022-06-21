package com.pubinfo.resource.model.bo.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.openapi.models.V1beta1CustomResourceColumnDefinition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "打印列")
public class CustomResourceColumnDefinition implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "字段")
  private String jsONPath;
  
  @ApiModelProperty(value = "描述")
  private String description;
  @ApiModelProperty(value = "列名称")
  private String name;
  @ApiModelProperty(value = "类型")
  private String type;
  
  public CustomResourceColumnDefinition(V1beta1CustomResourceColumnDefinition v1beta1CustomResourceColumnDefinition) {
    this.jsONPath = v1beta1CustomResourceColumnDefinition.getJsONPath();
    this.description = v1beta1CustomResourceColumnDefinition.getDescription();
    this.name = v1beta1CustomResourceColumnDefinition.getName();
    this.type = v1beta1CustomResourceColumnDefinition.getType();
  }
  
}