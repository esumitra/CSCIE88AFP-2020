package com.cscie88a.week5

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Random

object FutureExamples {

  val futureTask = Future {
    Thread.sleep(3000) // sleep for 3 seconds simulating processing
    println(s"${Thread.currentThread.getName}: done")
  }

  def createUnitFuture(d: Duration): Future[Unit] = Future {
    Thread.sleep(d.toMillis)
    println(s"${Thread.currentThread.getName}: done")
  }

  (1 to 3).foreach(_ => createUnitFuture(3 seconds))

  def createIntFuture(n: Int, d: Duration = 3 seconds): Future[Int] = Future {
    Thread.sleep(d.toMillis)
    // println(s"${Thread.currentThread.getName}: done")
    Math.pow(n,2).toInt
  }

  def calculateInt(n: Int, d: Duration = 3 seconds): Int = {
    Thread.sleep(d.toMillis)
    println(s"${Thread.currentThread.getName}: done")
    Math.pow(n,2).toInt
  }

  val intFuture1: Future[Int] = createIntFuture(3, 5 seconds)
  intFuture1.onComplete {
    case Success(n: Int) => println(s"completed value: ${n}")
    case Failure(e) => println(s"failed with error:  ${e}")
  }

  val intFuture2: Future[Int] = createIntFuture(4, 5 seconds)
  val futureResult2: Int = Await.result(intFuture2, 5 seconds) // blocking! try to avoid

  def namedFutureFunction[T, S](name: String = "futureFunction", d: Duration = 3 seconds)(f: T => S): T => Future[S] = {
    (x: T) => Future {
      Thread.sleep(d.toMillis)
      val y = f(x)
      println(s"${name} execution complete on ${Thread.currentThread.getName}, value: ${y}")
      y
    }
  }

  val getStudentId: String => Future[Int] = namedFutureFunction("getStudentId")(studentName => studentName.hashCode)
  val getStudentScore: Int => Future[Int] = namedFutureFunction("getStudentScore") { id =>
    Random.nextInt(100)
  }

  def convertScoreToGrade(score: Int): String = score match {
    case (s) if s > 90 => "A"
    case (s) if s > 80 => "B"
    case (s) if s > 70 => "C"
    case (s) if s > 60 => "D"
    case _ => "F"
  }

  def getStudentGradeById(studentId: Int): Future[String] = getStudentScore(studentId).map(convertScoreToGrade)
  def getStudentScoreByName(studentName: String): Future[Int] = getStudentId(studentName).flatMap(getStudentScore)

  val studentGrade1 = getStudentGradeById(30).onSuccess {case r => println(s"grade: ${r}")}
  val studentScore1 = getStudentScoreByName("alex")

  val studentList: List[String] = List("alex", "ben", "charlie", "dan")
  val studentScores1: List[Future[Int]] = studentList.map(getStudentScoreByName)
  val studentScores2: Future[List[Int]] = Future.sequence(studentScores1)
  val studentScores: Future[List[Int]] = Future.traverse(studentList)(getStudentScoreByName)

  val simpleList1 = (1 to 5).toList
  val derivedList1 = simpleList1.map(calculateInt(_))
  object demoParallel {
    def result = simpleList1.par.map(calculateInt(_))
  }
}
