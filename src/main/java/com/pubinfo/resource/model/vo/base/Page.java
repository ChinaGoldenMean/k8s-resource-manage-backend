package com.pubinfo.resource.model.vo.base;

import com.pubinfo.resource.model.dto.SearchParamDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

//@Data
@Getter
@Setter
@ApiModel(value = "分页数据")
public class Page<T> implements Serializable {
  
  private static final long serialVersionUID = -7770484662988510394L;
  @ApiModelProperty(value = "总页数")
  private Integer countPage; //d
  @ApiModelProperty(value = "当前页")
  private Integer currentPage;
  @ApiModelProperty(value = "总条数")
  private Integer totalItem;
  @ApiModelProperty(value = "当前页行数")
  private Integer itemsPerPage;
  //@ApiModelProperty(value = "状态")
  // private Status status = null;
  @ApiModelProperty(value = "返回数据")
  private T data;
  
  public Page(SearchParamDTO vo, T data, Integer totalItemVo, Integer itemsPerPageVo) {
    this.data = data;
    this.totalItem = totalItemVo;
    this.currentPage = vo.getCurrentPage();
    this.itemsPerPage = itemsPerPageVo;
    this.countPage = (totalItemVo + vo.getItemsPerPage() - 1)
        / vo.getItemsPerPage();
  }
  
  public Page(T data) {
    this.data = data;
  }
  
  public Page() {
  
  }
  
}
