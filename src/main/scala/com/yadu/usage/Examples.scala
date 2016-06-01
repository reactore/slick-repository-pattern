package com.yadu.usage

import java.util.concurrent.Executor

import com.yadu.repository.{User, UserRepository}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import ExecutionContext.Implicits.global

/**
  * Created by yadu on 7/2/16.
  */

object UserExampleApp extends App {

  /** User ID as 0 since the ID Column is created with BigSerial, which will do auto increment on ID **/
  val randomName = "yadu" + Random.nextInt(100)
  val user = User(0L, randomName, "abc@xyz.com", "password123", false, 1L)

  //ToDo: Remove the Thread.sleep
  //Thread.sleep added only to run the Save->Get->Update->Delete operations in the same order.
  val randomUserFuture =

    for {
      save <- UserOperations.saveToDB(user)
      _ = Thread.sleep(1000)
      allUsers <- UserOperations.getAllFromDB
      _ = Thread.sleep(1000)
      size = allUsers.size
      randomUser = allUsers(Random.nextInt(size))
      u = UserOperations.getUserById(randomUser.id)
      _ = Thread.sleep(1000)
      update = UserOperations.updateUser(randomUser.id, randomUser)
      _ = Thread.sleep(1000)
      delete = UserOperations.deleteUser(randomUser.id)
    } yield delete

  Thread.sleep(5000)
}


object UserOperations {

  val userRepo = new UserRepository

  def saveToDB(user: User): Future[Unit] = {
    println("------------")
    println("Saving to DB")
    println("------------")
    val savedStatus = userRepo.save(user)
    savedStatus map { u =>
      println("User Successfully Saved With ID : " + u.id)
    } recover {
      case ex => println("User save failed. Check stacktrace for more details.")
        ex.printStackTrace();
        throw new Exception("Save operation failed")
    }
  }

  def getAllFromDB: Future[Seq[User]] = {
    println("---------------------------------------")
    println("Getting all the users from the database")
    println("---------------------------------------")
    val allUsers = userRepo.getAll
    allUsers map { u =>
      println(s"There are ${u.size} users in the database")
      u.foreach(println)
    }
    allUsers
  }

  def getUserById(id: Long) = {
    println("---------------------------------------")
    println(s"Getting the user with id: $id")
    println("---------------------------------------")
    val user = userRepo.getById(id)
    user map { u =>
      println(s"User details : $u")
      u.foreach(println)
    }
    user
  }

  def updateUser(id: Long, user: User) = {
    println("---------------------------------------")
    println(s"Updating the user with id $id")
    println("---------------------------------------")
    val modifiedUser = user.copy(password = "new password")
    val status = userRepo.updateById(id, modifiedUser)
    status map { s =>
      println(s"Updated the password for the user ${modifiedUser.userName} successfully")
    } recover {
      case ex => println("Update operation failed.")
        ex.printStackTrace()
    }
    status
  }

  def deleteUser(id: Long) = {
    println("---------------------------------------")
    println(s"Deleting the user with id $id")
    println("---------------------------------------")
    val deleteStatus = userRepo.deleteById(id)
    deleteStatus map { s =>
      println(s"User with ID $id deleted successfully")
    } recover {
      case ex => println("Delete operation failed.")
        ex.printStackTrace()
    }
  }
}