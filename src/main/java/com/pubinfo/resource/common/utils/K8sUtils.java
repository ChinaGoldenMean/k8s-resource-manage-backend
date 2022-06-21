package com.pubinfo.resource.common.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.pubinfo.resource.model.bo.K8sApiResult;
import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.constant.K8sObject;
import com.pubinfo.resource.model.dto.K8sConfigDTO;
import com.pubinfo.resource.model.dto.K8sYamlDTO;
import com.pubinfo.resource.model.dto.ProjectDTO;
import com.pubinfo.resource.model.vo.PatchVo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.util.SSLUtils;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.scanner.ScannerException;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class K8sUtils {
  private static final Integer CONNECTION_TIME_OUT = 15;
  private static final Integer WRITE_TIME_OUT = 30;
  private static final Integer READ_TIME_OUT = 30;
  
  private K8sUtils() {
  
  }
  
  public static boolean isJson(String string) {
    try {
      return com.alibaba.fastjson.JSON.isValidObject(string);
      
    } catch (JSONException e) {
      return false;
    }
  }
  
  public static K8sYamlDTO transJson2Vo(JSONObject json, String nameSpaces) {
    K8sYamlDTO k8SYamlDTO = null;
    if (json != null) {
      String apiVersion = json.getString(K8sObject.API_VERSION);
      log.info("属性apiVersion", apiVersion);
      if (apiVersion == null || apiVersion.isEmpty()) {
        return null;
      }
      V1ObjectMeta objectMeta = K8sUtils.toObject(json.get(K8sObject.METADATA), V1ObjectMeta.class);
      
      log.info("属性objectMeta", objectMeta);
      if (objectMeta == null || objectMeta.getName() == null) {
        return null;
      }
      String namespace = !StringUtils.isEmpty(objectMeta.getNamespace())
          ? objectMeta.getNamespace()
          : K8sObject.NAMESPACE;
      String kind = json.getString(K8sObject.KIND);
      
      if (nameSpaces != null && nameSpaces.indexOf("all,") < 0) {
        namespace = nameSpaces;
        objectMeta.setNamespace(namespace);
        json.put(K8sObject.METADATA, objectMeta);
      }
      String selfLink = objectMeta.getSelfLink();
      log.info("属性namespace", namespace);
      log.info("属性kind", kind);
      if (kind == null || kind.isEmpty()) {
        return null;
      }
      k8SYamlDTO = new K8sYamlDTO(namespace, apiVersion, kind, objectMeta.getName(), objectMeta.getLabels(),selfLink);
      
      k8SYamlDTO.setO(json);
    }
    return k8SYamlDTO;
  }
  
  public static K8sYamlDTO transJson2Vo(JSONObject json) {
    
    return transJson2Vo(json, null);
  }
  
  public static String getMessage(ApiException e) {
    String message = e.getMessage();
    if (e.getResponseBody() != null) {
      message = com.alibaba.fastjson.JSON.parseObject(e.getResponseBody(), K8sApiResult.class).getMessage();
    }
    return message;
  }
  
  public static String getMessage(String body) {
    
    if (StringUtils.isEmpty(body))
      return body;
    K8sApiResult result=null;
    if (JSONObject.isValidObject(body)) {
        result = com.alibaba.fastjson.JSON.parseObject(body, K8sApiResult.class);
  
    }
    String message ="ok";
    if(result!=null&&result.getCode()!=null&&result.getCode()!=200){
      message = result.getMessage();
      throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL,message);
    }
    if(result==null){
      message = body;
      throw new ServiceException(ResultCode.RESOURCE_UPDATE_FAIL,message);
    }
    return message;
  }
  public static K8sYamlDTO transObject2Vo(Object obj) {
    String strJson = obj.toString();
    JSONObject json = JSONObject.parseObject(strJson);
    K8sYamlDTO dto = new K8sYamlDTO(json);
    return dto;
  }
  
  /**
   * 将yaml_file文件转换为java类
   *
   * @param yamlFile
   * @return
   */
  public static K8sYamlDTO transYaml2Vo(File yamlFile) throws IOException {
    K8sYamlDTO k8SYamlDTO = null;
    if (yamlFile != null && yamlFile.exists()) {
      Object o = Yaml.load(yamlFile);
      log.info(o.toString());
      k8SYamlDTO = transObject2Vo(o);
    }
    return k8SYamlDTO;
  }
  
  /**
   * 转换object类型为其对应的类
   *
   * @param o
   * @param clazz
   * @param <T>
   * @return
   */
  public static <T> T getObject(Object o, Class<T> clazz) {
    if (o != null && o.getClass().equals(clazz)) {
      return clazz.cast(o);
    }
    return null;
  }
  
  /**
   * 生成为patch所用的template里的mirror信息
   *
   * @param value
   */
  public static ArrayList<JsonObject> generatePatchPath(String path, Object value, String op) {
    PatchVo patchVo = new PatchVo();
    if (org.apache.commons.lang.StringUtils.isNotBlank(op)) {
      patchVo.setOp("add");
    }
    patchVo.setPath(path);
    patchVo.setValue(value);
    JsonElement element = new Gson().fromJson(com.alibaba.fastjson.JSON.toJSONString(patchVo), JsonElement.class);
    JsonObject jsonObject = element.getAsJsonObject();
    ArrayList<JsonObject> arr = new ArrayList<>();
    arr.add(jsonObject);
    return arr;
  }
  
  public static OkHttpClient getHttpClient(K8sConfigDTO configDTO) {
    OkHttpClient client;
    X509TrustManager trustManager;
    SSLSocketFactory sslSocketFactory;
    try {
      trustManager = trustManagerForCertificates(configDTO.getCertificateAuthorityData());
      SSLContext sslContext = SSLContext.getInstance("TLS");
      KeyManager[] managers = SSLUtils.keyManagers(
          Base64.decodeBase64(configDTO.getClientCertificateData()),
          Base64.decodeBase64(configDTO.getClientKeyData()),
          "RSA", "", null, null);
      sslContext.init(managers, new TrustManager[]{trustManager}, null);
      sslSocketFactory = sslContext.getSocketFactory();
    } catch (Exception e) {
      log.error("获取连接异常!", e);
      
      throw new ServiceException(ResultCode.GET_CONNECT_ERROR);
    }
    client = new OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustManager)
        .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
        .build();
    return client;
  }
  
  /**
   * okHttp 请求利用k8sConfig配置文件解析成ssl形式请求获取相关数据
   *
   * @return
   */
  public static String okhttpGetBack(String k8sConfig, String url) {
    K8sConfigDTO configDTO = MyJsonUtils.parse(k8sConfig);
    if (configDTO == null) {
      return null;
    }
    String back = null;
    OkHttpClient client = getHttpClient(configDTO);
    Request request = new Request.Builder()
        .url(configDTO.getServerUrl() + url)
        .build();
    try {
      Response response = client.newCall(request).execute();
      back = response.body().string();
    } catch (IOException e) {
      log.error("请求k8s失败!", e);
      
    }
    return back;
  }
  
  public static <T> T toObject(String str, Class<T> tClass) {
    if (StringUtils.isEmpty(str)) {
      return null;
    }
    final org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
    final Object obj = yaml.load(str);
    return toObject(obj, tClass);
  }
  
  public static <T> T toObject(Object obj, Class<T> tClass) {
    if (StringUtils.isEmpty(obj)) {
      return null;
    }
    T cla;
    try {
      final String json = new Gson().toJson(obj);
      cla = Yaml.loadAs(json, tClass);
      
    } catch (ScannerException e) {
      throw new ServiceException(ResultCode.STRING_TO_JSON_ERROR);
    }
    
    return cla;
  }
  
  public static List<ProjectDTO> generateTestData() {
    List<ProjectDTO> projectList = new ArrayList<>();
    ProjectDTO projectDTO = new ProjectDTO();
    projectDTO.setCode("default");
    projectList.add(projectDTO);
    
    ProjectDTO projectDTO2 = new ProjectDTO();
    projectDTO2.setCode("k8s-manager");
    projectList.add(projectDTO2);
    
    ProjectDTO projectDTO3 = new ProjectDTO();
    projectDTO3.setCode("kube-system");
    projectList.add(projectDTO3);
    
    ProjectDTO projectDTO4 = new ProjectDTO();
    projectDTO4.setCode("627");
    projectList.add(projectDTO4);
    ProjectDTO projectDTO5 = new ProjectDTO();
    projectDTO5.setCode("593");
    projectList.add(projectDTO5);
    return projectList;
  }
  
  public static String generateLabel(V1ObjectMeta v1ObjectMeta) {
    String labelSelector = "";
    Map<String, String> labelMap = v1ObjectMeta.getLabels();
    labelSelector = generateString(labelMap);
    return labelSelector;
  }
  public static String generateMatchLabels(V1LabelSelector v1LabelSelector) {
    String labelSelector = "";
    Map<String, String> labelMap = v1LabelSelector.getMatchLabels();
    labelSelector = generateString(labelMap);
    return labelSelector;
  }
  
  public static String generateString(Map<String, String> selectorMap) {
    String selector = "";
    StringBuilder stringBuffer = new StringBuilder();
    if (selectorMap != null && !selectorMap.isEmpty()) {
      
      for (Map.Entry<String, String> entry : selectorMap.entrySet()) {
        if ("pod-template-hash".equals(entry.getKey())) {
          continue;
        }
        if("version".equals(entry.getKey())){
          continue;
        }
        stringBuffer.append(",").append(entry.getKey()).append("=").append(entry.getValue());
      }
      selector = stringBuffer.toString();
      if (!StringUtils.isEmpty(selector)) {
        selector = selector.substring(1);
      }
      
    }
    return selector;
  }
  
  public static String generateSelector(V1ObjectMeta v1ObjectMeta, String kind) {
    String namespace = v1ObjectMeta.getNamespace();
    String name = v1ObjectMeta.getName();
    String uid = v1ObjectMeta.getUid();
    String fieldSelector = "";
    if (!StringUtils.isEmpty(name)) {
      fieldSelector += ",involvedObject.name=" + name;
    }
    if (!StringUtils.isEmpty(namespace)) {
      fieldSelector += ",involvedObject.namespace=" + namespace;
    }
    if (!StringUtils.isEmpty(uid)) {
      fieldSelector += ",involvedObject.uid=" + uid;
    }
    if (!StringUtils.isEmpty(kind)) {
      fieldSelector += ",involvedObject.kind=" + kind;
    }
    if (!StringUtils.isEmpty(fieldSelector)) {
      fieldSelector = fieldSelector.substring(1);
    }
    
    return fieldSelector;
  }
  
  private static X509TrustManager trustManagerForCertificates(String cad) {
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(
          new ByteArrayInputStream(Base64.decodeBase64(cad)));
      
      char[] password = "".toCharArray(); // Any password will work.
      KeyStore keyStore = newEmptyKeyStore(password);
      int index = 0;
      for (Certificate certificate : certificates) {
        String certificateAlias = Integer.toString(index++);
        keyStore.setCertificateEntry(certificateAlias, certificate);
      }
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, password);
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
        throw new IllegalStateException("Unexpected default trust managers:"
            + Arrays.toString(trustManagers));
      }
      
      return (X509TrustManager) trustManagers[0];
    } catch (Exception e) {
      log.error("获取k8s认证失败!", e);
      
    }
    return null;
  }
  
  private static KeyStore newEmptyKeyStore(char[] password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException {
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, password);
      return keyStore;
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }
  
}
