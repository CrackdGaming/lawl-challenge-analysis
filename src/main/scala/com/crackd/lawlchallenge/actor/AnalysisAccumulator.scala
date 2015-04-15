package com.crackd.lawlchallenge.actor

import akka.actor.Actor
import com.crackd.lawlchallenge.MutableMonoid
import com.crackd.lawlchallenge.actor.AnalysisAccumulator._
import play.api.libs.json.{Format, JsValue, Json}

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

class AnalysisAccumulator[T](m: MutableMonoid[T], f: Format[T], name: String) extends Actor {
  var accumulator: T = m.zero

  override def receive: Receive = {
    case Add(v) =>
      val vv: T = v.asInstanceOf[T]
      accumulator = m.+=(accumulator, vv)
      sender() ! Added
    case Serialize => sender() ! Serialized(Json.toJson(accumulator)(f), name)
    case Deserialize(json) => accumulator = json.as[T](f)
  }
}
