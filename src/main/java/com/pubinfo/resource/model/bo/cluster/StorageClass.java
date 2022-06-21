package com.pubinfo.resource.model.bo.cluster;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.openapi.models.V1StorageClass;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "存储类")
public class StorageClass extends Base {
  private static final long serialVersionUID = 2857195536456481290L;
  @ApiModelProperty(value = "参数map")
  private Map<String, String> parameters = null;
  @ApiModelProperty(value = "供应者")
  private String provisioner = null;
  @ApiModelProperty(value = "持久化存储卷集合")
  private List<PersistentVolume> persistentVolumeList = null;
  
  public StorageClass(V1StorageClass v1StorageClass) {
    initBase(v1StorageClass.getKind(), v1StorageClass, V1StorageClass::getMetadata);
    
    this.parameters = v1StorageClass.getParameters();
    this.provisioner = v1StorageClass.getProvisioner();
  }
  
}
