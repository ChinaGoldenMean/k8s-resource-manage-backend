//package com.pubinfo.resource;
//
//import com.pubinfo.resource.service.common.K8sApiService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.apache.commons.lang.StringUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = WebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class YamlTest {
//  @Autowired
//  K8sApiService k8sApiService;
//
////  @SneakyThrows
////  @Test
////  public void testYaml() {
////    String yaml = k8sApiService.getK8sConfig();
////    System.out.println("yaml:\n" + yaml);
////    String json = convertYamlToJson(yaml);
////    System.out.println("json:\n" + json);
////    // Yaml yaml = new Yaml();
////    JsonNode jsonNodeTree = new ObjectMapper().readTree(json);
////    String k8sConfig = new YAMLMapper().writeValueAsString(jsonNodeTree);
////    System.out.println("k8sConfig:\n" + k8sConfig);
////  }
////
////  @SneakyThrows
////  String convertYamlToJson(String yaml) {
////    ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
////    Object obj = null;
////    try {
////      obj = yamlReader.readValue(yaml, Object.class);
////    } catch (JsonProcessingException e) {
////      e.printStackTrace();
////    }
////
////    ObjectMapper jsonWriter = new ObjectMapper();
////    return jsonWriter.writeValueAsString(obj);
////  }
//  @Test
//  public   void test3() {
//    String cookies ="envId=2;aaa=fff";
//    String key ="envId";
//    System.out.println(getStr(cookies,key));
//  }
//  private   String getStr(String cookies,String key){
//    String value=null;
//    if (StringUtils.isNotBlank(cookies)) {
//      String[]  stringEnumeration = cookies.split(";");
//      if (stringEnumeration.length > 0) {
//        for (int i = 0; i < stringEnumeration.length; i++) {
//          String str = stringEnumeration[i];
//          if(str.contains(key)){
//            value =str.substring(key.length()+1 );
//          }
//        }
//      }
//
//    }
//    return value;
//  }
//}
