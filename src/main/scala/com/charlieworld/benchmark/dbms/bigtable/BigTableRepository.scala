package com.charlieworld.benchmark.dbms.bigtable

import com.charlieworld.benchmark.dbms.{DataPoint, Repository}
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, Put, Scan}
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, blocking}

/**
  * Created by jay on 23/11/2017.
  */
class BigTableRepository()(implicit ec: ExecutionContext, connection: Connection) extends Repository with BigTableKey {

  val table = connection.getTable(TableName.valueOf("dbms-benchmark"))
  val columnFamily = "dp"
  val column = "dp"

  implicit def str2Bytes(s: String): Array[Byte] =
    Bytes.toBytes(s)

  implicit def bytes2Str(bytes: Array[Byte]): String =
    Bytes.toString(bytes)

  override def getDataPoints(key: String, start: Long, end: Long, desc: Boolean): Future[Seq[DataPoint]] = {
    val scan = new Scan(rowKey(key, start), rowKey(key, end))
    scan.addColumn(columnFamily, column)
    val scanner = table.getScanner(scan)
    val results = scanner.asScala
    Future {
      blocking {
        try {
          val dps = for {
            res <- results
            cell <- res.getColumnCells(columnFamily, column).asScala
            (key, timestamp) <- parseRowKey(cell.getRowArray).toOption
          } yield DataPoint(key, Bytes.toLong(cell.getValueArray), timestamp)
          dps.toSeq
        } finally {
          scanner.close()
        }
      }
    }
  }

  override def putDataPoints(dps: Seq[DataPoint]): Future[Unit] = {
    val puts =
      for {
        dp <- dps
        k = rowKey(dp.key, dp.timestamp)
        p = new Put(Bytes.toBytes(k)).addColumn(columnFamily, column, Bytes.toBytes(dp.value))
      } yield p
    Future {
      blocking {
        table.put(puts.asJava)
      }
    }
  }
}
