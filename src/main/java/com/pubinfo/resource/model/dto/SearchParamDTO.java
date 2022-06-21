package com.pubinfo.resource.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(force=true)
//@Data
@Getter
@Setter
@ApiModel(value = "SearchParamDTO", description = "命名空间列表查询参数")
public class SearchParamDTO implements Serializable {
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "每页的数量")
  private Integer itemsPerPage = 10;
  
  @ApiModelProperty(value = "当前页")
  private Integer currentPage = 1;
  
  @ApiModelProperty(value = "关键词搜索")
  private String filterBy;
  @ApiModelProperty(value = "排序关键字 a(升序) d(降序)  name (名称) createTimeStamp (创建时间) 默认(d,createTimeStamp)")
  private String sortBy = "d,createTimeStamp";
  
  @ApiModelProperty(hidden = true)
  private int skip;
  
  public int getSkip() {
    return (this.getCurrentPage() <= 0 ? 0 : this.getCurrentPage() - 1) * this.getItemsPerPage();
  }
  
  public void validateParams() {
    String comma = ",";
    if (this.currentPage == null || this.currentPage <= 0) {
      throw new NullPointerException("当前页currentPage 参数不能为空,不能小于0");
    }
    if (this.itemsPerPage == null || this.itemsPerPage <= 0) {
      throw new NullPointerException("每页的数量itemsPerPage 参数不能为空,不能小于0");
    }
  
    String sortBy = this.sortBy;
    if (!sortBy.contains(comma)) {
      throw new NullPointerException("排序关键字sortBy 格式错误");
    }
    String[] filterBys = sortBy.split(comma);
    if (filterBys.length > 2) {
      throw new NullPointerException("排序关键字sortBy 格式错误");
    }
    String name = "name";
    String createTimeStamp = "createTimeStamp";
    if ((!"a".equals(filterBys[0]) && !"d".equals(filterBys[0])) || (!name.equals(filterBys[1]) && !createTimeStamp.equals(filterBys[1]))) {
      throw new NullPointerException("排序关键字sortBy 格式错误");
    }
    
  }
  
}
