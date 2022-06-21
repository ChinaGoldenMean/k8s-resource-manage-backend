package com.pubinfo.resource.service.cluster;

import com.pubinfo.resource.model.bo.cluster.PersistentVolume;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface PersistentVolumeService {
  /**
   * 根据查询对象获取持久化数据卷
   *
   * @param paramVo
   * @return
   */
  Page<List<PersistentVolume>> listPersistentVolume(SearchParamDTO paramVo);
  
  /**
   * 读取持久化数据卷
   *
   * @param persistentVolumeName
   * @return
   */
  PersistentVolume readPersistentVolume(String persistentVolumeName);
  
}
