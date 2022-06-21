package com.pubinfo.resource.model.constant;

public enum ApiVersionEnum {
  
  EXTENSION_API_V1BETA1("apiextensions.k8s.io/v1beta1"),
  EXTENSION_API_V1("apiextensions.k8s.io/v1"),
  EXTENSION_API("extensions/v1beta1"),
  COREAPI_V1("v1"),
  APP_V1("apps/v1"),
  //APPS_V1VETA1("apps/v1beta1"),
  //APPS_V1VETA2("apps/v1beta2"), k8s1.8版本后,java api没有此对象
  BATCH_V1("batch/v1"),
  BATCH_V2ALPHA1("batch/v2alpha1"),
  BATCH_V1BETA1("batch/v1beta1"),
  RBAC_AUTHORIZATION_K8S_IO_V1("rbac.authorization.k8s.io/v1"),
  RBAC_AUTHORIZATION_K8S_IO_V1BETA1("rbac.authorization.k8s.io/v1beta1"),
  STORAGE_K8S_IO_V1("storage.k8s.io/v1"),
  STORAGE_K8S_IO_V1BETA1("storage.k8s.io/v1beta1");
  private String apiVersionType;
  
  ApiVersionEnum(String apiVersionType) {
    this.apiVersionType = apiVersionType;
  }
  
  public String getApiVersionType() {
    return apiVersionType;
  }
  
  public static ApiVersionEnum getEnumByType(String apiVersion) {
    
    for (ApiVersionEnum k8sApiversionTypeEnum : values()) {
      if (k8sApiversionTypeEnum.getApiVersionType().equals(apiVersion)) {
        return k8sApiversionTypeEnum;
      }
    }
    return null;
  }
}
