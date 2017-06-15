package com.alucn.weblab.service;

import java.io.File;
import org.springframework.stereotype.Service;
import com.alucn.casemanager.server.common.CaseConfigurationCache;
import com.alucn.casemanager.server.common.constant.Constant;
import com.alucn.casemanager.server.common.util.Fifowriter;
import com.alucn.casemanager.server.common.util.FileUtil;
import com.alucn.casemanager.server.common.util.ParamUtil;
import com.alucn.weblab.model.Server;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author haiqiw
 * 2017年6月5日 下午6:28:09
 * desc:ServerInfoService
 */
@Service("serverInfoService")
public class ServerInfoService {
	
	public JSONArray getServerInfo(){
		JSONArray infos = CaseConfigurationCache.readOrWriteSingletonCaseProperties(CaseConfigurationCache.lock, true, null);
		return infos;
	}
	
	public void addServerDetails(Server server) throws Exception{
		String filePath = ParamUtil.getUnableDynamicRefreshedConfigVal("case.client.sftp.sourcepath");
		Fifowriter.writerFile(filePath, Constant.SPAANDRTDB, JSONObject.fromObject(server).toString());
		String sftpTargetPath = ParamUtil.getUnableDynamicRefreshedConfigVal("case.client.sftp.targetpath");
		String shellName = ParamUtil.getUnableDynamicRefreshedConfigVal("case.client.sftp.sendshellname");
		String userName = ParamUtil.getUnableDynamicRefreshedConfigVal("case.client.sftp.username");
		String password = ParamUtil.getUnableDynamicRefreshedConfigVal("case.client.sftp.password");
		int port = Integer.parseInt(ParamUtil.getUnableDynamicRefreshedConfigVal("case.client.sftp.password"));
		FileUtil.upLoadFile(FileUtil.createSession(server.getServerIp(), userName, password, port), filePath, sftpTargetPath);
		String[] cmds = new String[] {"sh "+sftpTargetPath+File.separator+shellName};
		String[] result = FileUtil.execShellCmdBySSH(server.getServerIp(), port, userName, password, cmds);
		for(String str : result){
			System.out.println(str);
		}
	}
	
	public void removeServerInfo(){
	}
	
	public void removeServerDetails(){
	}
	
	public void updateServerInfo(){
	}
	
	public void cancel(){
	}
}
