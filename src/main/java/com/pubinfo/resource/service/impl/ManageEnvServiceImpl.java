package com.pubinfo.resource.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sManagement;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.config.ApplicationConfig;
import com.pubinfo.resource.domain.ManageEnv;
import com.pubinfo.resource.model.dto.ManageEnvParam;
import com.pubinfo.resource.repository.ManageEnvRepository;
import com.pubinfo.resource.service.ManageEnvService;
import com.pubinfo.resource.model.constant.K8sParam;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service Implementation for managing ManageEnv.
 *
 * @author ctsi-biyi-generator
 */
@Service
@Slf4j
public class ManageEnvServiceImpl
    extends ServiceImpl<ManageEnvRepository, ManageEnv>
    implements ManageEnvService {
  
  @Autowired
  ManageEnvRepository envMapper;
  @Autowired
  ApplicationConfig applicationConfig;
  
  @Override
  public Boolean check(Integer id) {
    
    ManageEnv manageEnv = getById(id);
    if (manageEnv == null) {
      throw new ServiceException(ResultCode.ENV_NOT_EXIST);
    }
    String k8sConfig = manageEnv.getK8sConfig();
    return checkK8sConfig(k8sConfig);
  }
  
  @Override
  public Boolean checkK8sConfig(String k8sConfig) {
    if (k8sConfig == null) {
      throw new ServiceException(ResultCode.NOT_K8S_CONFIG);
    }
    K8sManagement k8sManagement = new K8sManagement();
    CoreV1Api coreV1Api = k8sManagement.getCoreV1Api(k8sConfig, applicationConfig.getDebugger());
    if (coreV1Api != null) {
      List<V1Node> items = null;
      try {
        items = coreV1Api.listNode(K8sParam.ListParam.pretty, K8sParam.ListParam.allowWatchBookmarks, K8sParam.ListParam._continue, K8sParam.ListParam.fieldSelector, K8sParam.ListParam.labelSelector, K8sParam.ListParam.limit, K8sParam.ListParam.resourceVersion, K8sParam.ListParam.timeoutSeconds, K8sParam.ListParam.watch).getItems();
      } catch (ApiException e) {
      
        throw new ServiceException(ResultCode.GET_NODE_ERROR, K8sUtils.getMessage(e));
      }
      if (items == null || items.size() == 0){
      
        throw new ServiceException(ResultCode.K8S_QUERY_ERROR);
      }
    } else {
      throw new ServiceException(ResultCode.K8S_CONNECT_ERROR);
    }
    return true;
  }
  
  @Override
  public Boolean createManageEnv(ManageEnvParam manageEnvParam) {
    String config = manageEnvParam.getK8sConfig();
    //是否能够新增
    Boolean manyEnv = applicationConfig.getManyEnv();
    if (!manyEnv) {
      int count = envMapper.selectCount(null);
      if (count >= 1) {
        throw new ServiceException(ResultCode.MANY_ENV_FAIL);
      }
    }
    int envCount = new LambdaQueryChainWrapper<>(envMapper).eq(ManageEnv::getEnvName,manageEnvParam.getEnvName()).count();
    if (StringUtils.isBlank(config) || "{}".equals(config)) {
      throw new ServiceException(ResultCode.DATA_NOT_REQUEST);
    }
    //保存前校验文件的正确性
    
    if (envCount > 0) {
      throw new ServiceException(ResultCode.NOT_ENVNAME_REPEATED);
    }
    K8sManagement k8sManagement = new K8sManagement();
    
    CoreV1Api coreV1Api = k8sManagement.getCoreV1Api(config, applicationConfig.getDebugger());
    if (coreV1Api == null) {
      throw new ServiceException(ResultCode.K8S_CONNECT_ERROR);
    }
    ManageEnv manageEnv = new ManageEnv(manageEnvParam);
    return save(manageEnv);
    
  }
}
