package com.charlieworld.benchmark.dbms.warm

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.pipe

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Future, Promise}

object OperatorActor {

  object Messages {
    case class Run()
    case class Result(elapsed: Duration)
  }

  def props(f: Unit => Future[Any], goal: Duration, numInRow: Int, callback: Promise[Duration]): Props =
    Props(classOf[OperatorActor], f, goal, numInRow, callback)
}

class OperatorActor(f: Unit => Future[Any],
                    goal: Duration,
                    numInRow: Int,
                    callback: Promise[Duration]) extends Actor with ActorLogging {

  import context.dispatcher

  private var numSatisfied = 0

  override def receive: Receive = {
    case Run() =>
      val start = System.currentTimeMillis()
      f() map (_ => Result((System.currentTimeMillis() - start).millis)) pipeTo self

    case Result(elapsed) =>
      log.info(s"elapsed $elapsed")
      if (!callback.isCompleted) {
        if (elapsed.lt(goal)) {
          numSatisfied += 1
          if (numSatisfied >= numInRow) {
            callback.success(elapsed)
          }
        } else {
          numSatisfied = 0
        }
      }

    case msg =>
      log.error(s"Received unexpected message $msg")
  }
}
