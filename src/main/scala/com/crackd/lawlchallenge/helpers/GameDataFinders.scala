package com.crackd.lawlchallenge.helpers

import com.crackd.lawlchallenge.gametypes._
import play.api.libs.json.{JsNull, JsArray, JsValue}

import scala.language.implicitConversions

/**
 * Created by tahrens on 4/9/15.
 */
object GameDataFinders {

  type Events = Seq[JsValue]

  class RichGameData(json: JsValue) {
    def championIdForParticipantId(id: ParticipantId): ChampionId = ((json \ "participants").as[JsArray].value.filter(v => (v \ "participantId").as[Int] == id).head \ "championId").as[Int]

    def events: Events = (json \ "timeline" \ "frames" \\ "events").asInstanceOf[Seq[JsArray]].flatMap(_.value)
  }

  implicit def richGameData(json: JsValue): RichGameData = new RichGameData(json)

  class RichEvents(e: Events) {
    def withPositions = e.filter(j => (j \ "position") != JsNull)

    def withType(et: EventType) = e.filter(j => (j \ "eventType").as[String] == et)
  }

  implicit def richEvents(e: Events): RichEvents = new RichEvents(e)
}
