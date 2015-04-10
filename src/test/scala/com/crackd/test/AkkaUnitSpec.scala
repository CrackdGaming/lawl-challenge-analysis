package com.crackd.test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll

/**
 * Created by trent ahrens on 4/10/15.
 */
abstract class AkkaUnitSpec(implicit system: ActorSystem = ActorSystem()) extends TestKit(system) with ImplicitSender
  with UnitSpec with BeforeAndAfterAll {
  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
}
