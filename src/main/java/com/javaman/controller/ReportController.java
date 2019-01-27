package com.javaman.controller;

import com.javaman.entity.Order;
import com.javaman.entity.Tenant;
import com.javaman.entity.User;
import com.javaman.model.*;
import com.javaman.service.ReportService;
import com.javaman.service.UserService;
import com.javaman.util.ConstRole;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/report")
@Secured({ConstRole.ARG_ADMIN_ROLE, ConstRole.ARG_USER_ROLE})
public class ReportController {

    private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);

    private ReportService reportService;
    private UserService userService;
    private ModelMapper modelMapper;

    @Autowired
    public ReportController(ReportService reportService, UserService userService,ModelMapper modelMapper) {
        this.reportService = reportService;
        this.userService=userService;
        this.modelMapper=modelMapper;
    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/{year}",method = RequestMethod.GET)
    public ResponseEntity<ReportSummaryModel> getMonthOfYearReport(@PathVariable int year , Principal principal){

        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        BaseModel baseModel=new BaseModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                reportSummaryModel=reportService.getMonthOfYearReport(year,tenant);
                baseModel.setResponseMessage("Rapor başarıyla oluşturuldu.");
                baseModel.setResponseCode(0);
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Month of year report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Rapor oluşturulamadı.");
            reportSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/endOfDay",method = RequestMethod.GET)
    public ResponseEntity<ReportSummaryModel> getEndOfDayReport(Principal principal){

        BaseModel baseModel=new BaseModel();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                reportSummaryModel=reportService.getEndOfDayReport(tenant);
                baseModel.setResponseMessage("Rapor başarıyla oluşturuldu.");
                baseModel.setResponseCode(0);
                reportSummaryModel.setBaseModel(baseModel);

                return new ResponseEntity<>(reportSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("End of day report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Rapor oluşturulamadı.");
            reportSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/lastThreeMonth",method = RequestMethod.GET)
    public ResponseEntity<ReportSummaryModel> getLastThreeMonthReport(Principal principal){

        BaseModel baseModel=new BaseModel();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                reportSummaryModel=reportService.getLastThreeMonthReport(tenant);
                baseModel.setResponseMessage("Rapor başarıyla oluşturuldu.");
                baseModel.setResponseCode(0);
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Last three month report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Rapor oluşturulamadı.");
            reportSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/weeksOfMonth/{year}/{month}",method = RequestMethod.GET)
    public ResponseEntity<ReportSummaryModel> getWeekOfMonthReport(@PathVariable("year") int year,@PathVariable("month") int month,Principal principal){

        BaseModel baseModel=new BaseModel();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                reportSummaryModel=reportService.getWeekOfMonthReport(year,month,tenant);
                baseModel.setResponseMessage("Rapor başarıyla oluşturuldu.");
                baseModel.setResponseCode(0);
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("week of month report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Rapor oluşturulamadı.");
            reportSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }


    @RequestMapping(value = "/next/delivery",method = RequestMethod.GET)
    public ResponseEntity<OrderSummaryModel> getNextThreeDayDeliveryOrder(Principal principal){

        OrderSummaryModel orderSummaryModel=new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        BaseModel baseModel=new BaseModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                List<Order> orders=reportService.getNextThreeDayDeliveryOrder(tenant);
                for (Order order:orders) {
                    OrderDetailModel orderDetailModel=new OrderDetailModel();
                    orderDetailModel=modelMapper.map(order,OrderDetailModel.class);
                    orderSummaryModel.getOrders().add(orderDetailModel);
                }
                baseModel.setResponseMessage("Sonraki 3 gün teslimatları başarıyla listelendi.");
                baseModel.setResponseCode(0);
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Next delivery report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Rapor oluşturulamadı.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/next/measure",method = RequestMethod.GET)
    public ResponseEntity<OrderSummaryModel> getNextThreeDayMeasureOrder(Principal principal){

        OrderSummaryModel orderSummaryModel=new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        BaseModel baseModel=new BaseModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                List<Order> orders=reportService.getNextThreeDayMeasureOrder(tenant);
                for (Order order:orders) {
                    OrderDetailModel orderDetailModel=new OrderDetailModel();
                    orderDetailModel=modelMapper.map(order,OrderDetailModel.class);
                    orderSummaryModel.getOrders().add(orderDetailModel);
                }
                baseModel.setResponseMessage("Sonraki 3 gün ölçüleri başarıyla listelendi.");
                baseModel.setResponseCode(0);
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Next measure report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Rapor oluşturulamadı.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/orders/endOfDay",method = RequestMethod.GET)
    public ResponseEntity<OrderSummaryModel> getEndOfDayOrder(Principal principal){

        OrderSummaryModel orderSummaryModel=new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        BaseModel baseModel=new BaseModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                List<Order> orders=reportService.getEndOfDayOrder(tenant);
                for (Order order:orders) {
                    OrderDetailModel orderDetailModel=new OrderDetailModel();
                    orderDetailModel=modelMapper.map(order,OrderDetailModel.class);
                    orderSummaryModel.getOrders().add(orderDetailModel);
                }
                baseModel.setResponseMessage("Gün sonu başarıyla listelendi.");
                baseModel.setResponseCode(0);
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("End of day order error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Gün sonu oluşturulamadı.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/orders/lastSeven",method = RequestMethod.GET)
    public ResponseEntity<ReportSummaryModel> getLastSevenDayOrder(Principal principal){

        BaseModel baseModel=new BaseModel();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                reportSummaryModel=reportService.getLastSevenDayOrder(tenant);
                baseModel.setResponseMessage("Son 7 gün başarıyla listelendi.");
                baseModel.setResponseCode(0);
                reportSummaryModel.setBaseModel(baseModel);
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Last seven day order error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Son 7 gün oluşturulamadı.");
            reportSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/orders/lastThirty",method = RequestMethod.GET)
    public ResponseEntity<ReportSummaryModel> getLastThirtyDayOrder(Principal principal){
        BaseModel baseModel=new BaseModel();
        ReportSummaryModel reportSummaryModel=new ReportSummaryModel();

        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                reportSummaryModel=reportService.getLastThirtyDayOrder(tenant);
                baseModel.setResponseMessage("Son 30 gün başarıyla listelendi.");
                baseModel.setResponseCode(0);
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                reportSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Last thirty day order error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Son 30 gün oluşturulamadı.");
            reportSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(reportSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/orders/remain",method = RequestMethod.GET)
    public ResponseEntity<OrderSummaryModel> getAllRemainingAmountOrder(Principal principal){

        OrderSummaryModel orderSummaryModel=new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        BaseModel baseModel=new BaseModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                List<Order> orders=reportService.getAllRemainingAmountOrder(tenant);
                for (Order order:orders) {
                    OrderDetailModel orderDetailModel=new OrderDetailModel();
                    orderDetailModel=modelMapper.map(order,OrderDetailModel.class);
                    orderSummaryModel.getOrders().add(orderDetailModel);
                }
                baseModel.setResponseMessage("Ödemesi kalan siparişler başarıyla listelendi.");
                baseModel.setResponseCode(0);
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel,HttpStatus.OK);

            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Remain orders report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Siparişler listelenemedi.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }

    @Secured(ConstRole.ARG_ADMIN_ROLE)
    @RequestMapping(value = "/orders/remain/date",method = RequestMethod.POST)
    public ResponseEntity<OrderSummaryModel> getAllRemainingAmountOrderByDate(@RequestBody ReportDateModel reportDateModel, Principal principal){

        OrderSummaryModel orderSummaryModel=new OrderSummaryModel();
        orderSummaryModel.setOrders(new ArrayList<>());
        BaseModel baseModel=new BaseModel();
        try{
            User user=userService.findByUsername(principal.getName());
            if(user!=null){
                Tenant tenant=user.getTenant();
                if(reportDateModel.getLimitDate()!=null){
                    List<Order> orders=reportService.getAllRemainingAmountOrderByDate(tenant,reportDateModel.getLimitDate());
                    for (Order order:orders) {
                        OrderDetailModel orderDetailModel=new OrderDetailModel();
                        orderDetailModel=modelMapper.map(order,OrderDetailModel.class);
                        orderSummaryModel.getOrders().add(orderDetailModel);
                    }
                    baseModel.setResponseMessage("Ödemesi kalan siparişler başarıyla listelendi.");
                    baseModel.setResponseCode(0);
                    orderSummaryModel.setBaseModel(baseModel);
                    return new ResponseEntity<>(orderSummaryModel,HttpStatus.OK);
                }else{
                    baseModel.setResponseCode(1);
                    baseModel.setResponseMessage("Hata! Bir tarih seçmelisiniz.");
                    orderSummaryModel.setBaseModel(baseModel);
                    return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
                }


            }else{
                baseModel.setResponseCode(1);
                baseModel.setResponseMessage("Hata! Kullanıcı bulunamadı.");
                orderSummaryModel.setBaseModel(baseModel);
                return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            LOG.error("Remain orders by date report error: "+e.getClass()+" "+e.getMessage());
            baseModel.setResponseCode(1);
            baseModel.setResponseMessage("Hata! Siparişler listelenemedi.");
            orderSummaryModel.setBaseModel(baseModel);
            return new ResponseEntity<>(orderSummaryModel, HttpStatus.BAD_REQUEST);
        }

    }



}
