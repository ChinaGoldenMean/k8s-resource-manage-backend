package com.pubinfo.resource.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.NoArgsConstructor;

//@Data
@Getter
@Setter
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "容器使用情况")
public class UsageContainer {
  String cpu = null;
  String memory = null;
  
  public UsageContainer(JSONObject jsonObject) {
    if (jsonObject != null) {
      JSONArray itemArray = jsonObject.getJSONArray("items");
      if (itemArray != null && !itemArray.isEmpty()) {
        for (int i = 0; i < itemArray.size(); i++) {
          JSONObject jo = itemArray.getJSONObject(i);
          JSONArray containerArray = jsonObject.getJSONArray("containers");
          if (containerArray != null && !containerArray.isEmpty()) {
            for (int j = 0; j < containerArray.size(); j++) {
              JSONObject container = containerArray.getJSONObject(j);
              
            }
          }
        }
      }
    }
  }
  
}
