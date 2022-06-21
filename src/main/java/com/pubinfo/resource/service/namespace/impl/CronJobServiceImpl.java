package com.pubinfo.resource.service.namespace.impl;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.utils.K8sSearch;
import com.pubinfo.resource.common.utils.K8sUtils;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.bo.namespace.CronJob;
import com.pubinfo.resource.model.bo.namespace.Event;
import com.pubinfo.resource.model.bo.namespace.Job;
import com.pubinfo.resource.model.constant.ApiVersionEnum;
import com.pubinfo.resource.model.constant.K8sParam;
import com.pubinfo.resource.model.constant.K8sParam.*;
import com.pubinfo.resource.model.constant.KindEnum;
import com.pubinfo.resource.model.dto.SearchParamDTO;
import com.pubinfo.resource.model.vo.base.Page;
import com.pubinfo.resource.service.cluster.EventsService;
import com.pubinfo.resource.service.common.K8sApiService;
import com.pubinfo.resource.service.common.K8sService;
import com.pubinfo.resource.service.namespace.CronJobService;
import com.pubinfo.resource.service.namespace.JobService;
import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.service.namespace.ServiceOfService;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.BatchV1beta1Api;
import io.kubernetes.client.openapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class CronJobServiceImpl extends K8sSearch implements CronJobService {
  @Autowired
  K8sApiService k8sApiService;
  @Autowired
   K8sService k8sService;
  @Autowired
  PodService podService;
  @Autowired
  EventsService eventsService;
  @Autowired
  ServiceOfService serviceOfService;
  @Autowired
  JobService jobService;
  
  @Override
  public Page<List<CronJob>> listCronJob(SearchParamDTO paramVo) {
    BatchV1beta1Api batchV1beta1Api = k8sApiService.getBatchV1beta1Api();
    List<V1beta1CronJob> items = null;
    String nameSpace = k8sApiService.getNamespace();
    try {
      if (nameSpace.indexOf(K8sParam.NAMESPACE_ALL) == 0) {
        
        items = batchV1beta1Api.listCronJobForAllNamespaces(ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.pretty, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch
        ).getItems();
        
      } else {
        items = batchV1beta1Api.listNamespacedCronJob(nameSpace, ListParam.pretty, ListParam.allowWatchBookmarks, ListParam._continue, ListParam.fieldSelector, ListParam.labelSelector, ListParam.limit, ListParam.resourceVersion, ListParam.timeoutSeconds, ListParam.watch).getItems();
      }
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CRON_JOB_FAIL, K8sUtils.getMessage(e));
    }
    
    K8sSearch search = new K8sSearch();
    List<V1beta1CronJob> list = pagingOrder(paramVo, items, V1beta1CronJob::getMetadata, nameSpace);
    List<CronJob> cronJobs = new ArrayList<>();
    if (list != null && list.size() > 0) {
      list.stream().forEach(v1beta1CronJob -> {
        
        CronJob cronJob = new CronJob().initCronJob(v1beta1CronJob);
        cronJobs.add(cronJob);
      });
    }
    Page<List<CronJob>> page = new Page<>(paramVo, cronJobs, getTotalItem(), cronJobs.size());
    return page;
  }
  
  List<String> listJobByCronJob(V1beta1CronJob v1beta1CronJob) {
    List<V1ObjectReference> active = v1beta1CronJob.getStatus().getActive();
    List<String> jobNames = new ArrayList<>();
    if (active != null && !active.isEmpty()) {
      active.stream().forEach(v1ObjectReference -> {
        jobNames.add(v1ObjectReference.getName());
      });
    }
    return jobNames;
  }
  
  @Override
  public CronJob readCronJob(String nameSpace, String name) {
    if (!k8sApiService.isUserNameSapce(nameSpace)) {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    BatchV1beta1Api batchV1beta1Api = k8sApiService.getBatchV1beta1Api();
    V1beta1CronJob v1beta1CronJob;
    CronJob cronJob = null;
    
    try {
      v1beta1CronJob = batchV1beta1Api.readNamespacedCronJob(name, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
    } catch (ApiException e) {
      
      throw new ServiceException(ResultCode.QUERY_CRON_JOB_FAIL, K8sUtils.getMessage(e));
    }
    List<String> jobNames = listJobByCronJob(v1beta1CronJob);
    List<Job> jobList = jobService.listJobByCronJob(nameSpace, jobNames);
    List<Event> eventList = eventsService.listNamespacedEvent(v1beta1CronJob.getMetadata(), "CronJob");
    cronJob = new CronJob(v1beta1CronJob, jobList, eventList);
    
    return cronJob;
  }
  
  @Override
  public Boolean createJob(String name, String selfLink) {
    if(StringUtils.isEmpty(selfLink)){
      throw new ServiceException(ResultCode.VALIDATE_FAILED, "请添加url");
    }
    String[] urls = selfLink.split("/");
    String nameSpace = urls[urls.length-3];
    String cronJobName = urls[urls.length-1];
   
    V1beta1CronJob v1beta1CronJob;
    BatchV1beta1Api batchV1beta1Api = k8sApiService.getBatchV1beta1Api();
    try {
      v1beta1CronJob = batchV1beta1Api.readNamespacedCronJob(cronJobName, nameSpace, ReadParam.pretty, ReadParam.exact, ReadParam.export);
  
      V1Job v1Job = new V1Job();
      V1ObjectMeta metadata = new V1ObjectMeta();
      metadata.setNamespace(nameSpace);
      metadata.setName(name);
      V1JobSpec v1JobSpec = v1beta1CronJob.getSpec().getJobTemplate().getSpec();
      v1Job.setApiVersion(ApiVersionEnum.BATCH_V1.getApiVersionType());
      v1Job.setKind(KindEnum.JOB.getKind());
      v1Job.setMetadata(metadata);
      v1Job.setSpec(v1JobSpec);
      BatchV1Api batchV1Api = k8sApiService.getBatchV1Api();
      V1Job job = batchV1Api.createNamespacedJob(nameSpace,v1Job, CreateParam.pretty, CreateParam.dryRun, CreateParam.fieldManager);
      if (job != null) {
        return true;
      }
      
    } catch (ApiException e) {
      throw new ServiceException(ResultCode.QUERY_CRON_JOB_FAIL, K8sUtils.getMessage(e));
    }
    return false;
  }
}
