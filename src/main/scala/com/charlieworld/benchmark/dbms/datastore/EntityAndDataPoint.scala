package com.charlieworld.benchmark.dbms.datastore

import com.charlieworld.benchmark.dbms.DataPoint
import com.spotify.asyncdatastoreclient.Entity
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by jay on 20/11/2017.
  */
trait EntityAndDataPoint extends DatastoreKey {

  implicit class EntityToDataPointOps(self: Entity) {
    def toDataPoint: DataPoint = {
      val keyTokens = self.getKey.getName.split("#")
      DataPoint(keyTokens(0), self.getDouble("value"), new DateTime(keyTokens(1), DateTimeZone.UTC).getMillis)
    }
  }

  implicit class DataPointToEntityOps(self: DataPoint) {
    def toEntity: Entity = {
      val builder = Entity.builder(self.toKey)
      builder.property("value", self.value, false)
      builder.build()
    }
  }
}
