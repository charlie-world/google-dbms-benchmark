package com.charlieworld.benchmark.dbms.gatling

import akka.actor.ActorSystem
import com.charlieworld.benchmark.dbms.Repository
import io.gatling.core
import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}

case class TsdbProtocol(repository: Repository) extends Protocol {
  type Components = TsdbComponents
}

object TsdbProtocol {

  val protocolKey: ProtocolKey = new ProtocolKey {

    type Protocol = TsdbProtocol
    type Components = TsdbComponents

    override def protocolClass: Class[core.protocol.Protocol] =
      classOf[TsdbProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): TsdbProtocol =
      throw new IllegalStateException("Can't provide a default value for TsdbProtocol")

    override def newComponents(system: ActorSystem, coreComponents: CoreComponents): TsdbProtocol => TsdbComponents = {
      protocol => TsdbComponents(protocol)
    }
  }
}
