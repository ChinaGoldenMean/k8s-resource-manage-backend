package com.pubinfo.resource.model.bo.namespace;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressSpec;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressStatus;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.NoArgsConstructor;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "访问权")
public class Ingress extends Base {
  
  private static final long serialVersionUID = -4914500121293031567L;
  private ExtensionsV1beta1IngressStatus status;
  private ExtensionsV1beta1IngressSpec spec;
  
  public Ingress initIngress(ExtensionsV1beta1Ingress beta1Ingress) {
    initBase(beta1Ingress.getKind(), beta1Ingress, ExtensionsV1beta1Ingress::getMetadata);
    return this;
  }
  
  public Ingress(ExtensionsV1beta1Ingress beta1Ingress) {
    initIngress(beta1Ingress);
    this.status = beta1Ingress.getStatus();
    this.spec = beta1Ingress.getSpec();
  }
}
