package socketserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chatDBInit.DBConnect;
import procotol.ChatMsgProtocol;

/**
 * 
 * 上次未实现功能：没有判断一个稳定连接的客户端（可通过几次握手成功确定，几次握手成功后在将用户放入服务端缓存），
 * 
 * Created by orange on 16/6/8.
 * 
 */

public class TCPLongConnectionServer {
	//4c37987f8e13461dbf0c133af338c039小米 IP:172.16.100.32        04fb00752bc94d84b718d9a41e024c7a三星 IP: 172.16.100.90
	//9b0a60c7df57485ba2be6b81dac00d5d preview nexus 5         4d8e938015b34049a21fad31a3e29820 custom phone 5.0
	private static boolean isStart=true;
	private static ServerResponseTask serverResponseTask;
	
	private static DBConnect db=DBConnect.instance;
	
    public TCPLongConnectionServer(){
    	
    }
    
    
    public static void main(String[] args){
    	
        ServerSocket serverSocket=null;
        ExecutorService executorService=Executors.newCachedThreadPool();
        try {
            serverSocket=new ServerSocket(9013);
			while (isStart) {
				Socket socket = serverSocket.accept();
				System.out.println("用户的IP地址为：" + socket.getInetAddress().getHostAddress());
				serverResponseTask = new ServerResponseTask(socket,
						new TCPLongConnectServerHandlerData.TCPResultCallBack() {

							@Override
							public void targetIsOffline(ChatMsgProtocol reciveMsg) {// 对方不在线
								// 新加入的客户端成功连接后
								if (reciveMsg != null) {
									// 不在线就存入离线数据库中
									db.insertMessage(1, reciveMsg.getMessage(), reciveMsg.getSelfUUid(),
											reciveMsg.getMsgTargetUUID(), new Date().toString());
									System.out.println(reciveMsg.getMsgTargetUUID()+" is offLine");
								}
							}
							
							@Override
							public void targetIsOnline(String targetUUId){
								System.out.println(targetUUId+" is onLine");
							}
						},db);
				if (socket.isConnected()) {
					executorService.execute(serverResponseTask);
				}
				// printAllClient();
			}
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket!=null){
                try {
                	isStart=false;
                    serverSocket.close();
                    if(serverSocket!=null)
                    	serverResponseTask.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    

    
    public boolean isOffLineMessageEmpty(){
    	boolean isEmpty=true;
    	
    	if (isEmpty) {
			
		}
    	return isEmpty;
    }
}
