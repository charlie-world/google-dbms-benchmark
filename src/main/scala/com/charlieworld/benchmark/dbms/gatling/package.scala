package com.charlieworld.benchmark.dbms

import io.gatling.core.action.builder.ActionBuilder

package object gatling {

  def read(queryRangeInDays: Int = 1): ActionBuilder =
    new actions.read.TsdbActionBuilder(queryRangeInDays)

  def write: ActionBuilder =
    new actions.write.TsdbActionBuilder()
}
