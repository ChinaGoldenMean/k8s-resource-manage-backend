package com.pubinfo.resource.model.bo.cluster;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.V1NFSVolumeSource;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
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
@ApiModel(value = "持久化存储卷")
public class PersistentVolume extends Base {
  private static final long serialVersionUID = -4073980931575720962L;
  @ApiModelProperty(value = "总量")
  private Map<String, Quantity> capacity = null;
  @ApiModelProperty(value = "访问模式")
  private List<String> accessModes = null;
  @ApiModelProperty(value = "回收策略")
  private String reclaimPolicy = null;
  @ApiModelProperty(value = "存储类")
  private String storageClass = null;
  @ApiModelProperty(value = "状态")
  private String status = null;
  @ApiModelProperty(value = "原因")
  private String reason = null;
  
  @ApiModelProperty(value = "消息")
  private String message = null;
  @ApiModelProperty(value = "来源")
  private NFSVolumeSource nfs = null;
  
  public PersistentVolume(V1PersistentVolume v1PersistentVolume) {
    
    initBase(v1PersistentVolume.getKind(), v1PersistentVolume, V1PersistentVolume::getMetadata);
    
    this.capacity = v1PersistentVolume.getSpec().getCapacity();
    this.accessModes = v1PersistentVolume.getSpec().getAccessModes();
    this.reclaimPolicy = v1PersistentVolume.getSpec().getPersistentVolumeReclaimPolicy();
    this.storageClass = v1PersistentVolume.getSpec().getStorageClassName();
    this.status = v1PersistentVolume.getStatus().getPhase();
    this.reason = v1PersistentVolume.getStatus().getReason();
    this.message = v1PersistentVolume.getStatus().getMessage();
    
    this.nfs = new NFSVolumeSource(v1PersistentVolume.getSpec().getNfs());
  }
  
  //@Data
@Getter
@Setter
  @NoArgsConstructor
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModel(value = "来源")
  public static class NFSVolumeSource {
    @ApiModelProperty(value = "路径")
    private String path;
    @ApiModelProperty(value = "是否只读")
    private Boolean readOnly;
    @ApiModelProperty(value = "服务器")
    private String server;
    
    public NFSVolumeSource(V1NFSVolumeSource nfsVolumeSource) {
      if (nfsVolumeSource != null) {
        this.path = nfsVolumeSource.getPath();
        this.readOnly = nfsVolumeSource.getReadOnly();
        this.server = nfsVolumeSource.getServer();
      }
      
    }
  }
}
