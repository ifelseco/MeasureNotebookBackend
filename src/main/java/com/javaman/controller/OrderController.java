package com.javaman.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.javaman.entity.*;
import com.javaman.model.*;
import com.javaman.service.*;
import com.javaman.util.ConstRole;
import org.apache.commons.text.WordUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.javaman.security.UserRole;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private OrderService orderService;
    private UserService userService;
    private OrderLineService orderLineService;
    private UserRoleService userRoleService;
    private NotificationService notificationService;
    private ModelMapper modelMapper;
    private FirebaseNotificationService firebaseNotificationService;

    @Autowired
    public OrderController(OrderService orderService,UserService userService,
                           OrderLineService orderLineService,UserRoleService userRoleService,
                           NotificationService notificationService,ModelMapper modelMapper,
                           FirebaseNotificationService firebaseNotificationService) {
        this.orderService=orderService;
        this.userService=userService;
        this.orderLineService=orderLineService;
        this.userRoleService=userRoleService;
        this.notificationService=notificationService;
        this.modelMapper=modelMapper;
        this.firebaseNotificationService=firebaseNotificationService;

    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseEntity<OrderSummaryPageModel> getOrders(@RequestBody PageModel pageModel, Principal principal) {

        BaseModel baseModel = new BaseModel();

        OrderSummaryPageModel orderSummaryPageModel = new OrderSummaryPageModel();
        Page<OrderDetailModel> orderDetailModelPages;
        //ModelMapper modelMapper = new ModelMapper();


        try {

            User user = userService.findByUsername(principal.getName());

            Tenant tenant = user.getTenant();

            Set<UserRole> userRoles = user.getUserRoles();

            ArrayList<String> roleNames = new ArrayList<>();

            for (UserRole userRole : userRoles) {
                roleNames.add(userRole.getRole().getName());
            }

            if (roleNames.contains("ROLE_SUPER_ADMIN")) {

                int pageNumber = pageModel.getFirst() / pageModel.getRows();
                int pageSize = pageModel.getRows();

                PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

                Page<Order> orderPages = orderService.findAll(pageRequest);

                orderDetailModelPages = orderPages.map(source -> {

                    OrderDetailModel orderDetailModel = new OrderDetailModel();
                    orderDetailModel = modelMapper.map(source, OrderDetailModel.class);
                    orderDetailModel.setDepositeAmount(source.getDepositeAmount().doubleValue());
                    orderDetailModel.setTotalAmount(source.getTotalAmount().doubleValue());
                    return orderDetailModel;

                });
            } else if (roleNames.contains("ROLE_ADMIN")) {

                int pageNumber = pageModel.getFirst() / pageModel.getRows();
                int pageSize = pageModel.getRows();

                PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");
                Page<Order> orderPages = orderService.findByTenant(pageRequest, tenant);

                orderDetailModelPages = orderPages.map(source -> {

                    OrderDetailModel orderDetailModel = new OrderDetailModel();
                    orderDetailModel = modelMapper.map(source, OrderDetailModel.class);
                    orderDetailModel.setDepositeAmount(source.getDepositeAmount().doubleValue());
                    orderDetailModel.setTotalAmount(source.getTotalAmount().doubleValue());
                    return orderDetailModel;

                });

            } else if (roleNames.contains("ROLE_TAILOR")) {

                int pageNumber = pageModel.getFirst() / pageModel.getRows();
                int pageSize = pageModel.getRows();

                PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

                //Page<Order> orderPages = orderService.findTailorOrder(pageRequest, tenant);
                List<Order> orders = orderService.findTailorOrder(tenant);
                List<OrderDetailModel> orderModels = new ArrayList<>();

                for (Order order : orders) {
                    OrderDetailModel orderDetailModel = new OrderDetailModel();
                    orderDetailModel = modelMapper.map(order, OrderDetailModel.class);
                    orderDetailModel.setTotalAmount(0);
                    orderDetailModel.setDepositeAmount(0);
                    orderDetailModel.setMeasureDate(null);
                    orderModels.add(orderDetailModel);
                }


                int start = pageRequest.getOffset();
                int end = (start + pageRequest.getPageSize()) > orderModels.size() ? orderModels.size() : (start + pageRequest.getPageSize());
                orderDetailModelPages = new PageImpl<>(orderModels.subList(start, end), pageRequest, orderModels.size());

            } else {

                int pageNumber = pageModel.getFirst() / pageModel.getRows();
                int pageSize = pageModel.getRows();

                PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

                Page<Order> orderPages = orderService.findByUser(pageRequest, user);

                orderDetailModelPages = orderPages.map(source -> {

                    OrderDetailModel orderDetailModel = new OrderDetailModel();
                    orderDetailModel = modelMapper.map(source, OrderDetailModel.class);
                    orderDetailModel.setDepositeAmount(source.getDepositeAmount().doubleValue());
                    orderDetailModel.setTotalAmount(source.getTotalAmount().doubleValue());
                    return orderDetailModel;

                });

            }

            orderSummaryPageModel.setOrderDetailPage(orderDetailModelPages);

            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Siparişler başarıyla listelendi");
            orderSummaryPageModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryPageModel, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Order List Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Siparişler listelenirken hata oluştu.");
            orderSummaryPageModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryPageModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/list/filter", method = RequestMethod.POST)
    public ResponseEntity<OrderSummaryPageModel> getOrdersByOrderStatus(@RequestParam(name = "status") int orderStatus, @RequestBody PageModel pageModel, Principal principal) {
        OrderStatus mOrderStatus = OrderStatus.valueOf(orderStatus);
        OrderSummaryPageModel orderSummaryPageModel = new OrderSummaryPageModel();
        //ModelMapper modelMapper = new ModelMapper();
        BaseModel baseModel = new BaseModel();
        if (mOrderStatus != null) {
            Page<OrderDetailModel> orderDetailModelPages;
            try {

                User user = userService.findByUsername(principal.getName());

                Tenant tenant = user.getTenant();

                Set<UserRole> userRoles = user.getUserRoles();

                ArrayList<String> roleNames = new ArrayList<>();

                for (UserRole userRole : userRoles) {
                    roleNames.add(userRole.getRole().getName());
                }

                if (roleNames.contains("ROLE_ADMIN")) {

                    int pageNumber = pageModel.getFirst() / pageModel.getRows();
                    int pageSize = pageModel.getRows();

                    PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

                    Page<Order> orderPages = orderService.findByTenantAndOrderStatus(pageRequest, tenant, mOrderStatus);

                    orderDetailModelPages = orderPages.map(new Converter<Order, OrderDetailModel>() {

                        @Override
                        public OrderDetailModel convert(Order source) {

                            OrderDetailModel orderDetailModel = new OrderDetailModel();
                            orderDetailModel = modelMapper.map(source, OrderDetailModel.class);
                            orderDetailModel.setDepositeAmount(source.getDepositeAmount().doubleValue());
                            orderDetailModel.setTotalAmount(source.getTotalAmount().doubleValue());
                            return orderDetailModel;

                        }

                    });

                } else if (roleNames.contains("ROLE_TAILOR")) {

                    if (orderStatus == 3 || orderStatus == 4) {
                        int pageNumber = pageModel.getFirst() / pageModel.getRows();
                        int pageSize = pageModel.getRows();

                        PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

                        //Page<Order> orderPages = orderService.findTailorOrder(pageRequest, tenant);
                        List<Order> orders = new ArrayList<>();
                        List<OrderDetailModel> orderModels = new ArrayList<>();

                        if (orderStatus == 3) {
                            orders = orderService.findTailorOrderProcessing(tenant);

                        } else if (orderStatus == 4) {
                            orders = orderService.findTailorOrderProcessed(tenant);
                        } else {

                        }


                        for (Order order : orders) {
                            OrderDetailModel orderDetailModel = new OrderDetailModel();
                            orderDetailModel = modelMapper.map(order, OrderDetailModel.class);
                            orderDetailModel.setTotalAmount(0);
                            orderDetailModel.setDepositeAmount(0);
                            orderDetailModel.setMeasureDate(null);
                            orderModels.add(orderDetailModel);
                        }


                        int start = pageRequest.getOffset();
                        int end = (start + pageRequest.getPageSize()) > orderModels.size() ? orderModels.size() : (start + pageRequest.getPageSize());
                        orderDetailModelPages = new PageImpl<>(orderModels.subList(start, end), pageRequest, orderModels.size());
                    } else {
                        baseModel.setResponseCode(1);
                        baseModel.setResponseMessage("Filtreleme kriteri yanlış");
                        orderSummaryPageModel.setBaseModel(baseModel);
                        return new ResponseEntity<OrderSummaryPageModel>(orderSummaryPageModel, HttpStatus.BAD_REQUEST);
                    }

                } else {

                    int pageNumber = pageModel.getFirst() / pageModel.getRows();
                    int pageSize = pageModel.getRows();

                    PageRequest pageRequest = new PageRequest(pageNumber, pageSize, Sort.Direction.DESC, "orderDate");

                    Page<Order> orderPages = orderService.findByUserAndOrderStatus(pageRequest, user, mOrderStatus);

                    orderDetailModelPages = orderPages.map(source -> {

                        OrderDetailModel orderDetailModel = new OrderDetailModel();
                        orderDetailModel = modelMapper.map(source, OrderDetailModel.class);
                        orderDetailModel.setDepositeAmount(source.getDepositeAmount().doubleValue());
                        orderDetailModel.setTotalAmount(source.getTotalAmount().doubleValue());
                        return orderDetailModel;

                    });

                }

                orderSummaryPageModel.setOrderDetailPage(orderDetailModelPages);

                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Siparişler başarıyla listelendi");
                orderSummaryPageModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryPageModel, HttpStatus.OK);

            } catch (Exception e) {
                LOG.error("Order Filter Error: "+e.getClass()+" "+e.getMessage());
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Siaprişler filtrelenirken hata oluştu.");
                orderSummaryPageModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryPageModel, HttpStatus.BAD_REQUEST);
            }
        } else {
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Filtreleme kriteri yanlış");
            orderSummaryPageModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryPageModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_TAILOR_ROLE)
    @RequestMapping(value = "/list/tailor/filter")
    public ResponseEntity<OrderSummaryModel> getTailorOrder(@RequestParam(name = "status") int orderStatus, Principal principal) {
        OrderSummaryModel orderSummaryModel = new OrderSummaryModel();
        OrderStatus mOrderStatus = OrderStatus.valueOf(orderStatus);
        //ModelMapper modelMapper = new ModelMapper();
        BaseModel baseModel = new BaseModel();
        if (mOrderStatus != null) {

            try {

                User user = userService.findByUsername(principal.getName());

                Tenant tenant = user.getTenant();


                if (orderStatus == 3 || orderStatus == 4) {


                    List<Order> tailorOrders = new ArrayList<>();
                    List<OrderDetailModel> orderModels = new ArrayList<>();

                    if (orderStatus == 3) {
                        tailorOrders = orderService.findTailorOrderProcessing(tenant);

                    } else if (orderStatus == 4) {
                        tailorOrders = orderService.findTailorOrderProcessed(tenant);
                    }


                    for (Order order : tailorOrders) {
                        OrderDetailModel orderDetailModel;
                        orderDetailModel = modelMapper.map(order, OrderDetailModel.class);
                        orderDetailModel.setTotalAmount(0);
                        orderDetailModel.setDepositeAmount(0);
                        orderDetailModel.setMeasureDate(null);
                        orderModels.add(orderDetailModel);
                    }

                    orderSummaryModel.setOrders(orderModels);
                    baseModel.setResponseCode(0);
                    baseModel.setResponseMessage("Siparişler başarıyla listelendi");
                    orderSummaryModel.setBaseModel(baseModel);
                    return new ResponseEntity<>(orderSummaryModel, HttpStatus.OK);

                } else {
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Filtreleme kriteri yanlış");
                    orderSummaryModel.setBaseModel(baseModel);
                    return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
                }


            } catch (Exception e) {
                LOG.error("Tailor Order Filter Error: "+e.getClass()+" "+e.getMessage());
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata: Terzi siparişler filrelenirken hata oluştu.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }
        } else {
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Filtreleme kriteri yanlış");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<BaseModel> updateOrder(@RequestBody OrderUpdateModel orderUpdateModel, Principal principal) {

        BaseModel baseModel = new BaseModel();

        try {

            User user = userService.findByUsername(principal.getName());
            Tenant tenant = user.getTenant();

            Order order = orderService.findOne(orderUpdateModel.getId());

            if (tailorCheck(user)) {
                order.setOrderStatus(orderUpdateModel.getOrderStatus());
            } else {
                order.setDeliveryDate(orderUpdateModel.getDeliveryDate());
                order.setDepositeAmount(new BigDecimal(orderUpdateModel.getDepositeAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                order.setMeasureDate(orderUpdateModel.getMeasureDate());
                order.setMountExist(orderUpdateModel.isMountExist());
                order.setOrderDate(orderUpdateModel.getOrderDate());
                order.setOrderStatus(orderUpdateModel.getOrderStatus());
                order.setTotalAmount(new BigDecimal(orderUpdateModel.getTotalAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
                order.setUser(user);
                order.setTenant(tenant);
            }

            Order savedOrder = orderService.save(order);

            // firebase start


            ArrayList<String> roleNames = (ArrayList<String>) userRoleService.findUserRole(user);

            List<User> adminUsers = new ArrayList<User>();
            adminUsers = userService.findTenantAdminUsers(tenant);
            List<User> tailorUsers = new ArrayList<User>();
            tailorUsers = userService.findTenantTailorUsers(tenant);


            if (!roleNames.contains("ROLE_ADMIN")) {

                //Kullanıcı admin değil ise


                if (roleNames.contains("ROLE_TAILOR")) {
                    //Kullanıcı terzi ise
                    //Admin notificationu olarak kaydet ve firebaseden admine notf. gönder.

                    Notification notification = new Notification();
                    notification.setCreatedDate(new Date());
                    notification.setData(String.valueOf(savedOrder.getId()));
                    notification.setTitle("Sipariş güncellendi.");
                    notification.setTenant(tenant);
                    String message = user.getNameSurname() + " Sipariş Güncelledi";
                    message= WordUtils.capitalize(message);
                    notification.setMessage(message);
                    notification.setReadNotification(false);
                    notification.setTenant(tenant);

                    CustomerController.setUserNotification(tenant, adminUsers, notification);

                    notification = notificationService.save(notification);

                    // admine gönder

                    sendMobileNotification(notification, firebaseNotificationService, adminUsers);
                    sendWebNotification(notification, firebaseNotificationService, adminUsers);


                } else {
                    //kullanıcı terzi değilse;
                    // kullanıcı user dır.
                    if (savedOrder.getOrderStatus() == OrderStatus.PROCESSING && savedOrder.getTailorOrderLineCount() > 0) {

                        //ve sipariş durum güncellemesi terziyi ilgilendiriyorsa
                        //notificationu terzi ve admin olarak kaydet

                        Notification notification = new Notification();
                        notification.setCreatedDate(new Date());
                        notification.setData(String.valueOf(savedOrder.getId()));
                        notification.setTitle("Sipariş Güncellendi");
                        notification.setTenant(tenant);
                        String message = user.getNameSurname() + " Sipariş Güncelledi";
                        message= WordUtils.capitalize(message);
                        notification.setMessage(message);
                        notification.setReadNotification(false);
                        notification.setTenant(tenant);


                        CustomerController.setUserNotification(tenant, tailorUsers, notification);

                        CustomerController.setUserNotification(tenant, adminUsers, notification);

                        notification = notificationService.save(notification);

                        //Terziye ve admine gönder

                        sendMobileNotification(notification, firebaseNotificationService, adminUsers);
                        sendWebNotification(notification, firebaseNotificationService, adminUsers);

                        sendMobileNotification(notification, firebaseNotificationService, tailorUsers);
                        sendWebNotification(notification, firebaseNotificationService, tailorUsers);


                    } else {
                        //Terziyi ilgilendirmiyor
                        //notf sadece admin olarak kaydet

                        Notification notification = new Notification();
                        notification.setCreatedDate(new Date());
                        notification.setData(String.valueOf(savedOrder.getId()));
                        notification.setTitle("Sipariş Güncellendi");
                        notification.setTenant(tenant);
                        String message = user.getNameSurname() + " Sipariş Güncelledi";
                        message= WordUtils.capitalize(message);
                        notification.setMessage(message);
                        notification.setReadNotification(false);
                        notification.setTenant(tenant);

                        CustomerController.setUserNotification(tenant, adminUsers, notification);

                        /*UserNotification userNotificationForAdmin = new UserNotification();
                        userNotificationForAdmin.setNotification(notification);
                        userNotificationForAdmin.setUser(user);
                        userNotificationForAdmin.setTenant(tenant);
                        notification.getUserNotifications().add(userNotificationForAdmin);*/
                        notification = notificationService.save(notification);


                        //sadece admine gönder
                        sendMobileNotification(notification, firebaseNotificationService, adminUsers);
                        sendWebNotification(notification, firebaseNotificationService, adminUsers);
                    }
                }


            } else {
                //kullanıcı admin ise

                if (savedOrder.getOrderStatus() == OrderStatus.PROCESSING && savedOrder.getTailorOrderLineCount() > 0) {

                    //ve sipariş durum güncellemesi terziyi ilgilendiriyorsa
                    //Terzi notificationu olarak kaydet


                    Notification notification = new Notification();

                    notification.setCreatedDate(new Date());
                    notification.setData(String.valueOf(savedOrder.getId()));
                    notification.setTitle("Yeni Sipariş");
                    notification.setTenant(tenant);
                    String message = user.getNameSurname() + " Sipariş Ekledi";
                    message= WordUtils.capitalize(message);
                    notification.setMessage(message);
                    notification.setReadNotification(false);
                    notification.setTenant(tenant);


                    CustomerController.setUserNotification(tenant, tailorUsers, notification);

                    notification = notificationService.save(notification);

                    // firebaseden terziye notification gönder.

                    if (!roleNames.contains("ROLE_TAILOR")) {

                        if (tailorUsers.size() > 0) {
                            sendMobileNotification(notification, firebaseNotificationService, tailorUsers);
                            sendWebNotification(notification, firebaseNotificationService, tailorUsers);
                        }

                    }

                }

            }

            // firebase end
            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Sipariş başarıyla güncellendi");
            return new ResponseEntity<>(baseModel, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Order Update Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Sipariş güncellenirken hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);

        }

    }

    @RequestMapping("/{id}")
    public ResponseEntity<OrderLineSummaryModel> getOrderLines(@PathVariable String id, Principal principal) {
        BaseModel baseModel = new BaseModel();
        OrderLineSummaryModel orderLineSummaryModel = new OrderLineSummaryModel();

        OrderLineDetailModel orderLineDetailModel = new OrderLineDetailModel();

        OrderDetailModel orderDetailModel = new OrderDetailModel();

        //ModelMapper mapper = new ModelMapper();

        try {

            orderLineSummaryModel.setOrderLineDetailList(new ArrayList<>());

            Order order = orderService.findOne(Long.valueOf(id));
            User user = userService.findByUsername(principal.getName());

            if (order != null) {


                ArrayList<OrderLine> orderLines = (ArrayList<OrderLine>) orderLineService.findByOrder(order);
                orderDetailModel = modelMapper.map(order, OrderDetailModel.class);
                orderDetailModel.setDepositeAmount(order.getDepositeAmount().doubleValue());
                orderDetailModel.setTotalAmount(order.getTotalAmount().doubleValue());
                orderLineSummaryModel.setOrder(orderDetailModel);

                if (tailorCheck(user)) {
                    orderLineSummaryModel.getOrder().setTotalAmount(0);
                    orderLineSummaryModel.getOrder().setMeasureDate(null);
                    orderLineSummaryModel.getOrder().setDepositeAmount(0);
                }

                for (OrderLine orderLine : orderLines) {
                    orderLineDetailModel = modelMapper.map(orderLine, OrderLineDetailModel.class);
                    orderLineDetailModel.setLineAmount(orderLine.getLineAmount().doubleValue());
                    orderLineDetailModel.setUsedMaterial(orderLine.getUsedMaterial().doubleValue());

                    if (tailorCheck(user)) {
                        orderLineDetailModel.setLineAmount(0);

                        if (orderLineDetailModel.getProduct().getProductValue() != ProducNames.DIKEY &&
                                orderLineDetailModel.getProduct().getProductValue() != ProducNames.STOR &&
                                orderLineDetailModel.getProduct().getProductValue() != ProducNames.ZEBRA &&
                                orderLineDetailModel.getProduct().getProductValue() != ProducNames.TULSTOR &&
                                orderLineDetailModel.getProduct().getProductValue() != ProducNames.JALUZI) {
                            orderLineSummaryModel.getOrderLineDetailList().add(orderLineDetailModel);
                        }

                    } else {
                        orderLineSummaryModel.getOrderLineDetailList().add(orderLineDetailModel);
                    }
                }

                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Sipariş içeriği başarıyla listelendi.");
                orderLineSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderLineSummaryModel, HttpStatus.OK);

            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Bu id de sipariş yok.");
                orderLineSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderLineSummaryModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Orderline List Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Siparişlere erişim sırasında hata oluştu.");
            orderLineSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderLineSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/list", method = RequestMethod.DELETE)
    @Secured(ConstRole.ARG_ADMIN_ROLE)
    public ResponseEntity<BaseModel> deleteOrders(@RequestBody DeleteOrdersModel deleteOrdersModel, Principal principal) {

        BaseModel baseModel = new BaseModel();
        ArrayList<Order> orders = new ArrayList<>();
        try {
            for (Long id : deleteOrdersModel.getOrderIds()) {
                Order order = orderService.findOne(id);
                if (order != null) {
                    orders.add(order);
                }
            }

            if (orders.size() > 0) {
                orderService.remove(orders);
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Seçilen siparişler başarıyla silindi.");
                return new ResponseEntity<BaseModel>(baseModel, HttpStatus.OK);
            } else {
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Seçilen siparişler zaten  silinmiş.");
                return new ResponseEntity<BaseModel>(baseModel, HttpStatus.OK);
            }

        } catch (Exception e) {
            LOG.error("Order List Delete Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Sipariş silme sırasında hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Secured(ConstRole.ARG_ADMIN_ROLE)
    public ResponseEntity<BaseModel> deleteOrder(@PathVariable long id) {

        BaseModel baseModel = new BaseModel();

        try {

            Order order = orderService.findOne(id);


            if (order != null) {
				/*order.setCustomer(null);
				order=orderService.save(order);*/
                orderService.remove(order);
                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Silme işlemi başarılı.");
                return new ResponseEntity<>(baseModel, HttpStatus.OK);
            } else {
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Silmek istediğiniz sipariş kayıtlarda yok.");
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            LOG.error("Order Delete Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Silme işlemi sırasında bir hata oluştu.");
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<OrderSummaryModel> search(@RequestParam(name="orderNumber") String orderNumber, Principal principal) {

        OrderDetailModel orderDetailModel;
        OrderSummaryModel orderSummaryModel = new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        BaseModel baseModel = new BaseModel();
        //ModelMapper modelMapper = new ModelMapper();

        try {

            User user = userService.findByUsername(principal.getName());

            Tenant tenant = user.getTenant();

            List<Order> orders = orderService.search(tenant, orderNumber);

            for (Order order : orders) {
                orderDetailModel = modelMapper.map(order, OrderDetailModel.class);
                orderDetailModel.setTotalAmount(order.getTotalAmount().doubleValue());
                orderDetailModel.setDepositeAmount(order.getDepositeAmount().doubleValue());
                orderSummaryModel.getOrders().add(orderDetailModel);
            }

            baseModel.setResponseCode(0);
            baseModel.setResponseMessage("Arama başarılı.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.OK);

        } catch (Exception e) {
            LOG.error("Order Search Error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata: Sipariş arama sırasında hata!");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    public boolean tailorCheck(User user) {
        Set<UserRole> userRoles = user.getUserRoles();

        ArrayList<String> roleNames = new ArrayList<>();

        for (UserRole userRole : userRoles) {
            roleNames.add(userRole.getRole().getName());
        }

        return roleNames.contains("ROLE_TAILOR");
    }

    private void sendMobileNotification(Notification notification, FirebaseNotificationService firebaseNotificationService, List<User> users) {
        for (User user : users) {

            if (user.getLastFirebaseRegId() != null && !user.getLastFirebaseRegId().isEmpty()) {

                try {
                    firebaseNotificationService.sendNotificationToMobile(user, notification);
                } catch (Exception e) {
                    LOG.debug("firebase_mobile_error : " + e.getClass() + " " + e.getMessage());
                }


            }

        }
    }

    private void sendWebNotification(Notification notification, FirebaseNotificationService firebaseNotificationService, List<User> users) {
        for (User user : users) {

            if (user.getLastFirebaseWebRegId() != null && !user.getLastFirebaseWebRegId().isEmpty()) {

                try {

                    firebaseNotificationService.sendNotificationToWeb(user, notification);

                } catch (Exception e) {
                    LOG.debug("firebase_web_error: " + e.getClass() + " " + e.getMessage());

                }
            }

        }
    }


}
