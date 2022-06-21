package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
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
@ApiModel(value = "持久化数据卷声明")
public class PersistentVolumeClaim extends Base {
  
  private static final long serialVersionUID = 4915404428295154946L;
  @ApiModelProperty(value = "状态")
  private String status = null;
  @ApiModelProperty(value = "存储卷")
  private String volume = null;
  @ApiModelProperty(value = "总量")
  private Map<String, Quantity> capacity = null;
  @ApiModelProperty(value = "访问模式")
  private List<String> accessModes = null;
  @ApiModelProperty(value = "存储类")
  private String storageClass;
  
  public PersistentVolumeClaim(V1PersistentVolumeClaim v1PersistentVolumeClaim) {
    
    initBase(v1PersistentVolumeClaim.getKind(), v1PersistentVolumeClaim, V1PersistentVolumeClaim::getMetadata);
    
    this.status = v1PersistentVolumeClaim.getStatus().getPhase();
    this.volume = v1PersistentVolumeClaim.getSpec().getVolumeName();
    this.capacity = v1PersistentVolumeClaim.getStatus().getCapacity();
    this.accessModes = v1PersistentVolumeClaim.getSpec().getAccessModes();
    this.storageClass = v1PersistentVolumeClaim.getSpec().getStorageClassName();
    
  }
  
}
