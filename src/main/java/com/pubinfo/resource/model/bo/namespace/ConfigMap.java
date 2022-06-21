package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.Map;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "配置字典")
public class ConfigMap extends Base {
  
  private static final long serialVersionUID = 2095644218550285412L;
  @ApiModelProperty(value = "数据")
  private Map<String, String> data = null;
  
  public ConfigMap initConfigMap(V1ConfigMap v1ConfigMap) {
    initBase(v1ConfigMap.getKind(), v1ConfigMap, V1ConfigMap::getMetadata);
    return this;
  }
  
  public ConfigMap(V1ConfigMap v1ConfigMap) {
    initConfigMap(v1ConfigMap);
    this.data = v1ConfigMap.getData();
  }
  
}
