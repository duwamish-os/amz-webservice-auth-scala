
import java.io.{FileOutputStream, PrintWriter}
import java.nio.file.{FileAlreadyExistsException, Files, Paths}

import com.typesafe.config.ConfigFactory
import spray.json.JsArray

import scala.concurrent.ExecutionContext.Implicits.global
import spray.json.DefaultJsonProtocol._

import spray.json._

/**
  * Created by prayagupd
  * on 7/17/17.
  */

object GetAuthenticationToken {

  val auth = new AmzTemporaryAuthentication

  val config = ConfigFactory.load("application.properties")

  def main(args: Array[String]): Unit = {

    //System.setProperty("javax.net.debug", "ssl")
    System.setProperty("https.protocols", "TLSv1.2")

    if(config.hasPath("auth.server.certs.store") && config.getString("auth.server.certs.store").nonEmpty) {
      System.setProperty("javax.net.ssl.trustStore", config.getString("auth.server.certs.store"))
    }

    auth.resources().map(res => {
      val roles = res.asInstanceOf[JsArray].elements.zipWithIndex.map { case (role, index) => {
        index -> (role.asJsObject.fields("Role").convertTo[String] -> role.asJsObject.fields("Principal").convertTo[String])
      }}.map { tuple => {
          tuple
      }}
      println("======================================================================")
      println(s"Please select one of the following role: [0 - ${roles.size -1}]      ")
      println("======================================================================")
      roles.foreach(tuple => println(s"[${tuple._1}] ${tuple._2._1}"))
      println("======================================================================")

      val roleToToken = scala.io.StdIn.readInt()

      if(roleToToken > roles.size) {
        println(s"[ERROR] There are only ${roles.size} roles.")
        auth.system.terminate()
      }

      val selected = roles(roleToToken)

      val tokenRequest =
        s"""
          {
           "Role": "${selected._2._1}",
           "Principal": "${selected._2._2}"
          }
        """.stripMargin

      println("[INFO] Requesting token for " + tokenRequest.parseJson)

      val tokenResponse = auth.getToken(tokenRequest)
      tokenResponse.map { response =>
        val credentialsPath = config.getString("auth.credentials.path")

        try {
          val path = Paths.get(credentialsPath)
          Files.createFile(path)
        } catch {
          case e: FileAlreadyExistsException => println("")
          case e: Throwable => e.printStackTrace()
        }

        println(s"[INFO] Writing to credentials file $credentialsPath")

        try {
          val writer = new PrintWriter(new FileOutputStream(credentialsPath, false))

          writer.write(
            s"""[${config.getString("auth.credentials.name")}]
aws_access_key_id=${response._1}
aws_secret_access_key=${response._2}
aws_session_token=${response._4}
aws_security_token=${response._4}""".stripMargin)

          println(s"[INFO] Updated public credentials to path $credentialsPath, will expire at ${response._3}")
          println("""
  .--.  .-"     "-.  .--.
 / .. \/  .-. .-.  \/ .. \
| |  '|  /   Y   \  |'  | |
| \   \  \ 0 | 0 /  /   / |
 \ '- ,\.-"`` ``"-./, -' /
  `'-' /_   ^ ^   _\ '-'`
      |  \._   _./  |
      \   \ `~` /   /
       '._ '-=-' _.'
          '~---~'
            """.stripMargin)
          writer.close()

        } catch {
          case e: Throwable => e.printStackTrace()
        }
      }
    })
  }

}
