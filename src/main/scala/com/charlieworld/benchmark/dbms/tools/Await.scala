package com.charlieworld.benchmark.dbms.tools

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by jay on 12/09/2017.
  */
trait Await {

  implicit class AwaitFutureOps[A](fa: Future[A]) {
    def await(timeout: Duration = 10.seconds): A = concurrent.Await.result(fa, timeout)
  }
}
