package com.crackd.lawlchallenge.actor

import play.api.libs.json.JsValue

/**
 * Created by trent ahrens on 4/10/15.
 */
object Bus {
  case class GameDataAvailable(json: JsValue)
}
