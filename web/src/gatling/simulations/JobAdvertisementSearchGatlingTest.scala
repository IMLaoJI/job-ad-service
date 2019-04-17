import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class JobAdvertisementSearchGatlingTest extends Simulation {

    val baseUrl = Option(System.getProperty("baseURL")) orElse Option(System.getenv("baseURL")) getOrElse """http://127.0.0.1:8080"""

    val httpConf = http
        .baseUrl(baseUrl)
        .inferHtmlResources()
        .acceptHeader("*/*")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
        .connectionHeader("keep-alive")
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:33.0) Gecko/20100101 Firefox/33.0")

    val scn = scenario("Test the jobsearch").repeat(5)(JobAdvertisementSearch.execute())

    val users = scenario("JobSeeker").exec(scn)

    setUp(
        users.inject(rampUsers(Integer.getInteger("users", 1)) during (Integer.getInteger("seconds", 1) seconds))
    ).protocols(httpConf)
}

object JobAdvertisementSearch {

    private val headers_http_authenticated = Map("Authorization" -> "Bearer ${bearer_token}")

    private val userFeeder = csv("data/users.csv").random

    private val occupationsFeeder = csv("data/occupations.csv").batch.random

    private val localityFeeder = csv("data/locality.csv").batch.random

    def searchJobAdvertisements() = {
        http("Search JobAdvertisements")
            .post("/jobadservice/api/jobAdvertisements/_search")
            .headers(headers_http_authenticated)
            .body(StringBody("""{
              "workloadPercentageMin": 10,
              "workloadPercentageMax": 100,
              "permanent": null,
              "companyName": null,
              "onlineSince": 30,
              "displayRestricted": false,
              "professionCodes":[{"type": "X28", "value": "${x28Code}"}, {"type": "AVAM", "value": "${avamCode}"}],
              "keywords":[],
              "communalCodes":["${communalCode}"],
              "cantonCodes":[]
            }""")).asJson
            .queryParam("page", "0")
            .queryParam("size", "20")
            .queryParam("sort", "score")
            .check(status.is(200))
    }

    def execute() = {
        feed(occupationsFeeder)
        .feed(localityFeeder)
        .feed(userFeeder)
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
            .exec(searchJobAdvertisements())
    }
}
