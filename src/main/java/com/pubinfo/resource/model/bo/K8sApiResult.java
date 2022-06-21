package com.pubinfo.resource.model.bo;

import lombok.*;

//@Data
@Getter
@Setter
public class K8sApiResult {
  String kind;
  String apiVersion;
  Object metadata;
  String status;
  String message;
  String reason;
  Object details;
  Integer code;
}
