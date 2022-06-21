package com.pubinfo.resource.service.namespace;

import com.pubinfo.resource.model.bo.namespace.Pod;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.PodVo;
import com.pubinfo.resource.model.vo.base.Page;
import io.kubernetes.client.openapi.models.*;
import okhttp3.Call;

import java.util.List;
import java.util.Map;

public interface PodService {
  /**
   * 根据查询对象获取容器组集合
   *
   * @param paramVo
   * @return
   */
  Page<PodVo> listPod(SearchParamDTO paramVo);
  
  /**
   * 容器组数据封装
   *
   * @param list
   * @param paramVo
   * @return
   */
  Page<PodVo> toPodPage(List<V1Pod> list, SearchParamDTO paramVo, String namespace);
  
  /**
   * 根据节点名称查询容器组集合
   *
   * @param nodeName
   * @return
   */
  V1PodList listV1PodByNode(String nodeName,Boolean isFilter);
  
  /**
   * 根据查询对象与节点名称获取容器组集合分页数据
   *
   * @param paramVo
   * @param nodeName
   * @return
   */
  Page<PodVo> listPodByNode(SearchParamDTO paramVo, String nodeName);
  
  /**
   * 读取容器组
   *
   * @param nameSpace
   * @param name
   * @return
   */
  Pod readPod(String nameSpace, String name);
  
  /**
   * 实时读取容器日志
   *
   * @param envId
   * @param nameSpace
   * @param podName
   * @param containerName
   * @return
   */
  Call namespacedPodLogCall(Integer envId, String nameSpace, String podName, String containerName);
  
  /**
   * 根据选择器查询容器组集合
   *
   * @param selector
   * @param nameSpace
   * @return
   */
  V1PodList listV1PodByLabelSelector(Map<String, String> selector, String nameSpace);
  
  /**
   * 根据对象元数据查询容器组集合
   *
   * @param v1ObjectMeta
   * @param isLabel
   * @param kind
   * @return
   */
  V1PodList listV1Pod(V1ObjectMeta v1ObjectMeta, boolean isLabel, String kind);
  
  /**
   * 根据标签查询容器组集合
   *
   * @param v1LabelSelector
   * @param namespace
   * @return
   */
  V1PodList listV1Pod(V1LabelSelector v1LabelSelector, String namespace, String ownerReferencesName);
  
  V1PodList listV1Pod(V1LabelSelector v1LabelSelector, String namespace);
  
  /**
   * 根据标签查询容器组集合
   *
   * @param selector
   * @param namespace
   * @return
   */
  V1PodList listV1Pod(Map<String, String> selector, String namespace);
  
  V1PodList filterOwnerReferences(V1PodList v1PodList, String ownerReferencesName);
  
  V1PodList filterOwnerReferences(V1PodList v1PodList, V1ReplicaSetList v1ReplicaSetList);
}
