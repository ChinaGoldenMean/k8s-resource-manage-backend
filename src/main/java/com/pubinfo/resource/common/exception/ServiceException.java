package com.pubinfo.resource.common.exception;

import com.pubinfo.resource.common.vo.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends RuntimeException {
  private static final long serialVersionUID = -3135239055465723987L;
  private static final URI API_URI = URI.create("problem/api");
  
  // 错误编码
  private Integer code;
  
  public ServiceException(String msg) {
    super(  msg);
  }
  
  public ServiceException(ResultCode resultCode) {
    super(resultCode.getMessage());
    this.code = resultCode.getCode();
  }
  
  public ServiceException(ResultCode resultCode, String message) {
    super( resultCode.getMessage());
    this.code = resultCode.getCode();
  }
  
  public ServiceException(Integer code, String msg) {
    super( msg);
    this.code = code;
  }
  
  public Integer getCode() {
    return code;
  }
  
}

