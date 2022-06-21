package com.pubinfo.resource.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import io.kubernetes.client.custom.V1Patch;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor(force=true)
//@Data
@Getter
@Setter
public class PatchVo implements Serializable {
  
  private static final long serialVersionUID = 5270724851663939953L;
  
  @JSONField(name = "op")
  private String op = "replace";
  
  private String path;
  
  private Object value;
  
  public PatchVo(String path, Object value) {
    this.path = path;
    this.value = value;
  }
  
  public PatchVo(String op, String path, Object value) {
    this.op = op;
    this.path = path;
    this.value = value;
  }
  
  public V1Patch getV1Patch(String path, Object value) {
    
    return new V1Patch(JSONObject.toJSONString(new PatchVo(path, value)));
  }
}