package cl.untec.biblioteca.service;

import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 con sal aleatoria por usuario; comparación constante. */
public final class Passwords {
  private Passwords() {}

  public static String hash(String password) {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt)
        + ":"
        + Base64.getEncoder().encodeToString(derive(password, salt));
  }

  public static boolean verify(String password, String stored) {
    try {
      String[] p = stored.split(":");
      return MessageDigest.isEqual(
          derive(password, Base64.getDecoder().decode(p[0])), Base64.getDecoder().decode(p[1]));
    } catch (Exception e) {
      return false;
    }
  }

  private static byte[] derive(String p, byte[] salt) {
    try {
      return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
          .generateSecret(new PBEKeySpec(p.toCharArray(), salt, 120000, 256))
          .getEncoded();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
