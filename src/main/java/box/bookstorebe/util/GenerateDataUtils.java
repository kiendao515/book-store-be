package box.bookstorebe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GenerateDataUtils {
    private GenerateDataUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateUniqueString(String input) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();

        try {
            // Generate a hash of the input to create a consistent unique string
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());
            String hashString = Base64.getEncoder().encodeToString(hashBytes);

            // Use the hash to select characters from SALTCHARS
            for (int i = 0; i < 10; i++) {
                int index = (hashString.charAt(i) & 0xFF) % SALTCHARS.length();
                salt.append(SALTCHARS.charAt(index));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return salt.toString();
    }

}
