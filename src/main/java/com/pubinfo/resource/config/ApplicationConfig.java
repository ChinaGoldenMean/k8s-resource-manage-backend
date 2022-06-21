package com.pubinfo.resource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "kube")
public class ApplicationConfig {
  private Boolean debugger;
  private Boolean manyEnv;
}
