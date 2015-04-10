package com.crackd.lawlchallenge.analyser

import com.crackd.lawlchallenge.analyser.GameAnalysis.Game
import com.crackd.test.{JsonLoader, UnitSpec}

/**
 * Created by trent ahrens on 4/9/15.
 */
class GameAnalysisTest extends UnitSpec {
  it should "generate correct result" in {
    val input = JsonLoader.load("/championdeaths-input.json")
    val sut = GameAnalysis()
    val actual = sut(input)
    val expected = Game(1)
    actual shouldBe expected
  }
}
