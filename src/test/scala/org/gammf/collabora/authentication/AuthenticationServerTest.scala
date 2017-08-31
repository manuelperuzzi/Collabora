package org.gammf.collabora.authentication

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Route
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.newmotion.akka.rabbitmq.{ConnectionActor, ConnectionFactory}
import org.gammf.collabora.communication.actors._
import org.gammf.collabora.database.actors.{ConnectionManagerActor, DBMasterActor}

class AuthenticationServerTest extends WordSpec with Matchers with ScalatestRouteTest {

  val dbConnectionActor: ActorRef = system.actorOf(Props[ConnectionManagerActor])
  val factory = new ConnectionFactory()
  val connection:ActorRef = system.actorOf(ConnectionActor.props(factory), "rabbitmq")
  val naming: ActorRef = system.actorOf(Props[RabbitMQNamingActor], "naming")
  val channelCreator: ActorRef = system.actorOf(Props[ChannelCreatorActor], "channelCreator")
  val publisherActor: ActorRef = system.actorOf(Props[PublisherActor], "publisher")
  val collaborationMemberActor:ActorRef = system.actorOf(Props(
    new CollaborationMembersActor(connection, naming, channelCreator, publisherActor)))
  val notificationActor: ActorRef = system.actorOf(Props(new NotificationsSenderActor(connection, naming, channelCreator, publisherActor,system)))
  val dbMasterActor:ActorRef = system.actorOf(Props.create(classOf[DBMasterActor], system, notificationActor,collaborationMemberActor))
  val subscriber:ActorRef = system.actorOf(Props[SubscriberActor], "subscriber")
  val updatesReceiver:ActorRef = system.actorOf(Props(
    new UpdatesReceiverActor(connection, naming, channelCreator, subscriber, dbMasterActor)), "updates-receiver")

  AuthenticationServer.start(system, dbMasterActor)


  "The authentication server" should {

    "authenticate the user" in {
      Get("/login") ~> addCredentials(BasicHttpCredentials("maffone", "admin")) ~> AuthenticationServer.route ~> check {
        responseAs[String] shouldEqual "OK"
      }
    }

    "reject empty credentials" in {
      Get("/login") ~> Route.seal(AuthenticationServer.route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("login"), Map("charset" -> "UTF-8"))
      }
    }

    "not authenticate user if password is wrong" in {
      Get("/login") ~> addCredentials(BasicHttpCredentials("maffone", "not_maffone_password")) ~>
        Route.seal(AuthenticationServer.route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("login"), Map("charset" -> "UTF-8"))
      }
    }

    "not authenticate user if username not exists" in {
      Get("/login") ~> addCredentials(BasicHttpCredentials("wrong_username", "password")) ~>
        Route.seal(AuthenticationServer.route) ~> check {
        status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
        header[`WWW-Authenticate`].get.challenges.head shouldEqual HttpChallenge("Basic", Some("login"), Map("charset" -> "UTF-8"))
      }
    }
  }
}
