package com.pubinfo.resource.common.utils;

import com.pubinfo.resource.model.bo.cluster.Role;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.ObjectMetaVo;
import com.pubinfo.resource.service.common.K8sApiService;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Slf4j
public class K8sSearch {
  
  protected Integer totalItem = 0;
  public static final String CREATE_TIME_STAMP = "createTimeStamp";
  public static final String ORDER = "d";
  @Autowired
  K8sApiService k8sApiService;
  
  @SneakyThrows
  public <T> List<T> pagingOrder(SearchParamDTO vo, List<T> list, Function<T, V1ObjectMeta> getV1ObjectMeta, String namespaces) {
    
    String filterName = vo.getFilterBy();
    String sortBy = vo.getSortBy();
    int limit = vo.getItemsPerPage();
    int skip = vo.getSkip();
    if (list == null || list.isEmpty()) {
      this.totalItem = 0;
      return list;
    }
    list =filterByNamespaces(list, getV1ObjectMeta, namespaces);
    if (!StringUtils.isEmpty(filterName)) {
      list = list.stream().filter(
          t -> {
            return getV1ObjectMeta.apply(t).getName().contains(vo.getFilterBy());
          }
      ).collect(Collectors.toList());
    }
    
    this.totalItem = list.size();
    list = list.stream().skip(skip).limit(limit).sorted((t1, t2) -> {
      V1ObjectMeta om1 = getV1ObjectMeta.apply(t1);
      V1ObjectMeta om2 = getV1ObjectMeta.apply(t2);
      return sort(om1, om2, sortBy);
    }).collect(Collectors.toList());
    
    return list;
  }
  
  public <T> List<T> filterByNamespaces(List<T> list, Function<T, V1ObjectMeta> getV1ObjectMeta, String namespaces) {
    if (k8sApiService.isNotProd() || !k8sApiService.isK8sManage()) {
      if (!StringUtils.isEmpty(namespaces) && namespaces.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        String[] namespacesArray = namespaces.split(",");
        List<String> namespacesList = new ArrayList<String>(Arrays.asList(namespacesArray));
        namespacesList.remove(0);
        
        list = list.stream().filter(
            t -> {
              return namespacesList.contains(getV1ObjectMeta.apply(t).getNamespace());
            }
        ).collect(Collectors.toList());
      }
    }
    
    return list;
  }
  
  public int sort(V1ObjectMeta om1, V1ObjectMeta om2, String sortBy) {
    return sort(new ObjectMetaVo(om1), new ObjectMetaVo(om2), sortBy);
  }
  
  public int sort(Role.RoleInfo om1, Role.RoleInfo om2, String sortBy) {
    Date data1 = om1.getMeta_creationTimestamp();
    Date data2 = om2.getMeta_creationTimestamp();
    String name1 = om1.getMeta_name();
    String name2 = om2.getMeta_name();
    return baseSort(data1, data2, name1, name2, sortBy);
  }
  
  public int baseSort(Date data1, Date data2, String name1, String name2, String sortBy) {
    if (!StringUtils.isEmpty(sortBy)) {
      String[] sorts = sortBy.split(",");
      if (CREATE_TIME_STAMP.equals(sorts[1])) {
        
        if (ORDER.equals(sorts[0])) {
          return data2.compareTo(data1);
        } else {
          return data1.compareTo(data2);
        }
        
      } else {
        if ("d".equals(sorts[0])) {
          return name2.compareTo(name1);
        } else {
          return name1.compareTo(name2);
        }
      }
      
    } else {
      return data2.compareTo(data1);
    }
  }
  
  public int sort(ObjectMetaVo om1, ObjectMetaVo om2, String sortBy) {
    Date data1 = om1.getCreationTimestamp();
    Date data2 = om2.getCreationTimestamp();
    String name1 = om1.getName();
    String name2 = om2.getName();
    return baseSort(data1, data2, name1, name2, sortBy);
  }
}
