package com.crackd.lawlchallenge

import akka.actor.{Actor, ActorRef, ActorRefFactory}
import akka.pattern._
import akka.util.Timeout
import com.crackd.lawlchallenge.actor.AnalysisEngine.{Analysis, GetAnalysis}
import spray.routing._

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
 * Created by trent ahrens on 4/13/15.
 */
class RestApi(analysisEngine: ActorRef) extends Actor with HttpService {
  import context._

  override implicit def actorRefFactory: ActorRefFactory = context

  implicit val timeout = Timeout(60 seconds)

  implicit val rSettings = RoutingSettings.default(context)

  override def receive: Receive = runRoute {
    path("data") {
      get {
        complete {
          (analysisEngine ? GetAnalysis) map {
            case Analysis(json) => json.toString()
          }
        }
      }
    }
  }
}
