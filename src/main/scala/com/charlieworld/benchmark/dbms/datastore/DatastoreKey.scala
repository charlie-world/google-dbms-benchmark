package com.charlieworld.benchmark.dbms.datastore

import com.charlieworld.benchmark.dbms.DataPoint
import com.spotify.asyncdatastoreclient.Key
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by jay on 20/11/2017.
  */
trait DatastoreKey {

  def datastoreKey(kind: String, key: String, timestamp: Long): Key =
    Key.builder(kind, s"$key#${new DateTime(timestamp, DateTimeZone.UTC)}").build

  implicit class DataPointToKeyOps(self: DataPoint) {

    def toKey: Key =
      datastoreKey(datastoreKind, self.key, self.timestamp)
  }
}
