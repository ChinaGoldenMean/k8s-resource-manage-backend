package com.pubinfo.resource.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.pubinfo.resource.model.constant.K8sObject;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

@Slf4j
//@Data
@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "K8sYamlDTO", description = "更新yaml参数")
public class K8sYamlDTO implements Serializable {
  
  private static final long serialVersionUID = -7770484662988510394L;
  
  @ApiModelProperty(value = "命名空间")
  private String namespace = "default";
  @ApiModelProperty(value = "资源类型", required = true)
  private String kind;
  @ApiModelProperty(value = "版本", required = true)
  private String apiVersion;
  @ApiModelProperty(value = "资源名称")
  private String metadataName;
  @ApiModelProperty(value = "标签")
  private Map<String, String> labelMap;
  @ApiModelProperty(value = "对象")
  @JSONField(serialize = false)
  private transient Object o;
  private String jsonStr;
  
  private String selfLink;
  
  public K8sYamlDTO(String namespace, String apiVersion, String kind, String metadataName, Map<String, String> labelMap, String selfLink) {
    this.namespace = namespace;
    this.apiVersion = apiVersion;
    this.kind = kind;
    this.metadataName = metadataName;
    this.labelMap = labelMap;
    this.selfLink = selfLink;
  }
  
  public K8sYamlDTO(Object obj) {
    
    if (obj != null) {
      Field[] fields = obj.getClass().getDeclaredFields();
      try {
        
        log.info("对象解析中:{}" + obj.toString());
        for (Field field : fields) {
          field.setAccessible(true);
          String name = field.getName();
          log.info("对象属性名称:{}", name);
          if (K8sObject.API_VERSION.equals(name)) {
            this.apiVersion = (String) field.get(obj);
          }
          if (K8sObject.KIND.equals(name)) {
            this.kind = (String) field.get(obj);
          }
          if (K8sObject.METADATA.equals(name)) {
            V1ObjectMeta objMeta = (V1ObjectMeta) field.get(obj);
            setMeta(objMeta);
          }
          
        }
        this.o = obj;
        log.info("对象解析后:{}" + this.toString());
      } catch (IllegalAccessException e) {
        log.error("解析yaml失败!", e);
      }
    }
  }
  
  public K8sYamlDTO(JSONObject obj) {
    
    if (obj != null) {
      
      log.info("对象解析中:{}" + obj.toString());
      
      for (String name : obj.keySet()) {
        
        log.info("对象属性名称:{}", name);
        if (K8sObject.API_VERSION.equals(name)) {
          this.apiVersion = obj.getString(name);
        }
        if (K8sObject.KIND.equals(name)) {
          this.kind = obj.getString(name);
        }
        if (K8sObject.METADATA.equals(name)) {
          V1ObjectMeta objMeta = obj.getObject(name, V1ObjectMeta.class);
          setMeta(objMeta);
        }
      }
      
      this.o = obj;
      log.info("对象解析后:{}" + this.toString());
      
    }
  }
  
  private void setMeta(V1ObjectMeta objMeta) {
    this.metadataName = objMeta.getName();
    this.labelMap = objMeta.getLabels();
    this.namespace = !StringUtils.isEmpty(objMeta.getNamespace())
        ? objMeta.getNamespace()
        : K8sObject.NAMESPACE;
    
  }
}
