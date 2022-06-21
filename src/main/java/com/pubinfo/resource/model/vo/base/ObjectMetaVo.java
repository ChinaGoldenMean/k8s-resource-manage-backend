package com.pubinfo.resource.model.vo.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

//@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force=true)
//@Data
@Getter
@Setter
public class ObjectMetaVo implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date creationTimestamp;
  private Map<String, String> labelMap;
  private String namespace;
  private Map<String, String> annotationMap;
  private String name;
  
  private String uid = null;
  
  public ObjectMetaVo(V1ObjectMeta objectMetaSource) {
    
    this.annotationMap = objectMetaSource.getAnnotations();
    this.labelMap = objectMetaSource.getLabels();
    this.creationTimestamp = new Date(objectMetaSource.getCreationTimestamp().getMillis());
    this.name = objectMetaSource.getName();
    this.namespace = objectMetaSource.getNamespace();
    this.uid = objectMetaSource.getUid();
  }
}
