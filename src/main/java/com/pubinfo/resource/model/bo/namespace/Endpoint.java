package com.pubinfo.resource.model.bo.namespace;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.openapi.models.V1EndpointAddress;
import io.kubernetes.client.openapi.models.V1EndpointPort;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("端点")
public class Endpoint implements Serializable {
  private static final long serialVersionUID = 3886609240081575338L;
  
  @ApiModelProperty(value = "资源类型")
  private Map<String, String> typeMeta = new HashMap<>();
  
  @ApiModelProperty(value = "端口集合")
  private List<EndpointPort> ports = new ArrayList<>();
  
  @ApiModelProperty(value = "主机ip")
  private String host;
  
  @ApiModelProperty(value = "节点名称")
  private String nodeName;
  
  @ApiModelProperty(value = "是否已就绪")
  private boolean ready;
  
  public Endpoint(V1EndpointAddress v1EndpointAddress, boolean ready, List<V1EndpointPort> v1Ports) {
    this.typeMeta.put("kind", "endpoint");
    this.ready = ready;
    this.host = v1EndpointAddress.getIp();
    this.nodeName = v1EndpointAddress.getNodeName();
    if (CollectionUtils.isNotEmpty(v1Ports)) {
      v1Ports.stream().forEach(v1Port -> {
        EndpointPort endpointsPort = new EndpointPort(v1Port);
        this.ports.add(endpointsPort);
      });
    }
    
  }
  
  //@Data
@Getter
@Setter
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @NoArgsConstructor
  @ApiModel("端口")
  public static class EndpointPort implements Serializable {
    
    private static final long serialVersionUID = 947063692325366086L;
    @ApiModelProperty(value = "端口")
    private Integer port;
    @ApiModelProperty(value = "协议")
    private String protocol;
    
    public EndpointPort(V1EndpointPort v1EndpointPort) {
      this.port = v1EndpointPort.getPort();
      this.protocol = v1EndpointPort.getProtocol();
    }
  }
}
