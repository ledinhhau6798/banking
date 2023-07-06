package com.cg.utils;

import java.util.regex.Pattern;

public class ValidateUtils {
     public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$";
     public static boolean isEmail(String email){
        return Pattern.matches(EMAIL_REGEX,email);
    }
}
