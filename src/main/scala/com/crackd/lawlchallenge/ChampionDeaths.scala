package com.crackd.lawlchallenge

import play.api.libs.json._

/**
*  Created by trent ahrens on 4/9/15.
*/
object ChampionDeaths {
  def apply() = new ChampionDeaths()
}

class ChampionDeaths {
  def apply(json: JsValue): JsValue = null
}
