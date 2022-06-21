package com.pubinfo.resource.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pubinfo.resource.domain.ManageEnv;
import com.pubinfo.resource.model.dto.ManageEnvParam;

/**
 * Service Interface for managing ManageEnv.
 *
 * @author ctsi-biyi-generator
 */

public interface ManageEnvService
    extends IService<ManageEnv> {
  Boolean check(Integer id);
  Boolean checkK8sConfig(String k8sConfig);
  Boolean createManageEnv(ManageEnvParam manageEnvParam);
}
