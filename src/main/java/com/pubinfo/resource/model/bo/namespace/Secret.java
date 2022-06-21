package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.openapi.models.V1Secret;
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
@ApiModel(value = "保密字典")
public class Secret extends Base {
  private static final long serialVersionUID = 3367085303391201827L;
  @ApiModelProperty(value = "数据")
  private Map<String, byte[]> data = null;
  @ApiModelProperty(value = "类型")
  private String type;
  
  public Secret initSecret(V1Secret v1Secret) {
    
    initBase(v1Secret.getKind(), v1Secret, V1Secret::getMetadata);
    this.type = v1Secret.getType();
    return this;
  }
  
  public Secret(V1Secret v1Secret) {
    initSecret(v1Secret);
    this.data = v1Secret.getData();
  }
  
}
