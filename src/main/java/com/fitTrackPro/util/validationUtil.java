package com.fitTrackPro.util;

import java.util.regex.Pattern;

/**
 * Validation Utility Class
 * Provides input validation methods
 */
public class validationUtil {
    
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    private static final String PHONE_PATTERN = "^[0-9]{10,15}$";
    
    private static final String NAME_PATTERN = "^[A-Za-z\\s\\-']{2,50}$";
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_PATTERN, email.trim());
    }
    
    /**
     * Validates phone number
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String cleaned = phone.replaceAll("[\\s\\-()]", "");
        return Pattern.matches(PHONE_PATTERN, cleaned);
    }
    
    /**
     * Cleans phone number for storage
     */
    public static String cleanPhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }
    
    /**
     * Validates name format
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return Pattern.matches(NAME_PATTERN, name.trim());
    }
    
    /**
     * Validates password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Sanitizes input to prevent XSS
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
}
