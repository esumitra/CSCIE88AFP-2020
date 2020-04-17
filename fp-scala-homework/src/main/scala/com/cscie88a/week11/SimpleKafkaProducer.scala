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
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.{ProducerSettings, ConsumerSettings}
import org.apache.kafka.common.serialization.{StringSerializer, ByteArrayDeserializer, StringDeserializer}
import akka.kafka.scaladsl._

object SimpleKafkaProducer {
  import StreamUtil.defaultActorSystem._

  val MyTopic = "orders"
  val KafkaConfPath = "com.example.akka.kafka.producer"

  // Source
  val intSource: Source[Int, NotUsed] =
    Source(1 to 100)

  // Flow
  def producerRecordFlow(topic: String): Flow[String, ProducerRecord[String, String], NotUsed] =
    Flow[String].map(new ProducerRecord[String, String](topic, _))

  // kafka producer settings
  val config = akkaStreams1System.settings.config.getConfig(KafkaConfPath)
  val producerSettings = ProducerSettings(config, new StringSerializer, new StringSerializer)

  // Pipeline
  val kafkaProducerPipeline: RunnableGraph[Future[Done]] =
    intSource
      .map(_.toString)
      .via(producerRecordFlow(MyTopic))
      .toMat(Producer.plainSink(producerSettings))(Keep.right)

  // kafkaProducer.run()

}
