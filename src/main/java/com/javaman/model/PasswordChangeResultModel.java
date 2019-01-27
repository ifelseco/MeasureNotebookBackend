package com.javaman.model;

import lombok.Data;

@Data
public class PasswordChangeResultModel {

    private boolean oldPasswordEmpty;
    private boolean newPasswordEmpty;
    private boolean oldPasswordWrong;
    private boolean newPasswordMustBeMinSix;
    private boolean newPasswodMustBeMaxTen;
    private boolean badRequest;
    private boolean userNotFound;


}
