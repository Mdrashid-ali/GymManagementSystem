public class TestResetToken {
  public static void main(String[] args) throws Exception {
    com.fitTrackPro.service.passwordResetService svc = new com.fitTrackPro.service.passwordResetService();
    String token = svc.createResetToken("jane.member@email.com");
    System.out.println("tokenCreated=" + (token != null));
    System.out.println("valid=" + svc.isValidToken(token));
    try (java.sql.Connection c = com.fitTrackPro.config.DBConnection.getConnection();
         java.sql.PreparedStatement ps = c.prepareStatement("SELECT created_at, expires_at, CURRENT_TIMESTAMP AS db_now, expires_at > CURRENT_TIMESTAMP AS valid_time FROM password_reset_tokens WHERE token=?")) {
      ps.setString(1, token);
      try (java.sql.ResultSet rs = ps.executeQuery()) {
        if (rs.next()) System.out.println("created=" + rs.getTimestamp("created_at") + " expires=" + rs.getTimestamp("expires_at") + " db_now=" + rs.getTimestamp("db_now") + " validTime=" + rs.getInt("valid_time"));
      }
    }
  }
}