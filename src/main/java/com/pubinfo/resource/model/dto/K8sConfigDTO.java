package com.pubinfo.resource.model.dto;

import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//@Data
@Getter
@Setter
@NoArgsConstructor(force=true)
public class K8sConfigDTO implements Serializable {
  
  private static final long serialVersionUID = 9163808214645146335L;
  
  private String serverUrl;
  
  private String certificateAuthorityData;
  
  private String user;
  
  private String clientCertificateData;
  
  private String clientKeyData;
  
}
