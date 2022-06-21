package com.pubinfo.resource.model.bo.cluster;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
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
@ApiModel("节点系统信息")
public class NodeSystemInfo implements Serializable {
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "架构")
  private String architecture;
  @ApiModelProperty(value = "启动 ID")
  private String bootID;
  @ApiModelProperty(value = "容器运行时版本")
  private String containerRuntimeVersion;
  @ApiModelProperty(value = "内核版本")
  private String kernelVersion;
  @ApiModelProperty(value = "Kubelet代理版本")
  private String kubeProxyVersion;
  @ApiModelProperty(value = "Kubelet 版本")
  private String kubeletVersion;
  @ApiModelProperty(value = "机器 ID")
  private String machineID;
  @ApiModelProperty(value = "操作系统")
  private String operatingSystem;
  @ApiModelProperty(value = "操作系统镜像")
  private String osImage;
  @ApiModelProperty(value = "系统 UUID")
  private String systemUUID;
  
  public NodeSystemInfo(V1NodeSystemInfo v1NodeSystemInfo) {
    this.architecture = v1NodeSystemInfo.getArchitecture();
    this.bootID = v1NodeSystemInfo.getBootID();
    this.containerRuntimeVersion = v1NodeSystemInfo.getContainerRuntimeVersion();
    this.kernelVersion = v1NodeSystemInfo.getKernelVersion();
    this.kubeletVersion = v1NodeSystemInfo.getKubeletVersion();
    this.machineID = v1NodeSystemInfo.getMachineID();
    this.operatingSystem = v1NodeSystemInfo.getOperatingSystem();
    this.osImage = v1NodeSystemInfo.getOsImage();
    this.systemUUID = v1NodeSystemInfo.getSystemUUID();
    this.kubeProxyVersion = v1NodeSystemInfo.getKubeProxyVersion();
  }
  
}
