package com.pubinfo.resource.websocket.thread;

import com.pubinfo.resource.common.exception.ServiceException;
import com.pubinfo.resource.common.vo.ResultCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class OutputThread extends Thread {
  private InputStream inputStream;
  private Session session;
  private static Long TIME_OUT = 10 * 60 * 1000L;
  
  public OutputThread(InputStream inputStream, Session session) {
    this.inputStream = inputStream;
    this.session = session;
  }
  
  @SneakyThrows
  @Override
  public void run() {
    
    Long start = System.currentTimeMillis();
    byte[] bytes = new byte[8164];
    try {
      //this.session.isRegistered()
      while (this.session.isOpen() && (System.currentTimeMillis() - start <= TIME_OUT)) {
        
        int index = inputStream.read(bytes);
        if (index > 0) {
          session.getBasicRemote().sendText(new String(bytes, 0, index));
        }
        
      }
    } catch (IOException e) {
      throw new ServiceException(ResultCode.READ_K8S_POD_LOG_FAIL);
    } finally {
      this.session.close();
      try {
        inputStream.close();
      } catch (IOException e) {
        log.error("读取k8s pod日志时关闭输入流失败!!!");
      }
    }
  }
}
