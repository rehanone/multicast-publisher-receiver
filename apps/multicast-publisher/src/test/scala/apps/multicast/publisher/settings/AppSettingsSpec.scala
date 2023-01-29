package apps
package multicast
package publisher
package settings

import cats.effect.IO
import com.comcast.ip4s.*
import model.authentication.Platform
import model.multicast.MulticastSettings
import settings.AppSettings.*
import munit.*
import org.http4s.implicits.*

import scala.concurrent.duration.*
class AppSettingsSpec extends CatsEffectSuite {

  test("load default config") {

    val loaded = AppSettings.load[IO].use {
      IO(_)
    }

    val expected = AppSettings(
      settings = Settings(
        stream = StreamSettings(tickInterval = 5.minutes, tickDelay = 5.seconds),
        alphavantage = Alphavantage(
          functions = Map(
            FUNCTION_TIME_SERIES_INTRADAY -> Function(
              baseUri = uri"https://www.alphavantage.co/query",
              queryParams = Map(
                "function"   -> "TIME_SERIES_INTRADAY",
                "interval"   -> "5min",
                "outputsize" -> "compact",
                "datatype"   -> "json",
                "apikey"     -> "demo"
              ),
              limitEntries = Some(30)
            )
          ),
          symbols = List(
            "AAPL",
            "MSFT",
            "W"
          )
        ),
        multicast = MulticastSettings(
          group = mip"239.10.10.10",
          port = port"5555",
          interfacePrefix = "en"
        ),
        encryption = Encryption(
          enabled = true,
          platform = Platform.Aws(
            keyArns = List("arn:aws:kms:us-west-1:093824298337:key/9ca1690b-b757-4300-a983-a5ab1072217c"),
            encryptionContext = Map(
              "name" -> "test-key"
            ),
            authentication = Platform.Aws.Authentication(
              awsRegion = "us-west-1",
              accessKeyId = "your_access_key_id",
              secretAccessKey = "your_secret_access_key"
            )
          )
        )
      )
    )

    loaded.assertEquals(expected)
  }
}
