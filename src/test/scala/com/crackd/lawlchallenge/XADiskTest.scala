package com.crackd.lawlchallenge

import org.scalatest.{FlatSpec, Matchers}
import org.xadisk.bridge.proxies.interfaces.XAFileSystemProxy
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration

/**
 * Created by trent ahrens on 4/9/15.
 */
class XADiskTest extends FlatSpec with Matchers {
  val dir = "xadisk"
  val config = new StandaloneFileSystemConfiguration(dir, "instance-1")
  val xaf = XAFileSystemProxy.bootNativeXAFileSystem(config)
  xaf.waitForBootup(10000L)
}
