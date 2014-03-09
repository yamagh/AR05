package jp.ygmh.ar05.xmlrpc.webserver;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

  public class Server {
      private static final int port = 8081;  // サーバのポート番号

      public static void main(String[] args) throws Exception {
          WebServer webServer = new WebServer(port);
          XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
          
          // Pictureクラスをマッピングに追加
          PropertyHandlerMapping phm = new PropertyHandlerMapping();
          phm.addHandler("Picture", jp.ygmh.ar05.xmlrpc.Picture.class);
          xmlRpcServer.setHandlerMapping(phm);
        
          XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
          serverConfig.setEnabledForExtensions(true);
          serverConfig.setContentLengthOptional(false);

          webServer.start();
      }
  }
