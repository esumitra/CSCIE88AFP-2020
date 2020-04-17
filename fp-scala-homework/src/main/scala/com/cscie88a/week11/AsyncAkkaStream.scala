package com.cscie88a.week11

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

object AsyncAkkaStream {

  import StreamUtil.defaultActorSystem._

  // helper flow
  def elementLogFlow[I](name: String): Flow[I, I, NotUsed] =
    Flow[I]
      .log(name)
      .addAttributes(Attributes.logLevels(onElement = Attributes.LogLevels.Info))

  // helper logging pipeline
  def sampleStream[I, M](source: Source[I, M]) =
    source.take(5).via(elementLogFlow("logger")).runWith(Sink.ignore)

  // time based operators
  val tick2 = Source.tick(0 seconds, 2 seconds, 1)

  // tick2.take(10).async.via(elementLogFlow("test")).async.runWith(Sink.ignore)

  val throttledInts = Source(1 to 100).throttle(3, 1 seconds)


  val delayedInts = Source(1 to 100).delay(2 seconds)


  val dropWithinInts = Source(1 to 100000).dropWithin(10 millis)


  val takeWithinInts = Source(1 to 100000).takeWithin(10 millis)


  // takeWithinInts.runWith(Sink.seq).map(_.size)

  // buffers
  val fastProducer:Source[Int, NotUsed] = Source(1 to 100).map(_ + 1).async.take(15)

  val slowConsumer: Sink[Int, NotUsed] =
    Flow[Int].throttle(1, 5 seconds).via(elementLogFlow("throttled")).to(Sink.ignore)

  // fastProducer.runWith(slowConsumerWithBuffer3)
  val slowConsumerWithBuffer1: Sink[Int, NotUsed] =
    Flow[Int]
      .buffer(10, OverflowStrategy.dropNew)
      .throttle(1, 5 seconds)
      .via(elementLogFlow("throttled"))
      .to(Sink.ignore)

  val slowConsumerWithBuffer2: Sink[Int, NotUsed] =
    Flow[Int]
      .buffer(10, OverflowStrategy.dropHead)
      .throttle(1, 5 seconds)
      .via(elementLogFlow("throttled"))
      .to(Sink.ignore)

  val slowConsumerWithBuffer3: Sink[Int, NotUsed] =
    Flow[Int]
      .buffer(10, OverflowStrategy.backpressure)
      .throttle(1, 5 seconds)
      .via(elementLogFlow("throttled"))
      .to(Sink.ignore)

}
