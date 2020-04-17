package com.cscie88a.week11

import org.scalatest.{ AsyncWordSpec, WordSpec, BeforeAndAfterAll, Matchers }
import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._
import com.cscie88a.util.StreamUtil
import cats.implicits._
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.{ProducerSettings, ConsumerSettings}
import akka.kafka.scaladsl._

class SimpleKafkaConsumerTest
  extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll {

  // create the akka system for stream materialization
  implicit val (sys, mat, _) = StreamUtil.actorSystemInstances("KafkaStreamTest")

  // shutdown the akka system after tests
  override protected def afterAll() {
    sys.terminate()
    super.afterAll()
  }

  "Kafka Consumer" should {
    "print running average" in {
      val result: Future[Done] = SimpleKafkaConsumer.kafkaConsumerPipeline.run()
      result.map(_ => succeed)
    }
  }

}
