package com.example.intelligentbibackend.model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

//  继承序列化

@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4903924858704791078L;

    private String account;
    private String password;
    private String checkPassword;

}
