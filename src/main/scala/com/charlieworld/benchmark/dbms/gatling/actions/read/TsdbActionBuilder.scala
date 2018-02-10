package com.charlieworld.benchmark.dbms.gatling.actions.read

import com.charlieworld.benchmark.dbms.gatling.{TsdbComponents, TsdbProtocol}
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry
import io.gatling.core.structure.ScenarioContext

class TsdbActionBuilder(queryRangeInDays: Int) extends ActionBuilder {

  private def components(protocolComponentsRegistry: ProtocolComponentsRegistry) =
    protocolComponentsRegistry.components(TsdbProtocol.protocolKey)

  override def build(ctx: ScenarioContext, next: Action): Action = {
    import ctx._
    val statsEngine = coreComponents.statsEngine
    val dbmsComponents = components(protocolComponentsRegistry).asInstanceOf[TsdbComponents]
    new TsdbAction(dbmsComponents.dbmsProtocol, queryRangeInDays, statsEngine, next)
  }
}
