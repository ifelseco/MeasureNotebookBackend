package com.javaman.controller;

import com.javaman.entity.*;
import com.javaman.model.BaseModel;
import com.javaman.model.NotificationDetailModel;
import com.javaman.model.NotificationSummaryModel;
import com.javaman.service.NotificationService;
import com.javaman.service.UserNotificationService;
import com.javaman.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/notification")
@Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_TAILOR"})
public class NotificationController {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);
    private UserService userService;
    private UserNotificationService userNotificationService;
    private NotificationService notificationService;
    private ModelMapper modelMapper;

    @Autowired
    public NotificationController(UserService userService,UserNotificationService userNotificationService,
                                  NotificationService notificationService,ModelMapper modelMapper) {
        this.userService=userService;
        this.userNotificationService=userNotificationService;
        this.notificationService=notificationService;
        this.modelMapper=modelMapper;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<NotificationSummaryModel> getNotification(Principal principal) {
        BaseModel baseModel = new BaseModel();
        NotificationSummaryModel notificationSummaryModel = new NotificationSummaryModel();
        notificationSummaryModel.setNotificationDetailModelList(new ArrayList<NotificationDetailModel>());

        try {
            User user = userService.findByUsername(principal.getName());

            if (user != null) {
                Tenant tenant = user.getTenant();

                List<UserNotification> userNotificationList = userNotificationService.findByTenantAndUser(tenant, user);

                for (UserNotification userNotification : userNotificationList) {
                    NotificationDetailModel notificationDetailModel = new NotificationDetailModel();
                    notificationDetailModel = modelMapper.map(userNotification.getNotification(), NotificationDetailModel.class);
                    notificationSummaryModel.getNotificationDetailModelList().add(notificationDetailModel);
                }

                Collections.reverse(notificationSummaryModel.getNotificationDetailModelList());

                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Bildirimler başarıyla listelendi.");
                notificationSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(notificationSummaryModel, HttpStatus.OK);

            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Kullanıcı kayıtlarda yok");
                notificationSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(notificationSummaryModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Notification List Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Bildirimler listelenirken hata oluştu");
            notificationSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(notificationSummaryModel, HttpStatus.BAD_REQUEST);
        }


    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseModel> deleteNotification(@PathVariable long id, Principal principal) {

        BaseModel baseModel = new BaseModel();

        try {
            User user = userService.findByUsername(principal.getName());

            if (user != null) {
                Tenant tenant = user.getTenant();
                Notification notification = notificationService.findOne(id);
                if (notification != null) {

                    UserNotification userNotification = userNotificationService.findByTenantAndNotificationAndUser(tenant, notification, user);

                    if (userNotification != null) {


                        notification.getUserNotifications().remove(userNotification);
                        notification=notificationService.save(notification);

                        if(notification.getUserNotifications().size()==0){
                            notificationService.remove(notification.getId());
                        }


                        baseModel.setResponseCode(0);
                        baseModel.setResponseMessage("Bildirim başarıyla silindi.");
                        return new ResponseEntity<>(baseModel, HttpStatus.OK);
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Bildirim zaten silinmiş.");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }

                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Bildirim zaten silinmiş.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Kullanıcı bulunamadı.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Notification Delete Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Silme işlemi sırasında bir hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/list", method = RequestMethod.DELETE)
    public ResponseEntity<BaseModel> deleteAll(Principal principal) {

        BaseModel baseModel = new BaseModel();

        try {

            User user = userService.findByUsername(principal.getName());

            if (user != null) {
                Tenant tenant = user.getTenant();
                List<UserNotification> userNotifications = userNotificationService.findByTenantAndUser(tenant, user);
                List<Notification> notifications=new ArrayList<>();
                HashMap<UserNotification,Notification> map=new HashMap<>();
                for (UserNotification userNotification:userNotifications) {
                    map.put(userNotification,userNotification.getNotification());
                }

                map.forEach((mUserNotification,mNotification)->mNotification.getUserNotifications().remove(mUserNotification));
                map.forEach((mUserNotification,mNotification)->notifications.add(mNotification));
                notificationService.save(notifications);

                for (Notification notification:notifications) {
                    List<UserNotification> userNotificationList=userNotificationService.findByTenantAndNotification(tenant,notification);
                    if(userNotificationList.size()==0){
                        notificationService.remove(notification.getId());
                    }
                }


                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Tüm bildirimler silindi");
                return new ResponseEntity<>(baseModel, HttpStatus.OK);



            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Kullanıcı kayıtlarda yok");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Notification Delete All Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Bildirim silme sırasında hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }

    }





}
