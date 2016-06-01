package com.yadu.core

import slick.driver.PostgresDriver.api._

import scala.reflect._

/**
  * Created by yadu on 7/2/16.
  */
trait BaseEntity {
  val id: Long
  val isDeleted: Boolean
}

trait WorkflowBaseEntity extends BaseEntity{
  val isApproved:Boolean
}

abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends Table[E](tag, schemaName, tableName) {
  val classOfEntity = classTag[E].runtimeClass
  val id: Rep[Long] = column[Long]("Id", O.PrimaryKey, O.AutoInc)
  val isDeleted: Rep[Boolean] = column[Boolean]("IsDeleted", O.Default(false))
}


abstract class WorkflowBaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends BaseTable[E](tag, schemaName, tableName) {

  val isApproved: Rep[Boolean] = column[Boolean]("IsApproved")

}
