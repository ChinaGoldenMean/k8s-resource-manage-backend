package com.pubinfo.resource.common.exception;

import lombok.Data;

@Data
public class K8sApiMessage {
  String kind;
  String apiVersion;
  Object metadata;
  String status;
  String message;
  String reason;
  Object details;
  Integer code;
}
