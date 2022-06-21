package com.pubinfo.resource.model.dto;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.util.Date;

//@Data
@Getter
@Setter

@NoArgsConstructor(force=true)
public class ProjectDTO {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * 项目ID
   */
  private Integer id;
  
  /**
   * 项目名称
   */
  private String name;
  
  /**
   * 项目描述
   */
  private String desc;
  
  /**
   * 项目创建时间
   */
  private Date begin;
  
  /**
   * 项目编码(英文名)
   */
  private String code;
  
  /**
   * pm
   */
  private String pm;
  
  public static long getSerialversionuid() {
    return serialVersionUID;
  }
  
}
