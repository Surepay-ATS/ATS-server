package com.alucn.weblab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alucn.weblab.dao.impl.UserDaoImpl;
import com.alucn.weblab.model.User;

/**
 * @author haiqiw
 * 2017年6月5日 下午5:27:40
 * desc:LoginService
 */
@Service("loginService")
public class LoginService {
	
	@Autowired(required=true)
	private UserDaoImpl userDaoImpl;
	
	
	public boolean getUser(User user){
		return false;
	}
	public UserDaoImpl getUserDaoImpl() {
		return userDaoImpl;
	}
	public void setUserDaoImpl(UserDaoImpl userDaoImpl) {
		this.userDaoImpl = userDaoImpl;
	}
	
	
}
