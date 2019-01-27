package com.javaman.controller;

import java.security.Principal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.model.BaseModel;
import com.javaman.model.FirebaseRegIdModel;
import com.javaman.service.UserService;

@RestController
@RequestMapping("/firebase")
public class FirebaseController {

	private static final Logger LOG = LoggerFactory.getLogger(FirebaseController.class);
	private UserService userService;

	@Autowired
	public FirebaseController(UserService userService) {
		this.userService=userService;
	}

	@RequestMapping(value = "/add/regId", method = RequestMethod.POST)
	public ResponseEntity<BaseModel> addRegToken(@RequestBody FirebaseRegIdModel regIdModel, Principal principal) {

		BaseModel baseModel = new BaseModel();


		try {
			User user = userService.findByUsername(principal.getName());		
			if (user != null) {
				
				Tenant tenant=user.getTenant();
				List<User> tenantUsers=userService.findByTenant(tenant);

				//Gelen reg id sadece bir usera ait olmalı
				//Aynı cihazdan başka userlar giriş yapmış olabilir
				//Gelen regId diğer userların reg id si ile eşleşiyorsa diğer user ların reg id si sıfırlanır
				// ve o reg id şuanda aktif  olan usera kayıt edilir.
				for (User mUser : tenantUsers) {
					if(mUser.getLastFirebaseRegId()!=null && mUser.getLastFirebaseRegId().equals(regIdModel.getRegistrationId())) {
						mUser.setLastFirebaseRegId("");
						userService.update(mUser);
					}

					if(mUser.getLastFirebaseWebRegId()!=null &&
							mUser.getLastFirebaseWebRegId().equals(regIdModel.getWebRegistrationId())){
						mUser.setLastFirebaseWebRegId("");
						userService.update(mUser);
					}
				}

				//

				if(regIdModel.getRegistrationId()!=null && !regIdModel.getRegistrationId().equals("")  ){
					user.setLastFirebaseRegId(regIdModel.getRegistrationId());
				}

				if(regIdModel.getWebRegistrationId()!=null && !regIdModel.getWebRegistrationId().equals("")  ){
					user.setLastFirebaseWebRegId(regIdModel.getWebRegistrationId());
				}

				userService.update(user);




				baseModel.setResponseCode(0);
				baseModel.setResponseMessage("Firebase registerId kaydı başarılı,bildirimler geldi");
				return new ResponseEntity<BaseModel>(baseModel,HttpStatus.OK);

			} else {

				baseModel.setResponseCode(1);
				baseModel.setResponseMessage("Firebase registerId kayıt edilemedi.Böyle bir kullanıcı yok");
				return new ResponseEntity<BaseModel>(baseModel,HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			LOG.error("Firebase regId save Error: "+e.getClass()+" "+e.getMessage());
			baseModel.setResponseCode(1);
			baseModel.setResponseMessage("Hata: Firebase registerId kayıt edilirken hata oluştu.");
			return new ResponseEntity<BaseModel>(baseModel,HttpStatus.BAD_REQUEST);
		}

	}

}
