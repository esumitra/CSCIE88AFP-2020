package com.cscie88a.week10

import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.util.ByteString
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._
import com.cscie88a.util.StreamUtil
import cats.implicits._
import java.nio.file.Paths

object SimpleAkkaStream {

  import StreamUtil.defaultActorSystem._

  // creating Sources
  val emp1: Source[Int, NotUsed] = Source.empty

  val single1: Source[String, NotUsed] = Source.single("a")

  val aas: Source[String, NotUsed] = Source.repeat("A")

  val countByTwo: Source[Int, NotUsed] = Source.cycle(() => List(1,2).iterator)

  val source: Source[Int, NotUsed] = Source(1 to 100)

  val simpleRange: Source[Int, NotUsed] = Source(1 to 100)

  val simpleList: Source[Int, NotUsed] = Source(List(1,2,3,4,5))

  val csv: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get("/Users/esumitra/tmp/tsc.log"))

  // csv.map(_.utf8String).runWith(Sink.foreach(print(_)))
  // StreamUtil.printStream(source)

  // Source Flow operators
  val tens = countByTwo.map(_ * 10)

  val twenties = tens.filter(_ == 20)

  val sum5 = simpleList.fold(0)(_ + _)

  // sum5.runWith(Sink.head)

  // Source run operators

  val sum5b = simpleList.runFold(0)(_ + _)

  val run5 = simpleList.runForeach(i => println(s"[$i]"))

  // Sinks

  val list2 = simpleList.map(_ * 10).runWith(Sink.seq)

  val list3Result = simpleList.map(i => ByteString(s"$i")).runWith(FileIO.toPath(Paths.get("list3.txt")))

  // Flow
  val upto25: Flow[Int, Int, NotUsed] =
    Flow[Int].takeWhile(_ < 25)

  val upto25Source: Source[Int, NotUsed] = simpleRange.via(upto25)

  val truncatedPrint: Sink[Int, NotUsed] =
    upto25.to(Sink.foreach(println))


  // Graphs
  val pipeline1:RunnableGraph[NotUsed] =
    Source(1 to 10).via(Flow[Int].map(_ * 10)).to(Sink.foreach(println(_)))

  val pipeline2: RunnableGraph[NotUsed] =
    simpleRange.to(truncatedPrint)


  // pipeline1.run()
  // pipeline2.run()

}
