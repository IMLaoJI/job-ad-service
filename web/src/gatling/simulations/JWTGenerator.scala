import java.sql.Timestamp
import java.time.LocalDateTime
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}

object JWTGenerator {

  private val secret = System.getProperty("secret")

  def generateToken(userId: String, firstName: String, lastName: String, email: String, langKey: String, externalId: String) = {
    Jwts.builder
      .claim("auth", "ROLE_JOBSEEKER_CLIENT")
      .claim("userId", userId)
      .claim("firstName", firstName)
      .claim("lastName", lastName)
      .claim("email", email)
      .claim("langKey", langKey)
      .claim("externalId", externalId)
      .claim("userProfileExtId", "XXX")
      .setExpiration(Timestamp.valueOf(LocalDateTime.now.plusMinutes(10)))
      .setSubject(externalId)
      .signWith(SignatureAlgorithm.HS512, secret)
      .compact
  }
}
