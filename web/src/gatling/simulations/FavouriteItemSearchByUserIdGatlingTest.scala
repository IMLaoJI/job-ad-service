import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random


class FavouriteItemSearchByUserIdGatlingTest extends Simulation {

  val baseUrl = Option(System.getProperty("baseURL")) orElse Option(System.getenv("baseURL")) getOrElse """http://127.0.0.1:8080"""

  val httpConf = http
    .baseUrl(baseUrl)
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
    .connectionHeader("keep-alive")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

  // set up how many time the scenario should repeat
  val scnSearchByUserId = scenario("Test the favourite item search by user Id").repeat(1)(FavouriteItemSearch.feedUsers())
  val usersSearchByUserId = scenario("JobSeeker").exec(scnSearchByUserId)

  setUp(
    setUpScenario()
  ).protocols(httpConf)

  def setUpScenario() = {
    scnSearchByUserId.inject(
      // set up how many users during which timespan should perform the request
      rampUsers(1) during (1 minute)
    )
  }
}

object FavouriteItemSearch {

  private val headers_http_authenticated = Map("Authorization" -> "Bearer ${bearer_token}")
  private val userFeeder = csv("data/users.csv").random

  def searchJobFavourites() = {
    val randomNote = "note=note nr. "+ Random.nextInt(5)
    http("FavouriteItemByUserIdSearch")
      .get("/jobadservice/api/favourite-items/_search/byUserId")
      .headers(headers_http_authenticated)
      .queryParam("userId", "${userId}")
      .queryParam("page", "0")
      .queryParam("size", "20")
      .queryParam("query", "noteNr"+ Random.nextInt(5))
      .check(status.is(200))
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
      .exec(searchJobFavourites())
  }
}
