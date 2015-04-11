package com.crackd.lawlchallenge.analysis.analyser

import com.crackd.lawlchallenge.analysis.analyzer.ParticipantAnalyzer
import com.crackd.test.{JsonLoader, UnitSpec}
import play.api.libs.json.Json

/**
 * Created by trent ahrens on 4/11/15.
 */
class ParticipantAnalyzerTest extends UnitSpec {
  it should "generate correct result" in {
    val input = JsonLoader.load("/participant-input.json")
    val sut = new ParticipantAnalyzer()
    val actual = Json.toJson(sut(input))
    val expected = JsonLoader.load("/participant-output.json")
    actual shouldBe expected
  }
}
