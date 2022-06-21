package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.vo.EndpointsVo;

public interface EndpointService {
  /**
   * 读取端点
   *
   * @param nameSpace
   * @param name
   * @return
   */
  EndpointsVo readEndpoint(String nameSpace, String name);
}
