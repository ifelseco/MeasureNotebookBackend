package com.javaman.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import com.javaman.entity.User;
import com.javaman.model.*;
import com.javaman.security.UserRole;

import com.javaman.util.ConstRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);


	@RequestMapping("/checkSession")
	@Secured({ConstRole.ARG_ADMIN_ROLE, ConstRole.ARG_USER_ROLE, ConstRole.ARG_TAILOR_ROLE})
	public ResponseEntity<UserDetailModel> checkSession() {
		BaseModel baseModel=new BaseModel();
		UserDetailModel userDetailModel=new UserDetailModel();
		try{
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) auth.getPrincipal();
			setRole(userDetailModel, user);
			baseModel.setResponseCode(0);
			baseModel.setResponseMessage("Oturum  aktif!");
			userDetailModel.setBaseModel(baseModel);
			return new ResponseEntity<>(userDetailModel, HttpStatus.OK);
		}catch(Exception e){
			LOG.error("Check Sesion Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Oturum kontrol hatası,tekrar giriş yapınız.");
			userDetailModel.setBaseModel(baseModel);
			return new ResponseEntity<>(userDetailModel,HttpStatus.BAD_REQUEST);
		}
	}


	@RequestMapping(value = "/user/logout", method = RequestMethod.POST)
	public ResponseEntity<BaseModel> logout() {
		BaseModel baseModel=new BaseModel();
		
		try {
			SecurityContextHolder.clearContext();
			baseModel.setResponseCode(0);
			baseModel.setResponseMessage("Çıkış başarılı.");
			return new ResponseEntity<>(baseModel, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Logout Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Çıkış yapılırken hata oluştu.");
			return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
		}

	}


	@RequestMapping("/token")
	@Secured({ConstRole.ARG_ADMIN_ROLE, ConstRole.ARG_USER_ROLE, ConstRole.ARG_TAILOR_ROLE})
	public ResponseEntity<LoginUserModel> token(HttpSession session, HttpServletRequest request) {
		LoginUserModel loginUserModel=new LoginUserModel();
		BaseModel baseModel=new BaseModel();

		try{
			String remoteHost = request.getRemoteHost();
			int portNumber = request.getRemotePort();
			LOG.info("Host : Port :",""+remoteHost+ " : "+portNumber);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) auth.getPrincipal();

			if(user.getTenant().isEnabled()){
				loginUserModel.setToken(session.getId());
				UserDetailModel userDetailModel=new UserDetailModel();
				setRole(userDetailModel, user);
				loginUserModel.setUserDetailModel(userDetailModel);

				baseModel.setResponseCode(0);
				baseModel.setResponseMessage("Giriş başarılı.");
				loginUserModel.setBaseModel(baseModel);

				return new ResponseEntity<>(loginUserModel,HttpStatus.OK);
			}else{
				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Firmanız bloklanmış lütfen destek ekibine başvurunuz.");
				loginUserModel.setBaseModel(baseModel);
				return new ResponseEntity<>(loginUserModel,HttpStatus.BAD_REQUEST);
			}

		}catch(Exception e){
			LOG.error("Login Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Giriş yapılırken hata oluştu, lütfen daha sonra tekrar deneyiniz.");
			loginUserModel.setBaseModel(baseModel);
			return new ResponseEntity<>(loginUserModel,HttpStatus.BAD_REQUEST);
		}
	}



	public static void setRole(UserDetailModel userDetailModel, User user) {

		Set<UserRole> userRoles=user.getUserRoles();
		ArrayList<String> roleNames=new ArrayList<>();
		for (UserRole userRole:userRoles) {
			roleNames.add(userRole.getRole().getName());
		}

		if (roleNames.contains(ConstRole.ARG_ADMIN_ROLE)) {
			userDetailModel.setRole(ConstRole.ROLE_VALUE_ADMIN);
		}else if(roleNames.contains(ConstRole.ARG_SUPER_ROLE)){
			userDetailModel.setRole(ConstRole.ROLE_VALUE_SUPER_ADMIN);
		}else if(roleNames.contains(ConstRole.ARG_USER_ROLE)){
			userDetailModel.setRole(ConstRole.ROLE_VALUE_USER);
		}else{
			userDetailModel.setRole(ConstRole.ROLE_VALUE_TAILOR);
		}
	}


}