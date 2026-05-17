import java.sql.*;
public class CheckReset {
  public static void main(String[] args) throws Exception {
    try (Connection c = com.fitTrackPro.config.DBConnection.getConnection(); Statement s = c.createStatement()) {
      try (ResultSet rs = s.executeQuery("DESCRIBE password_reset_tokens")) {
        while (rs.next()) System.out.println(rs.getString(1)+" | "+rs.getString(2)+" | Null="+rs.getString(3)+" | Default="+rs.getString(5));
      }
      System.out.println("-- latest tokens --");
      try (ResultSet rs = s.executeQuery("SELECT token_id,user_id,LEFT(token,10) AS token_start,expires_at,used_at,created_at, expires_at > CURRENT_TIMESTAMP AS valid_time FROM password_reset_tokens ORDER BY token_id DESC LIMIT 5")) {
        while (rs.next()) System.out.println(rs.getInt("token_id")+" | user="+rs.getInt("user_id")+" | "+rs.getString("token_start")+" | exp="+rs.getTimestamp("expires_at")+" | used="+rs.getTimestamp("used_at")+" | created="+rs.getTimestamp("created_at")+" | validTime="+rs.getInt("valid_time"));
      }
      try (ResultSet rs = s.executeQuery("SELECT CURRENT_TIMESTAMP AS db_now")) { if (rs.next()) System.out.println("db_now="+rs.getTimestamp("db_now")); }
    }
  }
}