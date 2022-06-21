package com.pubinfo.resource.model.constant;

@SuppressWarnings("ALL")
public enum KindEnum {
  
  NAMESPACE("Namespace"),
  DEPLOYMENT("Deployment"),
  POD("Pod"),
  SERVICE("Service"),
  DAEMON_SET("DaemonSet"),
  REPLICA_SET("ReplicaSet"),
  REPLICATION_CONTROLLER("ReplicationController"),
  INGRESS("Ingress"),
  CONFIG_MAP("ConfigMap"),
  NODE("Node"),
  STATEFUL_SET("StatefulSet"),
  ENDPOINTS("Endpoints"),
  JOB("Job"),
  ROLE("Role"),
  CLUSTER_ROLE("ClusterRole"),
  SECRET("Secret"),
  PERSISTENT_VOLUME("PersistentVolume"),
  CRON_JOB("CronJob"),
  CUSTOM_RESOURCE_DEFINITION("CustomResourceDefinition"),
  STORAGE_CLASS("StorageClass"),
  PERSISTENT_VOLUME_CLAIM("PersistentVolumeClaim"),
  SERVICE_ACCOUNT("ServiceAccount"),
  CLUSTER_ROLE_BINDING("ClusterRoleBinding");
  private String kind;
 
  KindEnum(String kind) {
    this.kind = kind;
  }
  
  public static KindEnum getEnumByType(String kindType) {
    for (KindEnum kindEnum : values()) {
      if (kindEnum.getKind().equalsIgnoreCase(kindType)) {
        return kindEnum;
      }
    }
    return null;
  }
  
  public static Boolean isK8sCluster(KindEnum kindTypeEnum) {
    if(kindTypeEnum==null)
      return false;
    switch (kindTypeEnum) {
      case NODE:
      case CLUSTER_ROLE:
      case PERSISTENT_VOLUME:
      case STORAGE_CLASS:
      case CUSTOM_RESOURCE_DEFINITION:
      case SERVICE_ACCOUNT:
      case CLUSTER_ROLE_BINDING:
        return true;
    }
    return false;
  }
  public static String getKindByType(String kindType) {
    for (int i = 0; i < values().length; i++) {
      String kind = values()[i].getKind();
      if (kind.equalsIgnoreCase(kindType)) {
        return kind;
      }
    }
    return null;
  }
  
  public String getKind() {
    return kind;
  }
  
  private void setKind(String kind) {
    this.kind = kind;
  }
  
}
