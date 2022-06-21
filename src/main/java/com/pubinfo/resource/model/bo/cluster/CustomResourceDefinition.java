package com.pubinfo.resource.model.bo.cluster;

import com.pubinfo.resource.model.bo.Base;
import com.google.gson.Gson;
import io.kubernetes.client.openapi.models.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "自定义资源")
public class CustomResourceDefinition extends Base {
  private static final long serialVersionUID = 3886609240081575338L;
  
  private   List<CustomResourceColumnDefinition> additionalPrinterColumns = new ArrayList<>();
  
  @ApiModelProperty(value = "组名称")
  private String group;
  @ApiModelProperty(value = "简称")
  private List<String> shortNames = new ArrayList<>();
  @ApiModelProperty(value = "作用域")
  private String scope;
  @ApiModelProperty(value = "复数名称")
  private String plural;
  @ApiModelProperty(value = "别名")
  private String singular;
  @ApiModelProperty(value = "类型")
  private String kind;
  @ApiModelProperty(value = "校验规则")
  private String validation;
  @ApiModelProperty(value = "自定义资源名称")
  private V1beta1CustomResourceDefinitionNames names;
  @ApiModelProperty(value = "容器状态")
  private List<V1beta1CustomResourceDefinitionCondition> conditions = null;
  @ApiModelProperty(value = "版本")
  private String version;
  
  public CustomResourceDefinition(V1beta1CustomResourceDefinition v1beta1CustomResourceDefinition) {
    initBase(v1beta1CustomResourceDefinition.getKind(), v1beta1CustomResourceDefinition, V1beta1CustomResourceDefinition::getMetadata);
    V1beta1CustomResourceDefinitionSpec spec =v1beta1CustomResourceDefinition.getSpec();
    this.group = spec.getGroup();
    this.shortNames = spec.getNames().getShortNames();
  
    this.kind = v1beta1CustomResourceDefinition.getKind();
    this.scope = v1beta1CustomResourceDefinition.getSpec().getScope();
  }
  
  public CustomResourceDefinition initCustomResourceColumnDefinition(V1beta1CustomResourceDefinition v1beta1CustomResourceDefinition) {
    V1beta1CustomResourceDefinitionNames names = v1beta1CustomResourceDefinition.getSpec().getNames();
    this.plural = names.getPlural();
    this.singular = names.getSingular();
  
    V1beta1CustomResourceDefinitionSpec spec =v1beta1CustomResourceDefinition.getSpec();
    this.version = spec.getVersion();
        List<V1beta1CustomResourceColumnDefinition> additionalPrinterColumnsPart = spec.getAdditionalPrinterColumns();
    if (additionalPrinterColumnsPart != null && additionalPrinterColumnsPart.size() > 0) {
      //columnDefinitions =
      additionalPrinterColumnsPart.stream().forEach(columnDefinitions -> {
        CustomResourceColumnDefinition columnDefinition = new CustomResourceColumnDefinition(columnDefinitions);
        if (!this.additionalPrinterColumns.contains(columnDefinition)) {
          this.additionalPrinterColumns.add(columnDefinition);
        }
      });
    }
    V1beta1CustomResourceValidation v1beta1CustomResourceValidation = spec.getValidation();
    if(v1beta1CustomResourceValidation!=null){
      this.validation = new Gson().toJson(v1beta1CustomResourceValidation);
    }
    if(spec.getNames()!=null){
      this.names = spec.getNames();
    }
    V1beta1CustomResourceDefinitionStatus status = v1beta1CustomResourceDefinition.getStatus();
    if(status!=null){
      this.conditions=status.getConditions();
    }
    return this;
    
  }
  
}
