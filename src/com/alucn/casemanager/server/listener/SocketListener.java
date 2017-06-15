package com.alucn.casemanager.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.casemanager.server.process.ReceiveAndSendRun;

/**
 * socket listener
 */
public class SocketListener implements ServletContextListener {

	private ServerSocket serverSocket;
	private int listenerPort;
	public static Logger logger = Logger.getLogger(SocketListener.class);
	public static long _COUNT = 0;

	public void contextDestroyed(ServletContextEvent arg0) {
		if (serverSocket != null) {
			if (!serverSocket.isClosed()) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
				}
			}

		}
	}

	public void contextInitialized(ServletContextEvent arg0) {}

	public void initialize() throws Exception {
		try {
			logger.info("[SocketListener Init...]");
			
			// listener port
			listenerPort = Integer.parseInt(ParamUtil.getUnableDynamicRefreshedConfigVal("case.socket.listener.port"));
			// maximum number of threads to handle socket requests
			final int synThreadMaxNumber = Integer.parseInt(ParamUtil.getUnableDynamicRefreshedConfigVal("case.syn.thread_max_number"));
			logger.info("[maximum number of threads to handle socket requests：" + synThreadMaxNumber + "]");
			
			
			try {
				serverSocket = new ServerSocket(listenerPort);
				logger.info("[listener port " + listenerPort + " success" + "]");
			} catch (IOException e) {
				logger.error("[listener port " + listenerPort + "failed" + "]");
				throw e;
			}
			
			// Init listener thread
			new Thread(new Runnable() {

				public void run() {
					// Init thread pool
					// unlimited buffer pool
					// ExecutorService executorService =
					// Executors.newCachedThreadPool();
					// limited buffer pool
					ExecutorService executorService = Executors
							.newFixedThreadPool(synThreadMaxNumber);
					
					//This type can be used to easily monitor thread pool status
					ThreadPoolExecutor threadPoolExecutor =(ThreadPoolExecutor)executorService;
					//Socket to be processed
					Socket socket = null;
					
					int readTimeout = Integer.parseInt(ParamUtil.getUnableDynamicRefreshedConfigVal("case.socket.read_timeout"));
					int waitQueueMaxSize  = Integer.parseInt(ParamUtil.getUnableDynamicRefreshedConfigVal("case.socket.wait_queue_max_size"));
					//Loop blocking port
					while (!serverSocket.isClosed()) {
						try {
							
							//Blocking the listening port, to a socket request, return request socket
							socket = serverSocket.accept();
							
							if(threadPoolExecutor.getActiveCount() >= synThreadMaxNumber && threadPoolExecutor.getQueue().size() >= waitQueueMaxSize){
								//Request host address
								logger.debug("socket.getInetAddress()  = " +socket.getInetAddress());
								//Number of active threads in the thread pool
								logger.debug("threadPoolExecutor.getActiveCount()  = " +threadPoolExecutor.getActiveCount());
//								sendSysBusyMessage(socket);
							}else{
								//request host
								String host = socket.getInetAddress().toString().replace("/", "");
								logger.debug("socket.getInetAddress()  = " +host);
								logger.debug("activeCount=" +Thread.activeCount()+"  currentThreadName="+Thread.currentThread().getName()+"  currentThreadID="+Thread.currentThread().getId()+" socket run start  ");
								//Number of active threads in the thread pool
								logger.debug("threadPoolExecutor.getActiveCount()  = " +threadPoolExecutor.getActiveCount());
								 
								socket.setSoTimeout(readTimeout);
								Socket socketInfo = CaseConfigurationCache.socketInfo.get(host);
								//Start thread only when the connection is established for the first time
								if(socketInfo==null || socketInfo.isClosed()){
									executorService.execute(new ReceiveAndSendRun(socket));
								}else{
									logger.info("[host "+host+ " Already connect...]");
									socket.close();
								}
							}
						} catch (Exception e) {
							logger.error("[Failed to monitor master thread socket processing]");
							if(null!=socket){
								//Request host address
								logger.error("[Socket request host address： " +socket.getInetAddress()+"]");
							}
							logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
						}
					}
				}

			}).start();

		} catch (Exception e) {
			logger.error("[Socket listener failed to start]");
			logger.error(ParamUtil.getErrMsgStrOfOriginalException(e));
			throw e;
		} 
	}
	
	/**
	 * send response
	 */
/*	public void sendSysBusyMessage(Socket socket) throws IOException {
		Head head = new Head();
//		head.setRtncode(RtnCodeConstants.EC_00000_CODE);
//		head.setRtntextcd(RtnCodeConstants.EC_00000_MSG);
		String sysBusyMsg =  head.getJson();
		//head
		int jsonDataLength = sysBusyMsg.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8).length;
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.write(int2ByteArr(jsonDataLength));
		dos.write(sysBusyMsg.getBytes(Constant.CHARACTER_SET_ENCODING_UTF8));
		dos.flush();
		logger.error("["+socket.getInetAddress()+" the request is busy with the response system ]");
		if(null != null){
			dos.close();
			dos = null;
		}
		if(null != socket && !socket.isClosed()){
			socket.close();
			socket = null;
		}
	}*/
	
	/**
	 * @param i
	 * @return
	 */
	public  byte[] int2ByteArr(int i){
		byte[] result = new byte[4];
		result[0] = (byte)((i>>24)&0xFF);
		result[1] = (byte)((i>>16)&0xFF);
		result[2] = (byte)((i>>8)&0xFF);
		result[3] = (byte)(i&0xFF);
		return result;
	}
}
