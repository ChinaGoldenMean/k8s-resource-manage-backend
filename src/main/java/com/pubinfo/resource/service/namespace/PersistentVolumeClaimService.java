package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.PersistentVolumeClaim;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface PersistentVolumeClaimService {
  /**
   * 根据查询对象获取持久化存储声明集合
   *
   * @param paramVo
   * @return
   */
  Page<List<PersistentVolumeClaim>> listPersistentVolumeClaim(SearchParamDTO paramVo);
  
  /**
   * 读取持久化存储声明
   *
   * @param namespace
   * @param name
   * @return
   */
  PersistentVolumeClaim readPersistentVolumeClaim(String namespace, String name);
}
