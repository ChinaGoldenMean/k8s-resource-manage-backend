package com.pubinfo.resource.model.dto;

import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(force=true)
//@Data
@Getter
@Setter
public class NodeDTO implements Serializable {
  private static final long serialVersionUID = 3886609240081575338L;
  private String nodeName;
  private Map<String, String> labels = new HashMap<>();
  private String[] deleteLabels;
  
  public NodeDTO(String nodeName, Map<String, String> labels) {
    this.nodeName = nodeName;
    this.labels = labels;
  }
  
  public NodeDTO(String nodeName, String[] deleteLabels) {
    this.nodeName = nodeName;
    this.deleteLabels = deleteLabels;
  }
}
