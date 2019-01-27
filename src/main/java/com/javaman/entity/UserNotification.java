package com.javaman.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class UserNotification implements Serializable{

    private static final long serialVersionUID = 45457898786549886L;

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Notification notification;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Override
    public String toString() {
        return "UserNotification{}";
    }
}
