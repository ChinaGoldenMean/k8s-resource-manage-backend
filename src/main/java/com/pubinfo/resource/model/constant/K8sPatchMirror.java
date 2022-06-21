package com.pubinfo.resource.model.constant;

public class K8sPatchMirror {
  
  public final static String TEMPLATE_CONTAINER_MIRROR_PATH = "/spec/template/spec/containers/0/image";
  
  public final static String CONTAINER_MIRROR_PATH = "/spec/containers/0/image";
  
  public final static String NODE_LABEL = "/metadata/labels";
  
  public final static String NODE_UNSCHEDULABLE = "/spec/unschedulable";
  
  public final static String TEMPLATE_CONTAINERS_RESOURCE_LIMIT = "/spec/template/spec/containers/0/resources/limits";
  
  public final static String TEMPLATE_SPEC_NODESELECTOR = "/spec/template/spec/nodeSelector";
  
  public final static String SPEC_METADATA_LABELS = "/spec/template/metadata/labels";
  
  public final static String TEMPLATE_SPEC_VOLUME = "/spec/template/spec/volumes";
  
  public final static String TEMPLATE_CONTAINERS_VOLUMEMOUNTS = "/spec/template/spec/containers/0/volumeMounts";
  
  public final static String SPEC_SELECTOR_MATCHLABELS = "/spec/selector/matchLabels";
}
