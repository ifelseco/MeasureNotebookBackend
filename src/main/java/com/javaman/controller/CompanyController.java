package com.javaman.controller;

import com.google.common.base.Strings;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.model.BaseModel;
import com.javaman.model.CompanyDetailModel;
import com.javaman.model.UserDetailModel;
import com.javaman.model.UserSummaryModel;
import com.javaman.security.UserRole;
import com.javaman.service.TenantService;
import com.javaman.service.UserService;
import com.javaman.util.ConstRole;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/company")
@Secured(ConstRole.ARG_ADMIN_ROLE)
public class CompanyController {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyController.class);
    private UserService userService;
    private TenantService tenantService;
    private ModelMapper modelMapper;

    @Autowired
    public CompanyController(UserService userService,TenantService tenantService,ModelMapper modelMapper) {
        this.tenantService=tenantService;
        this.modelMapper=modelMapper;
        this.userService=userService;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<UserSummaryModel> getCompanyUsers(Principal principal) {
        UserSummaryModel userSummaryModel = new UserSummaryModel();
        BaseModel baseModel = new BaseModel();

        try {
            User adminUser = userService.findByUsername(principal.getName());
            if (adminUser != null) {

                List<User> tenantUsers = userService.findByTenant(adminUser.getTenant());

                for (User user : tenantUsers) {
                    UserDetailModel userDetailModel;

                    if(user.isEnabled()){
                        userDetailModel = modelMapper.map(user, UserDetailModel.class);
                        ArrayList<String> roleNames = getUserRoleNames(user);
                        if (roleNames.contains(ConstRole.ARG_ADMIN_ROLE)) {
                            userDetailModel.setRole("r1");
                        } else if (roleNames.contains(ConstRole.ARG_TAILOR_ROLE)) {
                            userDetailModel.setRole("r3");
                        } else if (roleNames.contains(ConstRole.ARG_USER_ROLE)) {
                            userDetailModel.setRole("r2");
                        }

                        userSummaryModel.getUsers().add(userDetailModel);
                    }



                }

                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Kullanıcılar başarıyla listelendi");
                userSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(userSummaryModel, HttpStatus.OK);


            } else {
                baseModel.setResponseMessage("Hata : Kullanıcı bulunamadı,tekrar giriş yapınız!");
                baseModel.setResponseCode(1);
                userSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(userSummaryModel, HttpStatus.BAD_REQUEST);

            }
        } catch (Exception e) {
            LOG.error("Company Users Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseMessage("Hata : Kullanıcılar listelenirken hata oluştu.");
            baseModel.setResponseCode(1);
            userSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(userSummaryModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<BaseModel> updateCompany(@RequestBody CompanyDetailModel companyDetailModel, Principal principal) {
        BaseModel baseModel = new BaseModel();

        try {

            User user = userService.findByUsername(principal.getName());
            if (user != null) {

                Tenant tenant = user.getTenant();

                if (tenant != null) {

                    if (!Strings.isNullOrEmpty(companyDetailModel.getTenantName())) {
                        tenant.setTenantName(companyDetailModel.getTenantName());
                    }

                    if (!Strings.isNullOrEmpty(companyDetailModel.getAddress())) {
                        tenant.setAddress(companyDetailModel.getAddress());
                    }

                    if (!Strings.isNullOrEmpty(companyDetailModel.getPhone())) {
                        tenant.setPhone(companyDetailModel.getPhone());
                    }

                    if (!Strings.isNullOrEmpty(companyDetailModel.getEmail())) {
                        tenant.setEmail(companyDetailModel.getEmail());
                    }


                    tenantService.createTenant(tenant);
                    baseModel.setResponseMessage("Firma bilgileri başarıyla güncelendi");
                    baseModel.setResponseCode(0);
                    return new ResponseEntity<>(baseModel, HttpStatus.OK);

                } else {
                    baseModel.setResponseMessage("Hata : Firma bulunamadı");
                    baseModel.setResponseCode(1);
                    return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
                }


            } else {
                baseModel.setResponseMessage("Hata : Kullanıcı bulunamadı");
                baseModel.setResponseCode(1);
                return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            LOG.error("Company Update Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseMessage("Hata : Firma bilgieri güncellenriken hata oluştu.");
            baseModel.setResponseCode(1);
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }


    public ArrayList<String> getUserRoleNames(User user) {
        Set<UserRole> userRoles = user.getUserRoles();

        ArrayList<String> roleNames = new ArrayList<>();

        for (UserRole userRole : userRoles) {
            roleNames.add(userRole.getRole().getName());
        }

        return roleNames;
    }

}
