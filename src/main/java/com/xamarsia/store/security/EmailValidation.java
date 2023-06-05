package com.xamarsia.store.security;

import java.util.regex.Pattern;

public class EmailValidation {
    private static final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static boolean patternMatches(String emailAddress) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}