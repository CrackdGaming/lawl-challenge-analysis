package com.crackd.lawlchallenge.actor

import akka.actor.Actor
import com.crackd.lawlchallenge.actor.AnalysisAccumulator._
import play.api.libs.json.{Format, JsValue, Json}

import scalaz.Monoid

/**
 * Created by trent ahrens on 4/14/15.
 */

object AnalysisAccumulator {
  case class Add[T](v: T)
  case object Added
  case object Serialize
  case class Serialized(json: JsValue, name: String)
  case class Deserialize(json: JsValue)
}

class AnalysisAccumulator[T](m: Monoid[T], f: Format[T], name: String) extends Actor {
  var accumulator: T = m.zero

  override def receive: Receive = {
    case Add(v) =>
      val vv: T = v.asInstanceOf[T]
      accumulator = m.append(vv, accumulator)
      sender() ! Added
    case Serialize => sender() ! Serialized(Json.toJson(accumulator)(f), name)
    case Deserialize(json) => accumulator = json.as[T](f)
  }
}
