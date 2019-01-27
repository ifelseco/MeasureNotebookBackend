package com.javaman.controller;

import com.google.common.base.Strings;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.model.*;
import com.javaman.security.Role;
import com.javaman.security.SecurityUtility;
import com.javaman.security.UserRole;
import com.javaman.service.RoleService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@Secured(ConstRole.ARG_SUPER_ROLE)
@RequestMapping("/admin")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private UserService userService;
    private TenantService tenantService;
    private RoleService roleService;

    @Autowired
    public AdminController(UserService userService,TenantService tenantService,RoleService roleService) {
        this.tenantService=tenantService;
        this.roleService=roleService;
        this.userService=userService;
    }

    @RequestMapping("/token")
    public ResponseEntity<LoginUserModel> token(HttpSession session, HttpServletRequest request) {
        LoginUserModel loginUserModel = new LoginUserModel();
        BaseModel baseModel = new BaseModel();

        try {
            String remoteHost = request.getRemoteHost();
            int portNumber = request.getRemotePort();
            LOG.info("Host : Port :", "" + remoteHost + " : " + portNumber);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            loginUserModel.setToken(session.getId());
            UserDetailModel userDetailModel = new UserDetailModel();
            userDetailModel.setUsername(user.getUsername());
            LoginController.setRole(userDetailModel, user);
            loginUserModel.setUserDetailModel(userDetailModel);

            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Süper Admin Girişi Başarılı.");
            loginUserModel.setBaseModel(baseModel);

            return new ResponseEntity<>(loginUserModel, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Admin Login Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Giriş yapılırken hata oluştu, lütfen daha sonra tekrar deneyiniz.");
            loginUserModel.setBaseModel(baseModel);
            return new ResponseEntity<>(loginUserModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/checkSession")
    public ResponseEntity<UserDetailModel> checkSession() {
        BaseModel baseModel = new BaseModel();
        UserDetailModel userDetailModel = new UserDetailModel();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            LoginController.setRole(userDetailModel, user);
            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Süper Admin Oturumu  Aktif!");
            userDetailModel.setBaseModel(baseModel);
            return new ResponseEntity<>(userDetailModel, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Check Session Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Oturum kontrol hatası,tekrar giriş yapınız.");
            userDetailModel.setBaseModel(baseModel);
            return new ResponseEntity<>(userDetailModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/company/add", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> createCompany(@RequestBody CompanyDetailModel companyDetailModel, Principal principal) {
        BaseModel baseModel = new BaseModel();
        try {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {

                if (companyDetailModel.getId() == null) {
                    Tenant tenant = new Tenant();
                    if (!Strings.isNullOrEmpty(companyDetailModel.getEmail())) {
                        tenant.setEmail(companyDetailModel.getEmail());
                    }

                    if (!Strings.isNullOrEmpty(companyDetailModel.getPhone())) {
                        tenant.setPhone(companyDetailModel.getPhone());
                    }

                    if (!Strings.isNullOrEmpty(companyDetailModel.getAddress())) {
                        tenant.setAddress(companyDetailModel.getAddress());
                    }

                    if (!Strings.isNullOrEmpty(companyDetailModel.getTenantName())) {
                        tenant.setTenantName(companyDetailModel.getTenantName());
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata: Firma ismi boş bırakılamaz.");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }

                    if (companyDetailModel.getMaxUserCount()>0) {
                        tenant.setMaxUserCount(companyDetailModel.getMaxUserCount());
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata: Firma kullanıcı sayısı zorunlu bir alandır.");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }

                    tenant = tenantService.createTenant(tenant);
                    baseModel.setData(tenant.getId());
                    baseModel.setResponseMessage("Firma Ekleme İşlemi Başarılı");
                    return new ResponseEntity<>(baseModel, HttpStatus.OK);
                } else {
                    Tenant tenant = tenantService.findOne(companyDetailModel.getId());
                    if (tenant != null) {

                        if (!Strings.isNullOrEmpty(companyDetailModel.getEmail())) {
                            tenant.setEmail(companyDetailModel.getEmail());
                        }

                        if (!Strings.isNullOrEmpty(companyDetailModel.getPhone())) {
                            tenant.setPhone(companyDetailModel.getPhone());
                        }

                        if (!Strings.isNullOrEmpty(companyDetailModel.getAddress())) {
                            tenant.setAddress(companyDetailModel.getAddress());
                        }

                        if (!Strings.isNullOrEmpty(companyDetailModel.getTenantName())) {
                            tenant.setTenantName(companyDetailModel.getTenantName());
                        }

                        if(companyDetailModel.getMaxUserCount()>0){
                            if(tenant.getMaxUserCount()!=companyDetailModel.getMaxUserCount()){
                                tenant.setMaxUserCount(companyDetailModel.getMaxUserCount());
                            }
                        }

                        tenant = tenantService.createTenant(tenant);
                        baseModel.setData(tenant.getId());
                        baseModel.setResponseMessage("Firma Güncelleme İşlemi Başarılı");
                        return new ResponseEntity<>(baseModel, HttpStatus.OK);
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata: Verilen id'de firma bulunamadı.");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }
                }

            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Kullanıcı bulunamadı tekrar giriş yapınız.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Company Add Error : "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Firma oluşturulurken hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> registerAdminUser(@RequestBody UserRegisterModel userRegisterModel, Principal principal) {
        BaseModel baseModel = new BaseModel();
        try {

            if (!Strings.isNullOrEmpty(userRegisterModel.getEmail())) {
                User adminUser = userService.findByUsername(principal.getName());
                if (adminUser != null) {

                    if (userRegisterModel.getCompanyDetailModel() != null && userRegisterModel.getCompanyDetailModel().getId() != null) {

                        Tenant tenant = tenantService.findOne(userRegisterModel.getCompanyDetailModel().getId());
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
                                    Role adminRole = roleService.findByName(ConstRole.ARG_ADMIN_ROLE);
                                    if (adminRole != null) {
                                        roles.add(new UserRole(newUser, adminRole));
                                    } else {
                                        baseModel.setResponseCode(1);
                                        baseModel.setResponseMessage("Hata : Admin rolünde kullanıcı eklerken hata oluştu.");
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
                            baseModel.setResponseMessage("Hata : Önce firma kaydı yapmalısınız");
                            baseModel.setResponseCode(1);
                            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata : Firma bilgileri eksik , tekrar deneyiniz.");
                        return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                    }

                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata : Kullanıcı bulunamadı, tekrar giriş yapınız.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata : E-posta zorunlu bir alandır.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("User Add Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata : Kullanıcı eklerken hata oluştu");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/company/list", method = RequestMethod.GET)
    public ResponseEntity<CompanySummaryModel> getAllCompany(Principal principal) {
        CompanySummaryModel companySummaryModel = new CompanySummaryModel();
        CompanyDetailModel companyDetailModel = new CompanyDetailModel();
        UserDetailModel userDetailModel;
        BaseModel baseModel = new BaseModel();
        ModelMapper mapper = new ModelMapper();
        try {

            User mUser = userService.findByUsername(principal.getName());
            if (mUser != null) {

                List<Tenant> tenants = tenantService.findAll();
                if (tenants.size() > 0) {
                    for (Tenant tenant : tenants) {
                        companyDetailModel = mapper.map(tenant, CompanyDetailModel.class);
                        List<User> users = userService.findByTenant(tenant);

                        if (users.size() > 0) {
                            for (User user : users) {
                                userDetailModel = mapper.map(user, UserDetailModel.class);
                                LoginController.setRole(userDetailModel, user);
                                companyDetailModel.getUsers().add(userDetailModel);
                            }
                        }
                        companySummaryModel.getCompanies().add(companyDetailModel);

                    }

                    baseModel.setResponseMessage("Firma Listeleme Başarılı.");
                    baseModel.setResponseCode(0);
                    companySummaryModel.setBaseModel(baseModel);
                    return new ResponseEntity<>(companySummaryModel, HttpStatus.OK);
                } else {
                    baseModel.setResponseMessage("Şuanda hiç firma kayıtlı değil");
                    baseModel.setResponseCode(0);
                    companySummaryModel.setBaseModel(baseModel);
                    return new ResponseEntity<>(companySummaryModel, HttpStatus.OK);
                }


            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Kullanıcı bulunamadı , tekrar giriş yapınız..");
                companySummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(companySummaryModel, HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            LOG.error("Company List Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Firmalar listelenirken hata oluştu.");
            companySummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(companySummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/company/block", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> blockCompany(@RequestBody CompanyDetailModel companyDetailModel, Principal principal) {
        BaseModel baseModel = new BaseModel();

        try {

            User mUser = userService.findByUsername(principal.getName());
            if (mUser != null) {

                if (companyDetailModel != null && companyDetailModel.getId() != null) {
                    Tenant tenant = tenantService.findOne(companyDetailModel.getId());
                    if (tenant != null) {
                        tenant.setEnabled(false);
                        tenantService.createTenant(tenant);
                        baseModel.setResponseCode(0);
                        baseModel.setResponseMessage("Firma başarıyla bloklandı.");
                        return new ResponseEntity<>(baseModel, HttpStatus.OK);
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata: Firma bulunamadı , daha önce silinmiş olabilir");
                        return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
                    }
                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata: Firma bilgileri eksik tekrar deneyiniz.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }

            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Kullanıcı bulunamadı , tekrar giriş yapınız..");
                return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            LOG.error("Company Block Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Firma bloklarken hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/company/unblock", method = RequestMethod.POST)
    public ResponseEntity<BaseModel> unBlockCompany(@RequestBody CompanyDetailModel companyDetailModel, Principal principal) {
        BaseModel baseModel = new BaseModel();

        try {

            User mUser = userService.findByUsername(principal.getName());
            if (mUser != null) {

                if (companyDetailModel != null && companyDetailModel.getId() != null) {
                    Tenant tenant = tenantService.findOne(companyDetailModel.getId());
                    if (tenant != null) {
                        tenant.setEnabled(true);
                        tenantService.createTenant(tenant);
                        baseModel.setResponseCode(0);
                        baseModel.setResponseMessage("Firma aktif hale getirildi.");
                        return new ResponseEntity<>(baseModel, HttpStatus.OK);
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Hata: Firma bulunamadı , daha önce silinmiş olabilir");
                        return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
                    }
                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata: Firma bilgileri eksik tekrar deneyiniz.");
                    return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
                }

            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Kullanıcı bulunamadı , tekrar giriş yapınız..");
                return new ResponseEntity<>(baseModel, HttpStatus.NOT_FOUND);
            }


        } catch (Exception e) {
            LOG.error("Company Unblock Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Firma bloklarken hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public ResponseEntity<UserDetailModel> getActiveUser(Principal principal) {
        UserDetailModel userDetailModel = new UserDetailModel();
        ModelMapper modelMapper = new ModelMapper();
        BaseModel baseModel = new BaseModel();
        try {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                userDetailModel = modelMapper.map(user, UserDetailModel.class);
                userDetailModel.setUsername("");
                LoginController.setRole(userDetailModel, user);
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Kullanıcı isteği başarılı");
                userDetailModel.setBaseModel(baseModel);
                return new ResponseEntity<>(userDetailModel, HttpStatus.OK);
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata : Kullanıcı bulunamadı");
                userDetailModel.setBaseModel(baseModel);
                return new ResponseEntity<>(userDetailModel, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Active User Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Kullanıcı bulunurken hata oluştu,tekrar giriş yapınız.");
            userDetailModel.setBaseModel(baseModel);
            return new ResponseEntity<>(userDetailModel, HttpStatus.BAD_REQUEST);
        }
    }


}
