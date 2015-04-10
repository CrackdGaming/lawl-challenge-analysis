package com.crackd.lawlchallenge.analyser

import com.crackd.lawlchallenge.Statistic
import play.api.libs.json.JsValue

/**
 * Created by trent ahrens on 4/9/15.
 */
trait Analyser[A] {
  val name: String = getClass.getCanonicalName

  def apply(json: JsValue): Statistic[A]
}
