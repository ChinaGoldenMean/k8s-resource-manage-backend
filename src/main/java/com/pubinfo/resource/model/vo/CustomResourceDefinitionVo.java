package com.pubinfo.resource.model.vo;

import com.pubinfo.resource.model.bo.cluster.CustomResourceColumnDefinition;
import com.pubinfo.resource.model.bo.cluster.CustomResourceDefinition;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@Data
@Getter
@Setter
@NoArgsConstructor(force=true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "自定义资源结果")
public class CustomResourceDefinitionVo {
  List<CustomResourceDefinition> customResourceDefinitionList = null;
  List<CustomResourceColumnDefinition> columnDefinitions = new ArrayList<>();
  
}
