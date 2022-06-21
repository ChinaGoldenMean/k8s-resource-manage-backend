package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.CronJob;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;

import java.util.List;

public interface CronJobService {
  /**
   * 根据查询对象获取定时任务集合
   *
   * @param paramVo
   * @return
   */
  Page<List<CronJob>> listCronJob(SearchParamDTO paramVo);
  
  /**
   * 读取定时任务
   *
   * @param nameSpace
   * @param name
   * @return
   */
  CronJob readCronJob(String nameSpace, String name);
  Boolean createJob(String name, String selfLink);
}
