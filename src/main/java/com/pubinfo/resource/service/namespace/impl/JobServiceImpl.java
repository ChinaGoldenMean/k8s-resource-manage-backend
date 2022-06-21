package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.Builder;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.Job;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.ListParam;
import com.pubinfo.resource.model.constant.K8sParam.ReadParam;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.JobVo;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.namespace.JobService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1PodList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@org.springframework.stereotype.Service
public class JobServiceImpl extends K8sSearch implements JobService {
  
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  @Autowired
  ServiceOfService serviceOfService;
  @Autowired
  K8sApiService k8sService;
  
  @Override
  public Page<JobVo> listJob(SearchParamDTO paramVo) {
    BatchV1Api batchV1Api = k8sService.getBatchV1Api();
    String namespace = k8sService.getNamespace();
    
    List<V1Job> items;
    try {
      if (namespace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        items = batchV1Api.listJobForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
       // items = filterByNamespaces(items, V1Job::getMetadata, namespace);
        
      } else {
        items = batchV1Api.listNamespacedJob(namespace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_JOB_FAIL);
    }
    List<V1Job> list = pagingOrder(paramVo, items, V1Job::getMetadata, namespace);
    List<Job> jobs = generateListJob(list);
    if (jobs == null) {
      return null;
    }
    JobVo jobVo = new JobVo(jobs, list);
    Page<JobVo> page = new Page<>(paramVo, jobVo, getTotalItem(), jobs.size());
    return page;
  }
  
  @Override
  public List<Job> generateListJob(List<V1Job> v1JobList) {
    List<Job> jobs = new ArrayList<>();
    if (v1JobList != null && v1JobList.size() > 0) {
      v1JobList.stream().forEach(v1Job -> {
        jobs.add(toJob(v1Job));
      });
    }
    return jobs;
  }
  
  @Override
  public Job toJob(V1Job v1Job) {
//    V1PodList v1PodList = podService.listV1Pod(v1Job.getMetadata(), true, "job");
    V1PodList v1PodList = podService.listV1Pod(v1Job.getSpec().getSelector(), v1Job.getMetadata().getNamespace(), v1Job.getMetadata().getName());
    Set<Event> eventList = eventsService.generateEventSet(v1PodList);
    
    return new Job().initJob(v1Job, v1PodList, eventList);
  }
  
  @Override
  public List<Job> listJobByCronJob(String nameSpace, List<String> jobNames) {
    BatchV1Api batchV1Api = k8sService.getBatchV1Api();
    List<V1Job> v1Jobs = new ArrayList<>();
    if (jobNames != null && !jobNames.isEmpty()) {
      jobNames.stream().forEach(jobName -> {
        V1Job v1Job = null;
        try {
          v1Job = batchV1Api.readNamespacedJob(jobName, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
        } catch (ApiException e) {
          throw new ServiceException(ResultCode.QUERY_JOB_FAIL, K8sUtils.getMessage(e));
        }
        v1Jobs.add(v1Job);
      });
    }
    List<Job> jobs = generateListJob(v1Jobs);
    return jobs;
  }
  
  @Override
  public Job readJob(String nameSpace, String name) {
    if (!k8sService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    BatchV1Api batchV1Api = k8sService.getBatchV1Api();
    V1Job v1Job;
    Job job = null;
    
    try {
      v1Job = batchV1Api.readNamespacedJob(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_JOB_ERROR, K8sUtils.getMessage(e));
    }
    //  V1PodList v1PodList = podService.listV1Pod(v1Job.getMetadata(), true, "Job");
    V1PodList v1PodList = podService.listV1Pod(v1Job.getSpec().getSelector(), v1Job.getMetadata().getNamespace(), v1Job.getMetadata().getName());
    Set<Event> eventSet = eventsService.generateEventSet(v1PodList);
    List<Event> podEventList = eventsService.listNamespacedEventByWarning(v1Job.getMetadata(), null);
    job = new Job(v1Job, v1PodList, eventSet, podEventList);
    
    return job;
  }
  
  @Override
  public Builder readBuilder(String nameSpace, String name) {
    BatchV1Api batchV1Api = k8sService.getBatchV1Api();
    Builder builder = null;
    V1Job v1Job = null;
    try {
      v1Job = batchV1Api.readNamespacedJob(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_JOB_ERROR, K8sUtils.getMessage(e));
    }
    builder = toJob(v1Job);
    return builder;
  }
  
}
