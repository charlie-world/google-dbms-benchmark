package com.charlieworld.benchmark.dbms.warm

import akka.actor.ActorSystem

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

final class WarmupProcess {

  def start(goal: FiniteDuration,
            rate: FiniteDuration,
            numInRow: Int)
           (f: Unit => Future[Any])
           (implicit actorSystem: ActorSystem): Future[Duration] = {
    val promise = Promise[Duration]
    val actor = actorSystem.actorOf(OperatorActor.props(f, goal, numInRow, promise))
    actorSystem.scheduler.schedule(0.millis, rate, actor, Run())(actorSystem.dispatcher)
    promise.future
  }
}
