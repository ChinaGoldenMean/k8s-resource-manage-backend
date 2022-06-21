package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.vo.EndpointsVo;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.EndpointService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Endpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EndpointServiceImpl extends K8sSearch implements EndpointService {
  
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public EndpointsVo readEndpoint(String nameSpace, String name) {
    CoreV1Api coreV1Api = k8sService.getCoreV1Api();
    EndpointsVo endpointsVo = null;
    try {
      V1Endpoints v1Endpoints = coreV1Api.readNamespacedEndpoints(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
      endpointsVo = new EndpointsVo(v1Endpoints);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_ENDPOINTS_FAIL, K8sUtils.getMessage(e));
    }
    return endpointsVo;
  }
}
