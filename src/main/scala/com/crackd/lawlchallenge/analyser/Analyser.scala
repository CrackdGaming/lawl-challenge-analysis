package com.crackd.lawlchallenge.analyser

import com.crackd.lawlchallenge.Statistic
import play.api.libs.json.JsValue

/**
 * Created by trent ahrens on 4/9/15.
 */
abstract class Analyser[A] {
  def apply(json: JsValue): Statistic[A]
}
