
import java.io.{FileOutputStream, PrintWriter}
import java.nio.file.{Files, Paths}

import com.typesafe.config.ConfigFactory
import spray.json.JsArray

import scala.concurrent.ExecutionContext.Implicits.global
import spray.json.DefaultJsonProtocol._

/**
  * Created by prayagupd
  * on 7/17/17.
  */

object GetAuthenticationToken {

  val auth = new AmzTemporaryAuthentication

  val config = ConfigFactory.load("application.properties")

  def main(args: Array[String]): Unit = {

    println("Please select one of the following role: ")
    auth.resources().map(res => {
      val roles = res.asInstanceOf[JsArray].elements.zipWithIndex.map { case (role, index) => {
        index -> (role.asJsObject.fields("Role").convertTo[String] -> role.asJsObject.fields("Principal").convertTo[String])
      }}.map { tuple => {
          println(s"[${tuple._1}] ${tuple._2._1}")
          tuple
      }}
      val roleToToken = scala.io.StdIn.readInt()

      val selected = roles(roleToToken)

      val tokenRequest =
        s"""
          {
           "Role": "${selected._2._1}",
           "Principal": "${selected._2._2}"
          }
        """.stripMargin

      println("[INFO] " + tokenRequest)

      val tokenResponse = auth.getToken(tokenRequest)
      tokenResponse.map { response =>
        val credentialsPath = config.getString("auth.credentials.path")

        try {
          val path = Paths.get(credentialsPath)
          Files.createFile(path)
        } catch {
          case e: Throwable => println(e.getMessage)
        }

        println(s"Writing to credentials file $credentialsPath")

        val writer = new PrintWriter(new FileOutputStream(credentialsPath, false))

        writer.write(s"""[${config.getString("auth.credentials.name")}]
aws_access_key_id=${response._1}
aws_secret_access_key=${response._2}
aws_session_token=${response._4}
aws_security_token=${response._4}""".stripMargin)

        writer.close()

        println(s"[INFO] Updated public credentials to path $credentialsPath")
      }
    })
  }

}
