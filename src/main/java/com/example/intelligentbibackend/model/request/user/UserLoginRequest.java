package com.example.intelligentbibackend.model.request.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -6657660675839994445L;
    private String account;
    private String password;
}
