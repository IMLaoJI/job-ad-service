import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class FavouriteItemCreateGatlingTest extends Simulation {

  val baseUrl = Option(System.getProperty("baseURL")) orElse Option(System.getenv("baseURL")) getOrElse """http://127.0.0.1:8080"""

  val httpConf = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
    .connectionHeader("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

  // set the desired value of repetitions
  val scnCreate = scenario("Test the favourite item creation").repeat(1)(FavouriteItemCreate.feedUsers())
  val usersCreate = scenario("JobSeeker").exec(scnCreate)

  setUp(
    setUpScenario()
  ).protocols(httpConf)

  def setUpScenario() = {
    // set how many users during which time span should perform the requests
        usersCreate.inject(
          rampUsers(1) during (1 minutes) // 3
        )
  }
}

object FavouriteItemCreate {

  private val headers_http_authenticated = Map("Authorization" -> "Bearer ${bearer_token}")

  private val userFeeder = csv("data/users.csv").random
  private val jobAdFeeder = csv("data/job-advertisements.csv").random

  def createJobFavouriteItems() = {
    feed(jobAdFeeder)
      .exec { session =>
        session.set("id", session("id").as[String])
      }
      .exec(postRequest())
  }

  def postRequest() ={
    var randomString = Iterator.continually(Map("randstring" -> ( "noteNr" + Random.nextInt(5) )))
    feed(randomString)
      .exec(http("FavouriteItemByUserIdSearch")
        .post("/jobadservice/api/favourite-items")
        .headers(headers_http_authenticated)
        .body(StringBody("""{"note":"${randstring}", "userId": "${userId}", "jobAdvertisementId":"${id}"}""")).asJson
        .check(status.is(201)))
  }

  def feedUsers() = {
    feed(userFeeder)
      .exec { session =>
        val userId = session("userId").as[String]
        val firstName = session("firstName").as[String]
        val lastName = session("lastName").as[String]
        val email = session("email").as[String]
        val externalId = session("externalId").as[String]
        val langKey = session("langKey").as[String]
        val token = JWTGenerator.generateToken(userId, firstName, lastName, email, langKey, externalId)
        session.set("bearer_token", token)
      }
      .exec(createJobFavouriteItems())
  }
}
