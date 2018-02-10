package com.charlieworld.benchmark.dbms.gatling.actions.read

import com.charlieworld.benchmark.dbms.gatling.TsdbProtocol
import com.charlieworld.benchmark.dbms.tools.Await
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.util.NameGen

import scala.util.Random

class TsdbAction(protocol: TsdbProtocol, queryRangeInDays: Int, val statsEngine: StatsEngine, val next: Action)
  extends ExitableAction
    with NameGen
    with Await {

  override def name: String =
    genName("GetDataPoints")

  override def execute(session: Session): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val start = System.currentTimeMillis
    val key = s"test_${Random.nextInt(100)}"
    val dpsF = protocol.repository.getDataPoints(key, 0, queryRangeInDays * 24 * 60 * 60 * 1000, desc = false)
    dpsF onComplete { dpsT =>
      val end = System.currentTimeMillis
      val timings = ResponseTimings(start, end)
      val status = if (dpsT.isSuccess) OK else KO
      statsEngine.logResponse(session, name, timings, status, None, None)
      next ! session
    }
  }
}
