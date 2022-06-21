package com.pubinfo.resource.model.vo;

import com.pubinfo.resource.model.bo.namespace.Endpoint;
import io.kubernetes.client.openapi.models.V1EndpointAddress;
import io.kubernetes.client.openapi.models.V1EndpointSubset;
import io.kubernetes.client.openapi.models.V1Endpoints;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Data
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EndpointsVo implements Serializable {
  private static final long serialVersionUID = 3886609240081575338L;
  List<Endpoint> endpoints = new ArrayList<>();
  Map<String, Integer> listMeta = new HashMap<>();
  
  public EndpointsVo(V1Endpoints v1Endpoints) {
    
    List<V1EndpointSubset> endpointsSubsets = v1Endpoints.getSubsets();
    if (endpointsSubsets != null && endpointsSubsets.size() > 0) {
      
      endpointsSubsets.stream().forEach(subset -> {
        List<V1EndpointAddress> addresses = subset.getAddresses();
        List<V1EndpointAddress> notReadyAddresses = subset.getNotReadyAddresses();
        if (addresses != null && addresses.size() > 0) {
          addresses.stream().forEach(adders -> {
            Endpoint endpoint = new Endpoint(adders, true, subset.getPorts());
            endpoints.add(endpoint);
          });
          
        }
        if (notReadyAddresses != null && notReadyAddresses.size() > 0) {
          notReadyAddresses.stream().forEach(notAdders -> {
            Endpoint endpoint = new Endpoint(notAdders, false, subset.getPorts());
            endpoints.add(endpoint);
          });
        }
        
      });
    }
    listMeta.put("totalItems", endpoints.size());
  }
}
