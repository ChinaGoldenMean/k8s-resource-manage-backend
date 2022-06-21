package com.pubinfo.resource.common.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "返回")
public class JsonResult<T> implements Serializable {
  private static final long serialVersionUID = -4138033536625725437L;
  /**
   * 状态码
   */
  @ApiModelProperty(value = "状态码")
  private long status = 1;//1 表示ok,0表示error
  /**
   * 状态码对应的消息
   */
  @ApiModelProperty(value = "消息")
  private String message = "ok";
  /**
   * 页面上要具体呈现的数据
   */
  @ApiModelProperty(value = "返回数据")
  private T result;
  
  public JsonResult(long status, String message, T result) {
    this.status = status;
    this.message = message;
    this.result = result;
  }
  
  public JsonResult() {
  }
  
  
  public JsonResult(long status, String message) {
    this.status = status;
    this.message = message;
  }
  
  public static <T> JsonResult<List<T>> page(IPage page) {
    return success(page.getRecords());
  }
  
  public JsonResult(Throwable e) {
    this.status = 0;
    this.message = e.getMessage();
  }
  
  public JsonResult(ResultCode resultCode, String message, T result) {
    this(resultCode.getCode(), message, result);
  }
  
  public JsonResult(String message) {
    this.message = message;
  }
  
  public static JsonResult error(ResultCode resultCode, String message) {
    return new JsonResult(resultCode.getCode(), message);
  }
  
  public static JsonResult error(ResultCode resultCode) {
    return new JsonResult(resultCode.getCode(), resultCode.getMessage());
  }
  
  public static JsonResult error(long status, String message) {
    return new JsonResult(status, message);
  }
  
  public static <T> JsonResult<T> success(T result) {
    
    return new JsonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), result);
    
  }
  
}
