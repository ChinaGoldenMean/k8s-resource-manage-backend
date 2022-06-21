package com.pubinfo.resource.model.bo.namespace;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kubernetes.client.openapi.models.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "容器")
public class Container implements Serializable {
  private static final long serialVersionUID = 552512775529119209L;
  @ApiModelProperty(value = "参数")
  private List<String> args = null;
  @ApiModelProperty(value = "命令")
  private List<String> commands = null;
  private transient List<EnvVar> env = null;
  @ApiModelProperty(value = "镜像")
  private String image = null;
  @ApiModelProperty(value = "名称")
  private String name = null;
  
  public Container(V1Container v1Container) {
    this.args = v1Container.getArgs();
    this.commands = v1Container.getCommand();
    
    this.image = v1Container.getImage();
    this.name = v1Container.getName();
    List<V1EnvVar> envVarList = v1Container.getEnv();
    if (CollectionUtils.isNotEmpty(envVarList)) {
      this.env = new ArrayList<>();
      envVarList.stream().forEach(v1EnvVar -> {
        EnvVar envVar = new EnvVar(v1EnvVar);
        this.env.add(envVar);
      });
    }
    
  }
  
  //@Data
@Getter
@Setter
  @NoArgsConstructor
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModel(value = "环境变量")
  public static class EnvVar implements Serializable {
    private static final long serialVersionUID = 552512775529119209L;
    @ApiModelProperty(value = "环境变量名称")
    private String name = null;
    @ApiModelProperty(value = "环境变量值")
    private String value = null;
    private EnvVarSource valueFrom = null;
    
    public EnvVar(V1EnvVar v1EnvVar) {
      this.name = v1EnvVar.getName();
      this.value = v1EnvVar.getValue();
      V1EnvVarSource v1EnvVarSource = v1EnvVar.getValueFrom();
      if (v1EnvVarSource != null) {
        this.valueFrom = new EnvVarSource(v1EnvVarSource);
      }
      
    }
    
    //@Data
@Getter
@Setter
    @NoArgsConstructor
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModel(value = "值来源")
    public static class EnvVarSource {
      private ObjectFieldSelector fieldRef = null;
      private ConfigMapKeySelector configMapKeyRef = null;
      
      public EnvVarSource(V1EnvVarSource v1EnvVarSource) {
        if (v1EnvVarSource.getFieldRef() != null) {
          this.fieldRef = new ObjectFieldSelector(v1EnvVarSource.getFieldRef());
        }
        if (v1EnvVarSource.getConfigMapKeyRef() != null) {
          this.configMapKeyRef = new ConfigMapKeySelector(v1EnvVarSource.getConfigMapKeyRef());
        }
        
      }
      
      //@Data
@Getter
@Setter
      @NoArgsConstructor
      //@JsonInclude(JsonInclude.Include.NON_NULL)
      @ApiModel(value = "对象所在选择器")
      public static class ObjectFieldSelector {
        @ApiModelProperty(value = "api版本")
        private String apiVersion = null;
        @ApiModelProperty(value = "字段选择器")
        private String fieldPath = null;
        
        public ObjectFieldSelector(V1ObjectFieldSelector v1ObjectFieldSelector) {
          this.apiVersion = v1ObjectFieldSelector.getApiVersion();
          this.fieldPath = v1ObjectFieldSelector.getFieldPath();
        }
      }
      
      //@Data
@Getter
@Setter
      @NoArgsConstructor
      //@JsonInclude(JsonInclude.Include.NON_NULL)
      @ApiModel(value = "保密字典所在选择器")
      public static class ConfigMapKeySelector {
        @ApiModelProperty(value = "键")
        private String key;
        @ApiModelProperty(value = "值")
        private String name;
        
        public ConfigMapKeySelector(V1ConfigMapKeySelector v1ConfigMapKeySelector) {
          this.key = v1ConfigMapKeySelector.getKey();
          this.name = v1ConfigMapKeySelector.getName();
        }
      }
      
    }
  }
}
