package com.crackd.lawlchallenge.helper

import com.crackd.lawlchallenge.gametypes._
import play.api.libs.json.{JsObject, JsNull, JsArray, JsValue}

import scala.language.implicitConversions

/**
*  Created by trent ahrens on 4/9/15.
*/

class Events(s: Seq[JsValue]) extends Seq[JsValue] {
  def withPositions = filter(j => (j \ "position") != JsNull)

  def withType(et: EventType) = filter(j => (j \ "eventType").as[String] == et)

  override def length: ParticipantId = s.length

  override def apply(idx: ParticipantId): JsValue = s(idx)

  override def iterator: Iterator[JsValue] = s.iterator
}

object GameDataFinders {
  class RichGameData(json: JsValue) {
    def championIdForParticipantId(id: ParticipantId): ChampionId =
      participantById(id)
        .map(j => (j \ "championId").as[Int])
        .getOrElse(0)

    def participantTeamId(id: ParticipantId): TeamId =
      participantById(id)
        .map(j => (j \ "teamId").as[Int])
        .getOrElse(0)

    def participantById(id: ParticipantId): Option[JsObject] =
      participants.value.find(v => (v \ "participantId").as[Int] == id).map(_.as[JsObject])

    def events: Events = new Events((json \ "timeline" \ "frames" \\ "events")
      .filter(_ != JsNull).asInstanceOf[Seq[JsArray]].flatMap(_.value))

    def participants: JsArray = (json \ "participants").as[JsArray]
  }

  implicit def richGameData(json: JsValue): RichGameData = new RichGameData(json)
}
