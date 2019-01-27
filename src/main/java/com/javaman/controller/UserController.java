package com.javaman.controller;

import com.google.common.base.Strings;
import com.javaman.entity.PasswordResetToken;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.model.*;
import com.javaman.security.Role;
import com.javaman.security.SecurityUtility;
import com.javaman.security.UserRole;
import com.javaman.service.*;
import com.javaman.util.ConstApp;
import com.javaman.util.ConstRole;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.access.annotation.Secured;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;



import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private RoleService roleService;
    private TenantService tenantService;
    private EmailService emailService;
    private PasswordResetTokenService passwordResetTokenService;
    private ModelMapper modelMapper;

    @Autowired
    public UserController(ModelMapper modelMapper,RoleService roleService,UserService userService,
                          PasswordResetTokenService passwordResetTokenService,EmailService emailService,TenantService tenantService) {
        this.passwordResetTokenService=passwordResetTokenService;
        this.userService=userService;
        this.roleService=roleService;
        this.emailService=emailService;
        this.tenantService=tenantService;
        this.modelMapper=modelMapper;
    }




    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<BaseModel> updateUser(@RequestBody UserDetailModel userDetailModel, Principal principal) {

        BaseModel baseModel = new BaseModel();

        try {

            User user = userService.findByUsername(principal.getName());

            if (user != null) {

                if (userDetailModel.getId() != null) {

                    User mUser = userService.findOne(userDetailModel.getId());

                    if (mUser != null && user.getId() == mUser.getId()) {


                        if (!Strings.isNullOrEmpty(userDetailModel.getEmail()) && !userDetailModel.getEmail().equals(mUser.getEmail())) {

                            User checkUser=userService.findByEmail(userDetailModel.getEmail());

                            if(checkUser!=null){
                                baseModel.setResponseCode(1);
                                baseModel.setResponseMessage("Hata: Bu eposta başka bir kullanıcıya ait.");
                                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                            }else{
                                mUser.setEmail(userDetailModel.getEmail());
                                mUser.setUsername(userDetailModel.getEmail());
                            }
                        }

                        if (!Strings.isNullOrEmpty(userDetailModel.getPhone())) {
                            mUser.setPhone(userDetailModel.getPhone());
                        }

                        if (!Strings.isNullOrEmpty(userDetailModel.getNameSurname())) {
                            mUser.setNameSurname(userDetailModel.getNameSurname());
                        }


                        mUser=userService.update(mUser);
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
                        updatedAuthorities.addAll(mUser.getAuthorities());
                        Authentication newAuth = new UsernamePasswordAuthenticationToken(mUser.getUsername(), mUser.getPassword(), updatedAuthorities);
                        SecurityContextHolder.getContext().setAuthentication(newAuth);
                        baseModel.setResponseCode(0);
                        baseModel.setResponseMessage("Kullanıcı başarıyla güncellendi");
                        return new ResponseEntity<>(baseModel, HttpStatus.OK);
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Güncelleme hatası , tekrar deneyiniz.");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }


                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Güncelleme hatası , tekrar deneyiniz.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }


            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Kullanıcı kayıtlarda yok tekrar giriş yap.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("User Update Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Kullanıcı güncelelnirken hata oluştu");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }


    }


    @RequestMapping(value = "/update/password", method = RequestMethod.PUT)
    public ResponseEntity<BaseModel> changePassword(@RequestHeader("update-password") String updatePasswordToken, Principal principal) {
        BaseModel baseModel = new BaseModel();
        try {

            User user = userService.findByUsername(principal.getName());

            if (user != null) {
                if (updatePasswordToken != null && updatePasswordToken.startsWith("Basic")) {
                    String base64Data = updatePasswordToken.substring("Basic".length()).trim();
                    String data = new String(Base64.getDecoder().decode(base64Data), Charset.forName("UTF-8"));
                    final String[] values = data.split(":", 2);
                    String oldPassword = values[0];
                    String newPassword = values[1];

                    if (!Strings.isNullOrEmpty(oldPassword)) {

                        if (SecurityUtility.passwordEncoder().matches(oldPassword, user.getPassword())) {
                            if (!Strings.isNullOrEmpty(newPassword)) {
                                if (newPassword.length() >= 6 && newPassword.length() <= 10) {

                                    String encodedNewPassword = SecurityUtility.passwordEncoder().encode(newPassword);
                                    user.setPassword(encodedNewPassword);
                                    userService.update(user);
                                    baseModel.setResponseCode(0);
                                    baseModel.setResponseMessage("Parola değiştirme başarılı.");
                                    return new ResponseEntity<>(baseModel, HttpStatus.OK);
                                } else {
                                    if (newPassword.length() < 6) {
                                        baseModel.setResponseCode(1);
                                        baseModel.setResponseMessage("Parola en az 6 karakter olmalı!");
                                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                    } else {
                                        baseModel.setResponseCode(1);
                                        baseModel.setResponseMessage("Parola en fazla 10 karakter olmalı!");
                                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                    }
                                }
                            } else {
                                baseModel.setResponseCode(1);
                                baseModel.setResponseMessage("Yeni parola girmediniz!");
                                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);

                            }
                        } else {
                            baseModel.setResponseCode(1);
                            baseModel.setResponseMessage("Eski parolanız hatalı,lütfen doğru giriniz!");
                            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);

                        }
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Eski parolanızı girmediniz!");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }


                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Parola değiştirme işleme gerçekleştirilemedi ,bir hata oluştu!");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata, kullanıcı bulunamadı tekrar giriş yapınız!");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("User Update Password Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Parola güncellenirken beklenmedik bir hata oluştu!");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public ResponseEntity<CompanyUserModel> getActiveUser(Principal principal) {
        UserDetailModel userDetailModel = new UserDetailModel();
        CompanyDetailModel companyDetailModel;
        CompanyUserModel companyUserModel = new CompanyUserModel();
        BaseModel baseModel = new BaseModel();
        try {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                userDetailModel = modelMapper.map(user, UserDetailModel.class);
                userDetailModel.setUsername("");
                LoginController.setRole(userDetailModel, user);
                companyDetailModel = modelMapper.map(user.getTenant(), CompanyDetailModel.class);
                companyUserModel.setUserDetailModel(userDetailModel);
                companyUserModel.setCompanyDetailModel(companyDetailModel);
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Kullanıcı isteği başarılı");
                userDetailModel.setBaseModel(baseModel);
                return new ResponseEntity<>(companyUserModel, HttpStatus.OK);
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata : Kullanıcı bulunamadı");
                userDetailModel.setBaseModel(baseModel);
                return new ResponseEntity<>(companyUserModel, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("User Active Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Kullanıcı bulunurken hata oluştu,tekrar giriş yapınız.");
            userDetailModel.setBaseModel(baseModel);
            return new ResponseEntity<>(companyUserModel, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> register(@RequestBody UserRegisterModel userRegisterModel, Principal principal) {
        BaseModel baseModel = new BaseModel();
        try {

            if (!Strings.isNullOrEmpty(userRegisterModel.getEmail())) {
                User adminUser = userService.findByUsername(principal.getName());
                if (adminUser != null) {

                    Tenant tenant = tenantService.findOne(adminUser.getTenant().getId());
                    if (tenant != null) {

                        User mUser = userService.findByUsername(userRegisterModel.getEmail());
                        if (mUser != null) {
                            baseModel.setResponseCode(1);
                            baseModel.setResponseMessage("Hata : Bu e-posta adresi daha öceden kayıt edilmiş.");
                            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                        } else {
                            int tenantUserCount = tenant.getTenantUserCount();
                            int maxUserCount=tenant.getMaxUserCount();
                            if (tenantUserCount < maxUserCount) {

                                tenantUserCount = tenant.getTenantUserCount();
                                tenantUserCount++;
                                tenant.setTenantUserCount(tenantUserCount);
                                tenant = tenantService.createTenant(tenant);


                                User newUser = new User();
                                newUser.setTenant(tenant);
                                newUser.setEmail(userRegisterModel.getEmail());
                                newUser.setUsername(userRegisterModel.getEmail());

                                if (!Strings.isNullOrEmpty(userRegisterModel.getPassword())) {
                                    String hashedPassword = SecurityUtility.passwordEncoder().encode(userRegisterModel.getPassword());
                                    newUser.setPassword(hashedPassword);
                                } else {
                                    baseModel.setResponseCode(1);
                                    baseModel.setResponseMessage("Hata : Parola alanı boş bırakılamaz.");
                                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                }

                                if (!Strings.isNullOrEmpty(userRegisterModel.getPhone())) {
                                    newUser.setPhone(userRegisterModel.getPhone());
                                }


                                if (!Strings.isNullOrEmpty(userRegisterModel.getNameSurname())) {
                                    newUser.setNameSurname(userRegisterModel.getNameSurname());
                                } else {
                                    baseModel.setResponseCode(1);
                                    baseModel.setResponseMessage("Hata : İsim soyisim boş bırakılamaz.");
                                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                }


                                Set<UserRole> roles = new HashSet<>();
                                if (userRegisterModel.getRole() == 2) {
                                    Role role = roleService.findByName(ConstRole.ARG_USER_ROLE);
                                    if (createUserRole(baseModel, newUser, roles, role)) {
                                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                    }


                                } else if (userRegisterModel.getRole() == 3) {
                                    Role role = roleService.findByName(ConstRole.ARG_TAILOR_ROLE);
                                    if (createUserRole(baseModel, newUser, roles, role)) {
                                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                    }

                                } else {
                                    baseModel.setResponseCode(1);
                                    baseModel.setResponseMessage("Hata : Kullanıcı türü tanımlı değil, tekrar deneyiniz");
                                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                }

                                newUser = userService.createUser(newUser, roles);
                                baseModel.setResponseCode(0);
                                baseModel.setResponseMessage("Kullanıcı kayıt işlemi başarılı.");
                                baseModel.setData(newUser.getId());

                                return new ResponseEntity<>(baseModel, HttpStatus.OK);

                            } else {
                                baseModel.setResponseCode(1);
                                baseModel.setResponseMessage("Hata : Kullanıcı sınırına ulaştın.En fazla "+maxUserCount +" kullanıcı ekleyebilirsin.");
                                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                            }
                        }
                    } else {
                        baseModel.setResponseMessage("Hata :Firma bulunamadı,önce firma kaydı yapmalısınız");
                        baseModel.setResponseCode(1);
                        return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
                    }


                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata : Kullanıcı bulunamadı, tekrar giriş yapınız.");
                    return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
                }
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata : E-posta zorunlu bir alandır.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("User Register Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata : Kullnıcı eklerken hata oluştu");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<BaseModel> deleteUser(@RequestParam(name = "userId") long userId, Principal principal) {
        BaseModel baseModel = new BaseModel();
        try {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                User deletetingUser = userService.findOne(userId);
                if (deletetingUser != null) {

                    deletetingUser.setEnabled(false);
                    deletetingUser.setPhone("");
                    deletetingUser.setPassword("");
                    deletetingUser.setEmail("");
                    deletetingUser.setUsername("");
                    deletetingUser.setLastFirebaseWebRegId("");
                    deletetingUser.setLastFirebaseRegId("");
                    Tenant tenant = user.getTenant();
                    int tenantUserCount = tenant.getTenantUserCount();
                    tenantUserCount--;
                    tenant.setTenantUserCount(tenantUserCount);
                    tenantService.createTenant(tenant);


                    userService.update(deletetingUser);

                    baseModel.setResponseMessage("Kullanıcı başarıyla silindi");
                    baseModel.setResponseCode(1);
                    return new ResponseEntity<>(baseModel, HttpStatus.OK);

                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Silmek istediğiniz kullanıcı kayıtlarda yok.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Oturumuznuz geçersiz tekrar giriş yapınız.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("User Delete Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Kullanıcı silerken beklenmeyen bir hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/forget-password", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> sendResetPasswordMail(@RequestBody PasswordForgatModel passwordForgatModel) {
        BaseModel baseModel = new BaseModel();
        try {

            if (!Strings.isNullOrEmpty(passwordForgatModel.getEmail())) {
                User user = userService.findByEmail(passwordForgatModel.getEmail());
                if (user != null) {
                    PasswordResetToken token = new PasswordResetToken();
                    token.setToken(UUID.randomUUID().toString());
                    token.setUser(user);
                    token.setExpiryDate(5);
                    passwordResetTokenService.save(token);

                    EmailModel mail=new EmailModel();
                    mail.setFrom(ConstApp.FROM_EMAIL);
                    mail.setTo(user.getEmail());
                    mail.setSubject("Parola sıfırlama");

                    Map<String, Object> model = new HashMap<>();
                    model.put("token", token);
                    model.put("user", user);
                    model.put("signature", "Akıllı Ölçü Defteri");
                    model.put("resetUrl", ConstApp.WEB_URL+"/register/password?token="+token.getToken());
                    mail.setModel(model);
                    emailService.resetPasswordEmail(mail);

                    baseModel.setResponseCode(0);
                    baseModel.setResponseMessage("Parola sıfırlama linki mail adresinize gönderildi");
                    return new ResponseEntity<>(baseModel, HttpStatus.OK);
                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata: Girdiğiniz mail adresi sistemde kayıtlı değil.");
                    return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
                }
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Mail adresi girmelisiniz.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Reset Password Mail Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Parola sıfırlama maili gönderilirken beklenmedik bir hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> resetPassword(@RequestHeader("reset-password-token") String token,@RequestHeader("new-password") String resetPasswordToken) {
        BaseModel baseModel = new BaseModel();
        try {
            PasswordResetToken resetToken = passwordResetTokenService.findByToken(token);
            if (resetToken == null){
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Şifre sıfırlama anahtarı bulunamadı.!");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);

            } else if (resetToken.isExpired()){
                passwordResetTokenService.delete(resetToken);
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Anahtarın süresi doldu, lütfen yeni bir şifre sıfırlama isteğinde bulunun.!");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            } else {

                User user = resetToken.getUser();

                if (user != null) {

                    if (resetPasswordToken != null && resetPasswordToken.startsWith("Basic")) {
                        String base64Data = resetPasswordToken.substring("Basic".length()).trim();
                        String data = new String(Base64.getDecoder().decode(base64Data), Charset.forName("UTF-8"));
                        final String[] values = data.split(":", 2);
                        String email = values[0];
                        String newPassword = values[1];

                        if (!Strings.isNullOrEmpty(email)) {
                            User mUser = userService.findByEmail(email);
                            if (mUser != null && mUser == user) {
                                if (!Strings.isNullOrEmpty(newPassword)) {
                                    if (newPassword.length() >= 6 && newPassword.length() <= 10) {
                                        String encodedNewPassword = SecurityUtility.passwordEncoder().encode(newPassword);
                                        user.setPassword(encodedNewPassword);
                                        userService.update(user);
                                        passwordResetTokenService.delete(resetToken);
                                        baseModel.setResponseCode(0);
                                        baseModel.setResponseMessage("Parola sıfırlama başarılı.");
                                        return new ResponseEntity<>(baseModel, HttpStatus.OK);
                                    } else {
                                        if (newPassword.length() < 6) {
                                            baseModel.setResponseCode(1);
                                            baseModel.setResponseMessage("Hata: Parola en az 6 karakter olmalı!");
                                            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                        } else {
                                            baseModel.setResponseCode(1);
                                            baseModel.setResponseMessage("Hata: Parola en fazla 10 karakter olmalı!");
                                            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                        }
                                    }
                                } else {
                                    baseModel.setResponseCode(1);
                                    baseModel.setResponseMessage("Hata: Yeni parola boş bıraklılamaz.");
                                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                                }


                            } else {
                                baseModel.setResponseCode(1);
                                baseModel.setResponseMessage("Hata: E-posta adresi yanlış yada oturum zaman aşımına uğaramnış olabilir. ");
                                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                            }


                        } else {
                            baseModel.setResponseCode(1);
                            baseModel.setResponseMessage("Hata: E-posta boş bırakılamaz!");
                            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata: Parola değiştirme işleme gerçekleştirilemedi ,bir hata oluştu!");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);


                    }
                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata: Kullanıcı bulunamadı oturum zaman aşımına uğaramnış olabilir.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }
            }


        } catch (Exception e) {
            LOG.error("Password Reset Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Parola sıfırlanırken beklenmedik bir hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    public boolean createUserRole(BaseModel baseModel, User newUser, Set<UserRole> roles, Role role) {
        if (role != null) {
            roles.add(new UserRole(newUser, role));

        } else {
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata : Kullanıcı türü tanımlı değil, tekrar deneyiniz");
            return true;
        }
        return false;
    }
}


