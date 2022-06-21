package com.pubinfo.resource.common.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pubinfo.resource.common.exception.ConfigNotSupportException;
import com.pubinfo.resource.model.dto.K8sConfigDTO;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

public class MyJsonUtils {
  
  /**
   * 从k8s配置文件中提取出校验文件内容
   *
   * @param k8sConfig
   * @return
   */
  public static K8sConfigDTO parse(String k8sConfig) {
    Yaml yaml = new Yaml();
    Object load = yaml.load(k8sConfig);
    if (load != null) {
      K8sConfigDTO k8sConfigDTO = new K8sConfigDTO();
      JSONObject jsonObject = JSONUtil.parseObj(load);
      Object serverUrl = jsonObject.getByPath("clusters.0.cluster.server");
      String serverUrlString;
      if (serverUrl == null || StringUtils.isBlank(serverUrlString = (String) serverUrl)) {
        throw new ConfigNotSupportException("server is blank");
      }
      k8sConfigDTO.setServerUrl(serverUrlString);
      Object caData = jsonObject.getByPath("clusters.0.cluster.certificate-authority-data");
      String caDataString;
      if (caData == null || StringUtils.isBlank(caDataString = (String) caData)) {
        throw new ConfigNotSupportException("caData is blank");
      }
      k8sConfigDTO.setCertificateAuthorityData(caDataString);
      Object ccaData = jsonObject.getByPath("users.0.user.client-certificate-data");
      String ccaDataString;
      if (ccaData == null || StringUtils.isBlank(ccaDataString = (String) ccaData)) {
        throw new ConfigNotSupportException("ccaData is blank");
      }
      k8sConfigDTO.setClientCertificateData(ccaDataString);
      Object ckData = jsonObject.getByPath("users.0.user.client-key-data");
      String ckDataString;
      if (ckData == null || StringUtils.isBlank(ckDataString = (String) ckData)) {
        throw new ConfigNotSupportException("ccaData is blank");
      }
      k8sConfigDTO.setClientKeyData(ckDataString);
      return k8sConfigDTO;
    }
    return null;
  }
}
