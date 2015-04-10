package com.crackd.lawlchallenge.analysis.analyser

import com.crackd.lawlchallenge.analysis.analyzer.GameAnalyzer
import com.crackd.lawlchallenge.analysis.model.GameModels._
import com.crackd.test.{JsonLoader, UnitSpec}

/**
 * Created by trent ahrens on 4/9/15.
 */
class GameAnalyzerTest extends UnitSpec {
  it should "generate correct result" in {
    val input = JsonLoader.load("/championdeaths-input.json")
    val sut = GameAnalyzer()
    val actual = sut(input)
    val expected = Game(1)
    actual shouldBe expected
  }
}
