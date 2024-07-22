package com.example.Final.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fbid;
    @Column(nullable = false)
    private Integer otp;
    @Column(nullable = false)
    private Date expireTime;
    @OneToOne
    private User user;

}
