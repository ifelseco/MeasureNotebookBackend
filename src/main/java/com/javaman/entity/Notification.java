package com.javaman.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
@Data
public class Notification implements Serializable{

    private static final long serialVersionUID = 5454545223232154545L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false, updatable = false)
    private Long id;

    private String title;
    private String message;
    private String data;
    private boolean readNotification;
    private Date createdDate;

    @OneToMany(mappedBy = "notification",fetch = FetchType.EAGER,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserNotification> userNotifications=new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;


}
