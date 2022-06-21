package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Base;
import com.pubinfo.resource.model.vo.EndpointsVo;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "服务")
public class Service extends Base {
  private static final long serialVersionUID = -5349341030685053875L;
  @ApiModelProperty(value = "选择器")
  private Map<String, String> selector = new HashMap<>();
  
  @ApiModelProperty(value = "类型")
  private String type;
  @ApiModelProperty(value = "集群 IP")
  private String clusterIP;
  @ApiModelProperty(value = "外部端点")
  private List<String> externalEndpoints = new ArrayList<>();
  
  @ApiModelProperty(value = "容器组集合")
  private List<Pod> podList = new ArrayList<>();
  @ApiModelProperty(value = "事件集合")
  private List<Event> eventList = new ArrayList<>();
  @ApiModelProperty(value = "端口集合")
  private List<Port> ports = new ArrayList<>();
  @ApiModelProperty(value = "保持会话")
  private String sessionAffinity;
  
  @ApiModelProperty(value = "端点集合")
  private EndpointsVo endpointList;
  
  public Service(V1Service v1Service) {
    initService(v1Service);
  }
  
  public Service initService(V1Service v1Service) {
    initBase(v1Service.getKind(), v1Service, V1Service::getMetadata);
    V1ServiceSpec spec = v1Service.getSpec();
    this.selector = spec.getSelector();
    this.type = spec.getType();
    this.clusterIP = spec.getClusterIP();
    this.selector = spec.getSelector();
    this.externalEndpoints = spec.getExternalIPs();
    
    List<V1ServicePort> v1ServicePorts = spec.getPorts();
    if (CollectionUtils.isNotEmpty(v1ServicePorts)) {
      v1ServicePorts.stream().forEach(v1ServicePort -> {
        Port port = new Port(v1ServicePort);
        ports.add(port);
      });
    }
    return this;
  }
  
  public Service initService(V1Service v1Service, EndpointsVo endpointsVo) {
    initService(v1Service);
    this.endpointList = endpointsVo;
    return this;
  }
  
  public Service(V1Service v1Service, List<Event> podEventList, V1PodList v1PodList, EndpointsVo endpointsVo) {
    initService(v1Service);
    if (v1PodList != null && !v1PodList.getItems().isEmpty()) {
      v1PodList.getItems().stream().forEach(v1Pod -> {
        Pod pod = new Pod().initPod(v1Pod);
        podList.add(pod);
      });
    }
    this.eventList = podEventList;
    this.sessionAffinity = v1Service.getSpec().getSessionAffinity();
    this.endpointList = endpointsVo;
  }
  
  //@Data
@Getter
@Setter
  @NoArgsConstructor
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModel(value = "端口信息")
  public static class Port {
    @ApiModelProperty(value = "端口")
    private Integer port;
    @ApiModelProperty(value = "协议")
    private String protocol;
    @ApiModelProperty(value = "目标端口")
    private String targetPort;
    @ApiModelProperty(value = "节点端口")
    private Integer nodePort;
    
    public Port(V1ServicePort v1ServicePort) {
      this.port = v1ServicePort.getPort();
      this.protocol = v1ServicePort.getProtocol();
      this.targetPort = v1ServicePort.getTargetPort().toString();
      this.nodePort = v1ServicePort.getNodePort();
    }
  }
}
