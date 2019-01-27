package com.javaman.controller;

import com.google.common.base.Strings;
import com.javaman.model.BaseModel;
import com.javaman.model.EmailModel;
import com.javaman.model.InfoMailModel;
import com.javaman.service.EmailService;
import com.javaman.util.ConstApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mail")
public class MailController {


    private EmailService emailService;

    @Autowired
    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping(value = "/send",method = RequestMethod.POST)
    public ResponseEntity<BaseModel> sendMail(@RequestBody InfoMailModel infoMailModel){
        BaseModel baseModel=new BaseModel();

        try{

            if(!Strings.isNullOrEmpty(infoMailModel.getPhone()) && !Strings.isNullOrEmpty(infoMailModel.getNameSurname())){
                EmailModel mail=new EmailModel();
                mail.setFrom(ConstApp.FROM_EMAIL);
                mail.setTo(ConstApp.FROM_EMAIL);
                mail.setSubject("Bilgi Talebi");

                Map<String, Object> model = new HashMap<>();
                model.put("info", infoMailModel);
                model.put("signature", "Akıllı Ölçü Defteri");
                mail.setModel(model);
                emailService.infoEmail(mail);

                baseModel.setResponseCode(0);
                baseModel.setResponseMessage("Bilgi Talebi Gönderildi");
                return new ResponseEntity<>(baseModel, HttpStatus.OK);
            }else{
                baseModel.setResponseMessage("Hata: Eksik Bilgi Birdiniz.");
                baseModel.setResponseCode(1);
                return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
            }


        }catch(Exception e){
            baseModel.setResponseMessage("Hata: Bilgi Talebi Gönderilemedi");
            baseModel.setResponseCode(1);
            return new ResponseEntity<>(baseModel, HttpStatus.BAD_REQUEST);
        }
    }
}
