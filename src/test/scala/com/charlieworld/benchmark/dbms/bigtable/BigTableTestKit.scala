package com.charlieworld.benchmark.dbms.bigtable

import com.google.cloud.bigtable.hbase.BigtableConfiguration
import org.apache.hadoop.hbase.client.Connection

/**
  * Created by jay on 23/11/2017.
  */
trait BigTableTestKit {

  protected val projectId: String = "test-project"
  protected val instanceId: String = "dbms-benchmark"
  private lazy val conf = BigtableConfiguration.configure(projectId, instanceId)

  protected implicit lazy val bigtableConnection: Connection =
    BigtableConfiguration.connect(conf)
}