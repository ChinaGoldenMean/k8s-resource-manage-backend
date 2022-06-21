package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Deployment;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.DeploymentVo;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.V1Deployment;

public interface DeploymentService {
  /**
   * 根据查询对象获取部署集合
   *
   * @param paramVo
   * @return
   */
  Page<DeploymentVo> listDeployment(SearchParamDTO paramVo);
  
  /**
   * 转换部署对象
   *
   * @param v1Deployment
   * @param isSearchEvent
   * @return
   */
  Deployment toDeployment(V1Deployment v1Deployment, boolean isSearchEvent);
  
  /**
   * 读取创建者
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Builder readBuilder(String nameSpace, String name);
  
  /**
   * 读取部署对象
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Deployment readDeployment(String nameSpace, String name);
}
