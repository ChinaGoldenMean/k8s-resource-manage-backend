package com.pubinfo.resource.model.bo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Getter
@Setter
//@NoArgsConstructor
@ApiModel()
public class Base implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "名称")
  public String meta_name;
  @ApiModelProperty(value = "镜像集合")
  private List<String> containerImages = null;
  @ApiModelProperty(value = "初始镜像集合")
  private List<String> initContainerImages = null;
  @ApiModelProperty(value = "资源类型")
  private String kindMeta;
  @ApiModelProperty(value = "创建时间")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date meta_creationTimestamp;
  @ApiModelProperty(value = "标签")
  private String meta_labelMap;
  @ApiModelProperty(value = "命名空间")
  private String meta_namespace;
  @ApiModelProperty(value = "注释")
  private String meta_annotationMap;
  
  public <T> void initBase(String kind, T t, Function<T, V1ObjectMeta> getMetadata) {
    this.kindMeta = kind;
    V1ObjectMeta objectMetaSource = getMetadata.apply(t);
    setMetaData(objectMetaSource);
  }
  
  public void setMetaData(V1ObjectMeta objectMetaSource) {
    this.meta_annotationMap = JSON.toJSONString(objectMetaSource.getAnnotations());
    this.meta_labelMap = JSON.toJSONString(objectMetaSource.getLabels());
    this.meta_creationTimestamp = new Date(objectMetaSource.getCreationTimestamp().getMillis());
    this.meta_name = objectMetaSource.getName();
    this.meta_namespace = objectMetaSource.getNamespace();
    
  }
  
  public <T> void setContainer(T t, Function<T, V1PodTemplateSpec> getSpecPodTemplate) {
    List<V1Container> containers = getSpecPodTemplate.apply(t).getSpec().getContainers();
    List<V1Container> initContainers = getSpecPodTemplate.apply(t).getSpec().getInitContainers();
    if (CollectionUtils.isNotEmpty(containers)) {
      containerImages = new ArrayList<>();
      containers.stream().forEach(container -> {
        containerImages.add(container.getImage());
      });
    }
    if (CollectionUtils.isNotEmpty(initContainers)) {
      initContainerImages = new ArrayList<>();
      containers.stream().forEach(container -> {
        initContainerImages.add(container.getImage());
      });
    }
  }
  
}
