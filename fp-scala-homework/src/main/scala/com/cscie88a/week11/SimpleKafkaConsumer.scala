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
import org.apache.kafka.clients.consumer.ConsumerRecord
import akka.kafka.Subscriptions


object SimpleKafkaConsumer {
  import StreamUtil.defaultActorSystem._

  val MyTopic = "orders"
  val KafkaConfPath = "com.example.akka.kafka.consumer"

  // source config
  val config = akkaStreams1System.settings.config.getConfig(KafkaConfPath)
  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new StringDeserializer)

  // Source
  val kafkaSource: Source[ConsumerRecord[String, String], Consumer.Control] =
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(MyTopic))

  // Flow
  val runningAvgFlow: Flow[ConsumerRecord[String, String], Double, NotUsed] =
    Flow[ConsumerRecord[String, String]].map { record =>
      record.value.toDouble // TODO fix to calculate running average
    }

  // Sink
  val printSink: Sink[Double, Future[Done]] =
    Sink.foreach(v => println(s"record: $v"))

  // Processing pipeline
  val kafkaConsumerPipeline: RunnableGraph[Future[Done]] =
    kafkaSource
      .via(runningAvgFlow)
      .toMat(printSink)(Keep.right)

  // kafkaConsumerPipeline.run()
}
