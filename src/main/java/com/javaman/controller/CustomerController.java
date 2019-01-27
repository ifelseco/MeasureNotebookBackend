package com.javaman.controller;

import com.javaman.entity.*;
import com.javaman.model.*;
import com.javaman.service.*;
import org.apache.commons.text.WordUtils;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customer")
@Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_USER"})
public class CustomerController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);
    private OrderService orderService;
    private UserService userService;
    private CustomerService customerService;
    private UserRoleService userRoleService;
    private ModelMapper modelMapper;
    private NotificationService notificationService;
    private FirebaseNotificationService firebaseNotificationService;

    @Autowired
    public CustomerController(OrderService orderService,UserService userService,
                              CustomerService customerService,UserRoleService userRoleService,
                              ModelMapper modelMapper,NotificationService notificationService,
                              FirebaseNotificationService firebaseNotificationService) {
        this.orderService=orderService;
        this.userRoleService=userRoleService;
        this.userService=userService;
        this.customerService=customerService;
        this.modelMapper=modelMapper;
        this.notificationService=notificationService;
        this.firebaseNotificationService=firebaseNotificationService;

    }

    static void setUserNotification(Tenant tenant, List<User> adminUsers, Notification notification) {
        if (adminUsers.size() > 0) {
            for (User adminUser : adminUsers) {
                UserNotification userNotificationForAdmin = new UserNotification();
                userNotificationForAdmin.setNotification(notification);
                userNotificationForAdmin.setUser(adminUser);
                userNotificationForAdmin.setTenant(tenant);
                notification.getUserNotifications().add(userNotificationForAdmin);
            }
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<AddCustomerResultModel> addCustomer(@RequestBody AddCustomerModel addCustomerModel,
                                                              Principal principal) {

        BaseModel baseModel = new BaseModel();

        CustomerDetailModel customerDetailModel = addCustomerModel.getCustomerDetailModel();

        int orderStatus = addCustomerModel.getOrderStatus();

        AddCustomerResultModel addCustomerResultModel = new AddCustomerResultModel();



        try {

            User user = userService.findByUsername(principal.getName());

            if (user != null) {

                Tenant tenant = user.getTenant();
                List<User> adminUsers;
                adminUsers = userService.findTenantAdminUsers(tenant);
                ArrayList<String> roleNames = (ArrayList<String>) userRoleService.findUserRole(user);


                if (customerDetailModel.getId() != null) {

                    Customer customer = customerService.findOne(customerDetailModel.getId());

                    if (customer != null) {

                        customer = modelMapper.map(customerDetailModel, Customer.class);
                        customer.setTenant(tenant);

                        LOG.info("Customer", customer.toString());
                        customer = customerService.save(customer);


                        Order order = new Order();
                        order.setTenant(tenant);
                        order.setUser(user);
                        order.setDepositeAmount(new BigDecimal(0));
                        order.setTotalAmount(new BigDecimal(0));
                        order.setOrderDate(new Date());
                        order.setOrderStatus(OrderStatus.valueOf(orderStatus));
                        order.setCustomer(customer);

                        Order localOrder = order = orderService.save(order);
                        String orderNumber = "SN" + localOrder.getId();
                        localOrder.setOrderNumber(orderNumber);
                        Order savedOrder = orderService.save(localOrder);


                        //create notification and save it to notification table

                        createAdminNotification(baseModel, addCustomerResultModel, user, tenant, adminUsers, roleNames, savedOrder);


                        //sentNotification and readNotiication property should be exist
                        //keep created notification


                        baseModel.setResponseCode(0);
                        baseModel.setResponseMessage("Müşteri güncellendi,sipariş kaydı başarılı.");
                        addCustomerResultModel.setBaseModel(baseModel);
                        addCustomerResultModel.setCustomerId(customer.getId());
                        addCustomerResultModel.setCustomerNameSurname(customer.getNameSurname());
                        addCustomerResultModel.setId(order.getId());
                        addCustomerResultModel.setOrderDate(order.getOrderDate());
                        addCustomerResultModel.setOrderNumber(order.getOrderNumber());
                        return new ResponseEntity<>(addCustomerResultModel, HttpStatus.OK);

                    } else {

                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Bu müşteri kayıtlarda yok.");
                        addCustomerResultModel.setBaseModel(baseModel);
                        addCustomerResultModel.setCustomerId(customerDetailModel.getId());
                        return new ResponseEntity<>(addCustomerResultModel, HttpStatus.BAD_REQUEST);
                    }

                } else {

                    Customer customer = new Customer();
                    customer = modelMapper.map(customerDetailModel, Customer.class);
                    customer.setTenant(tenant);
                    LOG.info("Customerr {}", customer.toString());

                    customer = customerService.save(customer);

                    Order order = new Order();

                    order.setCustomer(customer);
                    order.setTenant(tenant);
                    order.setUser(user);
                    order.setOrderDate(new Date());
                    order.setDepositeAmount(new BigDecimal(0));
                    order.setTotalAmount(new BigDecimal(0));
                    order.setOrderStatus(OrderStatus.valueOf(orderStatus));

                    Order localOrder = order = orderService.save(order);
                    String orderNumber = "SN" + localOrder.getId();
                    localOrder.setOrderNumber(orderNumber);
                    Order savedOrder = orderService.save(localOrder);

                    createAdminNotification(baseModel, addCustomerResultModel, user, tenant, adminUsers, roleNames, savedOrder);


                    baseModel.setResponseCode(0);
                    baseModel.setResponseMessage("Yeni müşteri için yeni sipariş oluşturuldu.");
                    addCustomerResultModel.setBaseModel(baseModel);
                    addCustomerResultModel.setCustomerId(customer.getId());
                    addCustomerResultModel.setCustomerNameSurname(customer.getNameSurname());
                    addCustomerResultModel.setId(order.getId());
                    addCustomerResultModel.setOrderDate(order.getOrderDate());
                    addCustomerResultModel.setOrderNumber(order.getOrderNumber());
                    return new ResponseEntity<>(addCustomerResultModel, HttpStatus.OK);
                }


            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Kullanıcı bulunamadı");
                addCustomerResultModel.setBaseModel(baseModel);
                return new ResponseEntity<>(addCustomerResultModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Customer Add Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Sipariş kaydı sırasında hata oluştu");
            addCustomerResultModel.setBaseModel(baseModel);

            return new ResponseEntity<>(addCustomerResultModel, HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseEntity<CustomerSummaryPagedModel> getCustomers(@RequestBody PageModel pageModel, Principal principal) {

        BaseModel baseModel = new BaseModel();

        CustomerSummaryPagedModel customerSummaryPagedModel = new CustomerSummaryPagedModel();
        Page<CustomerDetailModel> customerDetailModelPages;

        try {

            User user = userService.findByUsername(principal.getName());

            Tenant tenant = user.getTenant();

            int pageNumber = pageModel.getFirst() / pageModel.getRows();
            int pageSize = pageModel.getRows();

            //PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

            PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.ASC, "nameSurname");

            Page<Customer> customerPages = customerService.findByTenant(pageRequest, tenant);

            customerDetailModelPages = customerPages.map(source -> {

                CustomerDetailModel customerDetailModel = new CustomerDetailModel();
                customerDetailModel = modelMapper.map(source, CustomerDetailModel.class);
                return customerDetailModel;

            });


            customerSummaryPagedModel.setCustomerDetailPage(customerDetailModelPages);

            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Müşteriler başarıyla listelendi");

            customerSummaryPagedModel.setBaseModel(baseModel);
            return new ResponseEntity<>(customerSummaryPagedModel, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Customer List Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Müşteriler listelenirken hata oluştu.");
            customerSummaryPagedModel.setBaseModel(baseModel);
            return new ResponseEntity<>(customerSummaryPagedModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<BaseModel> updateCustomer(@RequestBody CustomerDetailModel customerDetailModel, Principal principal) {

        BaseModel baseModel = new BaseModel();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        try {

            User user = userService.findByUsername(principal.getName());
            Tenant tenant = user.getTenant();
            Customer customer = customerService.findOne(customerDetailModel.getId());
            if (customer != null) {
                customer = modelMapper.map(customerDetailModel, Customer.class);
                customer.setTenant(tenant);
                customerService.save(customer);
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Müşteri  başarıyla güncellendi");
                return new ResponseEntity<>(baseModel, HttpStatus.OK);

            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage(
                        "Belirtilen id li müşteri yok. ");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Customer Update Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Müşteri güncelleme sırasında hata oluştu!");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);

        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BaseModel> deleteCustomer(@PathVariable long id) {

        BaseModel baseModel = new BaseModel();

        try {

            Customer customer = customerService.findOne(id);

            if (customer != null) {
                customerService.remove(id);
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Silme işlemi başarılı.");
                return new ResponseEntity<>(baseModel, HttpStatus.OK);
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Silmek istediğiniz müşteri kayıtlarda yok.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Customer Delete Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Silme işlemi sırasında bir hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<CustomerSummaryModel> search(@RequestParam(name = "text") String text, Principal principal) {

        CustomerDetailModel customerDetailModel = new CustomerDetailModel();
        CustomerSummaryModel customerSummaryModel = new CustomerSummaryModel();
        customerSummaryModel.setCustomers(new ArrayList<>());
        BaseModel baseModel = new BaseModel();

        try {

            User user = userService.findByUsername(principal.getName());

            Tenant tenant = user.getTenant();

            List<Customer> customers = customerService.search(tenant, text);

            for (Customer customer : customers) {
                customerDetailModel = modelMapper.map(customer, CustomerDetailModel.class);
                customerSummaryModel.getCustomers().add(customerDetailModel);
            }

            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Arama başarılı.");
            customerSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(customerSummaryModel, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Customer Search Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Müşteri arama sırasında hata oluştu");
            customerSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(customerSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping("/{id}/orders")
    public ResponseEntity<OrderSummaryModel> getCustomerOrder(@PathVariable long id) {

        BaseModel baseModel = new BaseModel();
        OrderSummaryModel orderSummaryModel = new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        ModelMapper mapper = new ModelMapper();


        try {

            Customer customer = customerService.findOne(id);

            if (customer != null) {

                List<Order> orders = orderService.findByCustomer(customer);

                for (Order order : orders) {

                    OrderDetailModel orderDetailModel = new OrderDetailModel();
                    orderDetailModel = mapper.map(order, OrderDetailModel.class);
                    orderSummaryModel.getOrders().add(orderDetailModel);
                }

                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Siparişler başarıyla listelendi.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.OK);
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Bu müşteri kayıtlarda yok.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }


        } catch (Exception e) {
            LOG.error("Customer Orders Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Müşteri siparişleri listelenirken hata oluştu.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    private void createAdminNotification(BaseModel baseModel, AddCustomerResultModel addCustomerResultModel, User user, Tenant tenant, List<User> adminUsers, ArrayList<String> roleNames, Order savedOrder) {
        if (!roleNames.contains("ROLE_ADMIN")) {
            Notification notification = new Notification();
            notification.setCreatedDate(new Date());
            notification.setData(String.valueOf(savedOrder.getId()));
            notification.setTitle("Yeni Sipariş");
            notification.setTenant(tenant);
            String message = user.getNameSurname() + " Yeni Sipariş Ekledi";
            message= WordUtils.capitalize(message);
            notification.setMessage(message);
            notification.setReadNotification(false);

            setUserNotification(tenant, adminUsers, notification);


            notification = notificationService.save(notification);



            //pass notification to sendNotificaiton method.
            if (sendNotificationToMobile(baseModel, addCustomerResultModel, notification, tenant, firebaseNotificationService, roleNames)) {
                LOG.error("Firebase mobile  notification gönderilemedi!!!");
            }

            if (sendNotificationToWeb(baseModel, addCustomerResultModel, notification, tenant, firebaseNotificationService, roleNames)) {
                LOG.error("Firebase web  notification gönderilemedi!!!");
            }
        }
    }

    private boolean sendNotificationToMobile(BaseModel baseModel, AddCustomerResultModel addCustomerResultModel, Notification notification, Tenant tenant, FirebaseNotificationService firebaseNotificationService, ArrayList<String> roleNames) {

        if (!roleNames.contains("ROLE_ADMIN")) {
            List<User> adminUsers;
            adminUsers = userService.findTenantAdminUsers(tenant);
            for (User adminUser : adminUsers) {

                if (adminUser.getLastFirebaseRegId() != null && adminUser.getLastFirebaseRegId() != "") {
                    try {

                        firebaseNotificationService.sendNotificationToMobile(adminUser, notification);


                    } catch (Exception e) {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Firebase bildirim gönderirken hata oluştu: "
                                + e.getClass() + " " + e.getMessage());
                        addCustomerResultModel.setBaseModel(baseModel);
                        return true;

                    }
                }
            }
        }
        return false;
    }

    private boolean sendNotificationToWeb(BaseModel baseModel, AddCustomerResultModel addCustomerResultModel, Notification notification, Tenant tenant, FirebaseNotificationService firebaseNotificationService, ArrayList<String> roleNames) {

        if (!roleNames.contains("ROLE_ADMIN")) {
            List<User> adminUsers;
            adminUsers = userService.findTenantAdminUsers(tenant);
            for (User adminUser : adminUsers) {

                if (adminUser.getLastFirebaseWebRegId() != null && adminUser.getLastFirebaseWebRegId() != "") {
                    try {


                        firebaseNotificationService.sendNotificationToWeb(adminUser, notification);


                    } catch (Exception e) {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Firebase bildirim gönderirken hata oluştu: "
                                + e.getClass() + " " + e.getMessage());
                        addCustomerResultModel.setBaseModel(baseModel);
                        return true;

                    }
                }
            }
        }
        return false;
    }

}
