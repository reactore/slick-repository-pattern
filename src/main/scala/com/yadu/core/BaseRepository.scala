package com.yadu.core

import slick.driver.PostgresDriver
import slick.lifted.{CanBeQueryCondition, Rep, TableQuery}

import scala.concurrent.Future
import scala.reflect._
import PostgresDriver.api._

/**
  * Created by yadu on 7/2/16.
  */

object DriverHelper {
  val user = "postgres"
  val url = "jdbc:postgresql://localhost:5432/LocalDB"
  val password = "admin"
  val jdbcDriver = "org.postgresql.Driver"
  val db = Database.forURL(url, user, password, driver = jdbcDriver)
}

trait BaseRepositoryComponent[T <: BaseTable[E], E <: BaseEntity] {
  def getById(id: Long) : Future[Option[E]]
  def getAll : Future[Seq[E]]
  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]
  def save(row: E) : Future[E]
  def deleteById(id: Long) : Future[Int]
  def updateById(id: Long, row: E) : Future[Int]
}

trait BaseRepositoryQuery[T <: BaseTable[E], E <: BaseEntity] {

  val query: PostgresDriver.api.type#TableQuery[T]

  def getByIdQuery(id: Long) = {
    query.filter(_.id === id).filter(_.isDeleted === false)
  }

  def getAllQuery = {
    query.filter(_.isDeleted === false)
  }

  def filterQuery[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
    query.filter(expr).filter(_.isDeleted === false)
  }

  def saveQuery(row: E) = {
    query returning query += row
  }

  def deleteByIdQuery(id: Long) = {
    query.filter(_.id === id).map(_.isDeleted).update(true)
  }

  def updateByIdQuery(id: Long, row: E) = {
    query.filter(_.id === id).filter(_.isDeleted === false).update(row)
  }

}

abstract class BaseRepository[T <: BaseTable[E], E <: BaseEntity : ClassTag](clazz: TableQuery[T]) extends BaseRepositoryQuery[T, E] with BaseRepositoryComponent[T,E] {

  val clazzTable: TableQuery[T] = clazz
  lazy val clazzEntity = classTag[E].runtimeClass
  val query: PostgresDriver.api.type#TableQuery[T] = clazz
  val db: PostgresDriver.backend.DatabaseDef = DriverHelper.db

  def getAll: Future[Seq[E]] = {
    db.run(getAllQuery.result)
  }

  def getById(id: Long): Future[Option[E]] = {
    db.run(getByIdQuery(id).result.headOption)
  }

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
    db.run(filterQuery(expr).result)
  }

  def save(row: E) = {
    db.run(saveQuery(row))
  }

  def updateById(id: Long, row: E) = {
    db.run(updateByIdQuery(id, row))
  }

  def deleteById(id: Long) = {
    db.run(deleteByIdQuery(id))
  }

}


abstract class WorkflowRepository[T <: WorkflowBaseTable[E], E <: WorkflowBaseEntity : ClassTag](clazz: TableQuery[T]) extends BaseRepository[T, E](clazz) {
  def approve(id:Long): Future[Int] = {
    db.run(getByIdQuery(id).map(_.isApproved).update(true))
  }
}