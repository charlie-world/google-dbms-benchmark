package com.charlieworld.benchmark.dbms

import com.charlieworld.benchmark.dbms.simulation.SpannerTsdbSimulation
import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object Main {

  def main(args: Array[String]) {
    val simClass = classOf[SpannerTsdbSimulation].getName

    val props = new GatlingPropertiesBuilder
    props.simulationClass(simClass)
    props.noReports()
    props.mute()
    Gatling.fromMap(props.build + ("gatling.data.console.light" -> true))
    Thread.sleep(1000 * 60 * 10)
  }
}
