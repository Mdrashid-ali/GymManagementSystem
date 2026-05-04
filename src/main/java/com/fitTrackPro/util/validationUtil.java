package com.fitTrackPro.util;

import java.util.regex.Pattern;

public final class validationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");
    private validationUtil() {}
    
    public static boolean isBlank(String value) { 
    	return value == null || value.trim().isEmpty(); }
    
    public static boolean isValidEmail(String email) { 
    	return !isBlank(email) && EMAIL_PATTERN.matcher(email.trim()).matches(); }
    
    public static boolean isStrongPassword(String password) { 
    	return password != null && STRONG_PASSWORD_PATTERN.matcher(password).matches(); }
    
    public static boolean isValidPhone(String phone) { 
    	return !isBlank(phone) && phone.replaceAll("[\\s()+-]", "").matches("\\d{7,15}"); }
}