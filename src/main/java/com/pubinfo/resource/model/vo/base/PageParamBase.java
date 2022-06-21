package com.pubinfo.resource.model.vo.base;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
//@Data
@Getter
@Setter

@NoArgsConstructor(force=true)
@ApiModel(value = "PageParam", description = "公共分页参数")
public class PageParamBase<T> implements Serializable {
  private static final long serialVersionUID = -7770484662988510394L;
  
  /**
   * 每页显示条数，默认 10
   */
  @ApiModelProperty(value = "每页显示条数，默认 10", required = true, example = "10")
  protected Long size = 10L;

//  @ApiModelProperty(hidden = true)
//  protected Long skip = 0L;
  /**
   * 当前页
   */
  @ApiModelProperty(value = "当前页,默认 1", required = true, example = "1")
  protected Long current = 1L;
  @ApiModelProperty(value = "排序字段信息")
  protected List<OrderItem> orders = new ArrayList<>();
  
  @ApiModelProperty(hidden = true)
  public Page<T> getPage() {
    Page<T> page = new Page<>();
    page.setCurrent(this.current);
    page.setSize(this.size);
    page.setOrders(this.orders);
    return page;
  }
  
}
