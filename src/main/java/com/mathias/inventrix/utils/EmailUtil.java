package com.mathias.inventrix.utils;

public class EmailUtil {

    public static String getVerificationUrl( String token){
        return  "http://localhost:8080/api/auth/confirm?token=" + token ;
    }
}
