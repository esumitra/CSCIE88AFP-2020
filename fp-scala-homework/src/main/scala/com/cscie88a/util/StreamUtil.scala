package com.cscie88a.util

import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._

object StreamUtil {

  def actorSystemInstances(name: String): (ActorSystem,ActorMaterializer,ExecutionContext) = {
    val akkaSystem = ActorSystem(s"$name-Actor-System")
    val akkaMaterializer = ActorMaterializer()(akkaSystem)
    val akkaStreamsEC = akkaSystem.dispatcher
    (akkaSystem, akkaMaterializer, akkaStreamsEC)
  }

  object defaultActorSystem {

    implicit val (akkaStreams1System, akkaStreams1Materializer, akkaStreams1EC) = actorSystemInstances("Default-Actor-System")

    def shutdown() =
      akkaStreams1System.terminate()
  }

  def printStream[O](s: Source[O, NotUsed]): Future[Done] = {
    import defaultActorSystem._
    s.runForeach(println)
  }

}
