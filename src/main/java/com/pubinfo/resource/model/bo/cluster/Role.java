package com.pubinfo.resource.model.bo.cluster;

import com.pubinfo.resource.model.bo.Base;
import io.kubernetes.client.openapi.models.V1ClusterRole;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PolicyRule;
import io.kubernetes.client.openapi.models.V1Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
//@Data
@Getter
@Setter
@NoArgsConstructor
@ApiModel(value = "角色")
public class Role implements Serializable {
  
  private static final long serialVersionUID = 3886609240081575338L;
  @ApiModelProperty(value = "角色信息集合")
  public List<RoleInfo> items;
  @ApiModelProperty(value = "api版本")
  private String apiVersion;
  @ApiModelProperty(value = "角色信息")
  private RoleInfo roleInfo;
  @ApiModelProperty(value = "资源类型")
  private List<PolicyRule> rules;
  
  public Role(V1Role v1Role) {
    this.apiVersion = v1Role.getApiVersion();
    this.roleInfo = new RoleInfo(v1Role.getMetadata(), v1Role.getKind());
    List<V1PolicyRule> v1PolicyRuleList = v1Role.getRules();
    setRules(v1PolicyRuleList);
  }
  
  public Role(V1ClusterRole v1ClusterRole) {
    this.apiVersion = v1ClusterRole.getApiVersion();
    this.roleInfo = new RoleInfo(v1ClusterRole.getMetadata(), v1ClusterRole.getKind());
    setRules(v1ClusterRole.getRules());
  }
  
  private void setRules(List<V1PolicyRule> v1PolicyRuleList) {
    if (CollectionUtils.isNotEmpty(v1PolicyRuleList)) {
      v1PolicyRuleList.stream().forEach(v1PolicyRule -> {
        this.rules.add(new PolicyRule(v1PolicyRule));
      });
    }
    
  }
  
  //@Data
@Getter
@Setter
  @NoArgsConstructor
  @ApiModel(value = "角色信息")
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  public static class RoleInfo extends Base {
    
    private static final long serialVersionUID = 3886609240081575338L;
    @ApiModelProperty(value = "资源类型")
    private String kindMeta;
    
    public RoleInfo(V1ObjectMeta objectMetaSource, String type) {
      this.kindMeta = type;
      setMetaData(objectMetaSource);
    }
  }
  
  //@Data
@Getter
@Setter
  @NoArgsConstructor
  @ApiModel(value = "角色权限")
  //@JsonInclude(JsonInclude.Include.NON_NULL)
  public static class PolicyRule {
    @ApiModelProperty(value = "api集合")
    private List<String> apiGroups = null;
    @ApiModelProperty(value = "资源url集合")
    private List<String> nonResourceURLs = null;
    @ApiModelProperty(value = "资源名称集合")
    private List<String> resourceNames = null;
    @ApiModelProperty(value = "资源")
    private List<String> resources = null;
    @ApiModelProperty(value = "操作集合")
    private List<String> verbs = new ArrayList<String>();
    
    public PolicyRule(V1PolicyRule v1PolicyRule) {
      this.apiGroups = v1PolicyRule.getApiGroups();
      this.nonResourceURLs = v1PolicyRule.getNonResourceURLs();
      this.resourceNames = v1PolicyRule.getResourceNames();
      this.resources = v1PolicyRule.getResources();
      this.verbs = v1PolicyRule.getVerbs();
    }
  }
}

