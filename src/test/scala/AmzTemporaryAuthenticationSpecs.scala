import org.scalatest.{FunSuite, Matchers}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
  * Created by prayagupd
  * on 6/2/17.
  */

class AmzTemporaryAuthenticationSpecs extends FunSuite with Matchers {

  val auth = new AmzTemporaryAuthentication

  //FIXME stupid test
  test("connects to Amz Resources") {

    val response: Future[JsValue] = auth.resources()

    response.map { r =>
      r.asJsObject.fields.size should be > 0

      println(s"[INFO] ${r.asJsObject}")
    }
  }

  test("tokens") {

    auth.getToken(
      """{
         "Role":"arn:aws:iam::accountId:role/SomeRole",
        "Principal":"arn:aws:iam::accountId:saml-provider/DWM"
        }""".stripMargin)

    Thread.sleep(20000)
  }

  test("test future") {
    val r = auth.testFuture()

    Thread.sleep(3000)
    r.foreach(x => println("[INFO]"+x))
  }

}
