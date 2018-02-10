package com.charlieworld.benchmark.dbms.datastore

import com.google.cloud.datastore.testing.LocalDatastoreHelper
import com.spotify.asyncdatastoreclient.{Datastore, DatastoreConfig}
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.slf4j.LoggerFactory
import org.threeten.bp.Duration

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits

/**
  * Created by jay on 12/09/2017.
  */
trait DatastoreTestKit extends BeforeAndAfterEach { self: Suite =>

  private val log = LoggerFactory.getLogger(classOf[DatastoreTestKit])
  val localDatastoreHelper = LocalDatastoreHelper.create(1.0)

  private val host = s"http://localhost:${localDatastoreHelper.getPort}"
  private val project = localDatastoreHelper.getProjectId
  private val datastoreConfig = DatastoreConfig.builder()
    .host(host)
    .project(project)
    .build()

  protected implicit val datastore: Datastore = Datastore.create(datastoreConfig)

  implicit def executionContext: ExecutionContext = Implicits.global

  override def beforeEach(): Unit = {
    localDatastoreHelper.start()
    log.info(s"Started a local Datastore at $host")
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    try super.afterEach()
    finally localDatastoreHelper.stop(Duration.ofMinutes(1))
    log.info("Stopped a local Datastore")
  }
}
