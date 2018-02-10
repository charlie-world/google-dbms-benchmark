package com.charlieworld.benchmark.dbms.bigtable

import com.charlieworld.benchmark.dbms.DataPoint
import com.charlieworld.benchmark.dbms.bigtable.BigTableRepository
import com.charlieworld.benchmark.dbms.tools.Await
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

/**
  * Created by jay on 23/11/2017.
  */
class BigTableRepositorySpec extends FlatSpec with BigTableTestKit with Await with Matchers with BeforeAndAfterEach {

  import scala.concurrent.ExecutionContext.Implicits.global

  lazy val repository = new BigTableRepository()

  "BigTableRepository" should "read/write data points" ignore {
    val key = "test"
    val sample = Seq(DataPoint(key, 0, 0))
    repository.putDataPoints(sample).await()
    repository.getDataPoints(key, 0, 10, desc = true).await() shouldBe Seq(DataPoint(key, 0.0, 0))
  }
}
