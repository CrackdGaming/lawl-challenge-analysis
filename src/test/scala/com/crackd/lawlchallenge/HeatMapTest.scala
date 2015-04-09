package com.crackd.lawlchallenge

import com.crackd.test.JsonLoader
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json.Json

/**
*  Created by trent ahrens on 4/9/15.
*/
class HeatMapTest extends FlatSpec with Matchers {
  it should "generate correct json result" in {
    val input = JsonLoader.load("/heatmap-input.json")
    val sut = HeatMapAnalysis()
    val actual = sut(input)
    val expected = JsonLoader.load("/heatmap-output.json")
    actual shouldBe expected
  }

  it should "skip event types with no position" in {
    val input = JsonLoader.load("/heatmap-input-noposition.json")
    val sut = HeatMapAnalysis()
    val actual = sut(input)
    val expected = Json.toJson(List.empty[String])
    actual shouldBe expected
  }
}
