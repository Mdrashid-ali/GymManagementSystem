package com.fitTrackPro.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Password Utility Class
 * Handles password hashing and verification using SHA-256 with salt
 * (Replaces BCrypt to avoid external dependency issues)
 */
public class passwordUtil {
    
    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    
    /**
     * Hashes a plain text password with a random salt
     * @param plainPassword The plain text password
     * @return Salted hash in format: salt:hash
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            String saltString = Base64.getEncoder().encodeToString(salt);
            String hash = hashWithSalt(plainPassword, salt);
            
            // Return salt and hash combined
            return saltString + ":" + hash;
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verifies a plain text password against a stored hash
     * @param plainPassword The plain text password to verify
     * @param storedHash The stored hash in format salt:hash
     * @return true if password matches
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        
        try {
            // Split stored hash into salt and hash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String saltString = parts[0];
            String originalHash = parts[1];
            
            // Decode salt
            byte[] salt = Base64.getDecoder().decode(saltString);
            
            // Hash the provided password with the same salt
            String newHash = hashWithSalt(plainPassword, salt);
            
            // Compare hashes
            return originalHash.equals(newHash);
            
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    /**
     * Helper method to hash password with salt
     */
    private static String hashWithSalt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
    
    /**
     * Validates password strength
     * Password must be at least 8 characters and contain:
     * - Uppercase letter
     * - Lowercase letter
     * - Digit
     * - Special character
     */
    public static boolean isStrongPassword(String password) {
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
     * Gets password strength requirements message
     */
    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain at least one " +
               "uppercase letter, one lowercase letter, one digit, and one special character.";
    }
    
    /**
     * Generates a secure random token for password reset
     */
    public static String generateResetToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Generates a random temporary password
     */
    public static String generateTemporaryPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String allChars = upperCase + lowerCase + digits + special;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one of each required character type
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));
        
        // Fill remaining 4 characters randomly
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
}