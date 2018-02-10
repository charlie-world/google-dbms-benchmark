package com.charlieworld.benchmark.dbms

import scala.concurrent.Future

/**
  * Created by jay on 01/09/2017.
  */
trait Repository {

  def getDataPoints(key: String,
                    start: Long,
                    end: Long,
                    desc: Boolean): Future[Seq[DataPoint]]

  def putDataPoints(dps: Seq[DataPoint]): Future[Unit]
}
