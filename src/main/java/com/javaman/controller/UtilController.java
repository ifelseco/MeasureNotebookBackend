package com.javaman.controller;

import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.model.AppUtilInfoModel;
import com.javaman.model.BaseModel;

import com.javaman.service.UserNotificationService;
import com.javaman.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/util")
public class UtilController {

    private static final Logger LOG = LoggerFactory.getLogger(UtilController.class);
    private UserService userService;
    private UserNotificationService userNotificationService;

    @Autowired
    public UtilController(UserService userService,UserNotificationService userNotificationService) {
        this.userNotificationService=userNotificationService;
        this.userService=userService;
    }

    @RequestMapping(value = "/android", method = RequestMethod.GET)
    public ResponseEntity<AppUtilInfoModel> getAppUtilInfo(Principal principal) {
        AppUtilInfoModel infoModel=new AppUtilInfoModel();
        BaseModel baseModel=new BaseModel();
        try {
            User user = userService.findByUsername(principal.getName());

            if (user != null) {
                Tenant tenant = user.getTenant();
                int count = userNotificationService.findNotificationCountByTenantAndUser(tenant, user);
                baseModel.setResponseMessage("İşlem başarılı");
                baseModel.setResponseCode(0);
                infoModel.setComapanyName(tenant.getTenantName());
                infoModel.setUserNameSurname(user.getNameSurname());
                infoModel.setBaseModel(baseModel);
                infoModel.setCount(count);
                return new ResponseEntity<>(infoModel, HttpStatus.OK);


            } else {
                baseModel.setResponseMessage("İşlem sırasında hata!");
                baseModel.setResponseCode(1);
                infoModel.setBaseModel(baseModel);
                infoModel.setCount(-1);
                return new ResponseEntity<>(infoModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Android App Util Info Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseMessage("Hata: İşlem sırasında hata!!");
            baseModel.setResponseCode(1);
            infoModel.setBaseModel(baseModel);
            infoModel.setCount(-1);
            return new ResponseEntity<>(infoModel, HttpStatus.BAD_REQUEST);
        }


    }
}
