package com.pubinfo.resource.service.cluster;

import com.pubinfo.resource.model.bo.cluster.Namespace;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;

import java.util.List;
import java.util.Set;

public interface EventsService {
  /**
   * 根据查询对象,获取某个命名空间下事件集合
   *
   * @param name
   * @param paramVo
   * @return
   */
  Namespace listEventByParam(String name, SearchParamDTO paramVo);
  
  /**
   * 根据对象元数据与类型查询事件集合
   *
   * @param v1ObjectMeta
   * @param kind
   * @return
   */
  List<Event> listNamespacedEvent(V1ObjectMeta v1ObjectMeta, String kind);
  
  /**
   * 根据对象元数据与类型查询警告类型事件集合
   *
   * @param v1ObjectMeta
   * @param kind
   * @return
   */
  List<Event> listNamespacedEventByWarning(V1ObjectMeta v1ObjectMeta, String kind);
  
  /**
   * 根据容器组集合,生成事件集合
   *
   * @param v1PodList
   * @return
   */
  Set<Event> generateEventSet(V1PodList v1PodList);
  
  /**
   * 根据容器组集合,生成事件集合
   *
   * @param v1PodList
   * @return
   */
  Set<Event> generateEventSet(List<V1Pod> v1PodList);
}
