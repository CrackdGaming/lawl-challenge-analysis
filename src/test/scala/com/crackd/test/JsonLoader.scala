package com.crackd.test

import org.apache.commons.io.IOUtils
import play.api.libs.json._

/**
*  Created by trent ahrens on 4/9/15.
*/
object JsonLoader {
  def load(resourceName: String) = Json.parse(IOUtils.toString(getClass.getResource(resourceName)))
}
