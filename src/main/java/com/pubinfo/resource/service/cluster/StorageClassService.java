package com.pubinfo.resource.service.cluster;

import com.pubinfo.resource.model.bo.cluster.StorageClass;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface StorageClassService {
  /**
   * 根据查询对象获取存储类集合
   *
   * @param paramVo
   * @return
   */
  Page<List<StorageClass>> listStorageClass(SearchParamDTO paramVo);
  
  /**
   * 读取存储类
   *
   * @param storageClassName
   * @return
   */
  StorageClass readStorageClass(String storageClassName);
}
