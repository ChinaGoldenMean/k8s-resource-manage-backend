package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.Service;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;

import java.util.List;

public interface ServiceOfService {
  /**
   * 根据查询对象获取服务集合
   *
   * @param paramVo
   * @return
   */
  Page<List<Service>> listService(SearchParamDTO paramVo);
  
  /**
   * 根据元数据查询服务集合
   *
   * @param v1ObjectMeta
   * @return
   */
  List<V1Service> listServiceByLabel(V1ObjectMeta v1ObjectMeta);
  
  List<V1Service> listServiceByLabel(String nameSpace,V1LabelSelector v1LabelSelector);
  /**
   * 读取服务
   *
   * @param namespace
   * @param name
   * @return
   */
  Service readService(String namespace, String name);
}
