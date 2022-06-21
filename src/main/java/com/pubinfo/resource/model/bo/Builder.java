package com.pubinfo.resource.model.bo;

import com.pubinfo.resource.model.bo.namespace.PodInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

//@NoArgsConstructor
@ApiModel()
public class Builder extends Base {
  private static final long serialVersionUID = -7462107632586801201L;
  @ApiModelProperty(value = "容器组信息")
  public PodInfo podInfo;
  
}
