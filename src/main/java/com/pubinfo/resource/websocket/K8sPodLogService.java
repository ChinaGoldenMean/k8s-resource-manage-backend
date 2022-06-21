package com.pubinfo.resource.websocket;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.vo.ResultCode;
import com.pubinfo.resource.model.constant.K8sConstant;

import com.pubinfo.resource.service.namespace.PodService;
import com.pubinfo.resource.websocket.thread.OutputThread;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ServerEndpoint(value = "/websocket/k8s/pod/log")
@Component
@Slf4j
public class K8sPodLogService {
  
  //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static transient volatile Set<Session> webSocketSessionSet = ConcurrentHashMap.newKeySet();
  private Session session;
  private ThreadPoolExecutor threadPoolExecutor;
  private Call call;
  private ResponseBody responseBody;
  
  @Value("${spring.profiles.active}")
  private String env;
  private static PodService podService;
  
  public static PodService getPodService() {
    return podService;
  }
  
  public static void setPodService(PodService service) {
    podService = service;
  }
  
  @OnOpen
  public void onOpen(Session session) {
    //  session.getp
    Map<String, List<String>> parameterMap = session.getRequestParameterMap();
    
    String envId = parameterMap.get("envId").get(0);
    String podName = parameterMap.get("podName").get(0);
    String containerName = parameterMap.get("containerName").get(0);
    String token = parameterMap.get("token").get(0);
    String nameSpace = parameterMap.get("nameSpace").get(0);
    if (!checkParamter(session, podName, containerName, token, nameSpace)) {
      return;
    }
//    log.info("当前用户id" + SecurityUtils.getCurrentUserId());
//    log.info("当前用户name" + SecurityUtils.getCurrentUserName());
    
    this.session = session;
    webSocketSessionSet.add(session);
    if (!StringUtils.isEmpty(env) && K8sConstant.PROD.equalsIgnoreCase(env))   {
      throw new ServiceException(ResultCode.FORBIDDEN);
    }
    call = podService.namespacedPodLogCall(Integer.parseInt(envId), nameSpace, podName, containerName);
    try {
      Response response = call.execute();
      
      if (response == null) {
        session.getBasicRemote().sendText("未找到对应的pod的log日志");
        
        session.close();
        return;
      }
      responseBody = response.body();
      InputStream inputStream = null;
      if (responseBody != null) {
        inputStream = responseBody.byteStream();
      }
      
      threadPoolExecutor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.SECONDS
          , new ArrayBlockingQueue<>(10));
      threadPoolExecutor.submit(new OutputThread(inputStream, session));
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ServiceException(ResultCode.GET_K8S_WEBSOCKET_CONNECT_FAIL);
    }
    
  }
  
  @SneakyThrows
  private Boolean checkParamter(Session session, String podName, String containerName, String token, String nameSpace) {
    log.info("参数传递:   podName: {}, containerName: {},token:{}", podName, containerName, token);
    
    if (Objects.isNull(podName)) {
      session.getBasicRemote().sendText("podName不能为空");
      return false;
    }
    if (Objects.isNull(containerName)) {
      session.getBasicRemote().sendText("containerName不能为空");
      return false;
    }
    if (Objects.isNull(token)) {
      
      session.getBasicRemote().sendText("token不能为空");
      
      return false;
    }
    if (Objects.isNull(nameSpace)) {
      session.getBasicRemote().sendText("nameSpace不能为空");
      return false;
    }
    return true;
  }
  
  /**
   * 实现服务器主动推送
   */
  @SneakyThrows
  @OnMessage
  public boolean sendMessage(String message) {
    boolean success = false;
    if (this.session != null) {
      this.session.getBasicRemote().sendText(message);
      success = true;
    }
    return success;
  }
  
  /**
   * 连接关闭调用的方法
   */
  @SneakyThrows
  @OnClose
  public void onClose() {
    log.info("websocket连接关闭: {}", session);
    webSocketSessionSet.remove(session);
    if (session != null) {
      session.close();
    }
    if (call != null) {
      call.cancel();
      log.info("k8s call cancel:{}", call.isCanceled());
    }
    if (threadPoolExecutor != null) {
      threadPoolExecutor.shutdown();
      if (!threadPoolExecutor.isTerminated()) {
        try {
          threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS);
          threadPoolExecutor.shutdownNow();
          responseBody.close();
        } catch (InterruptedException e) {
          
          log.info("关闭input stream出错:{}", e.getMessage());
          Thread.currentThread().interrupt();
        }
        
      }
    }
    
  }
}
