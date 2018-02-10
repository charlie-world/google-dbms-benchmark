package com.charlieworld.benchmark.dbms.gatling

import io.gatling.core.protocol.ProtocolComponents
import io.gatling.core.session.Session

case class TsdbComponents(dbmsProtocol: TsdbProtocol) extends ProtocolComponents {

  def onStart: Option[Session => Session] = None

  def onExit: Option[Session => Unit] = None
}
