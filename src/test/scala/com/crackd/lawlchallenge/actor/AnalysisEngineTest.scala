package com.crackd.lawlchallenge.actor

import akka.testkit.TestActorRef
import com.crackd.lawlchallenge.actor.AnalysisEngine.{Analyze, AnalysisComplete}
import com.crackd.test.AkkaUnitSpec

/**
 * Created by trent ahrens on 4/10/15.
 */
class AnalysisEngineTest extends AkkaUnitSpec {
  it should "track" in {
    val sut = TestActorRef[AnalysisEngine]
    sut ! Analyze
    expectMsg(AnalysisComplete)
  }
}
