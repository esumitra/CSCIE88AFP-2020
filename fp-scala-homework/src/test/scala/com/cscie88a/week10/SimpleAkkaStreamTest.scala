package com.cscie88a.week10

import org.scalatest.{ AsyncWordSpec, WordSpec, BeforeAndAfterAll, Matchers }
import akka.stream._
import akka.stream.scaladsl._
import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._
import com.cscie88a.util.StreamUtil
import cats.implicits._
import org.scalatest.compatible.Assertion

class SimpleAkkaStreamTest
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

  /**
    * helper function to test source contents
    * materializes source to a sequence and calls assertion on resulting sequence
    *
    * @param testSource input source
    * @param assertion assertion on sequence
    * @return
    */
  def assertSourceContent[O]
  (testSource: Source[O, NotUsed])
  (assertion: Seq[O] => Assertion): Future[Assertion] = {
    testSource
      .runWith(Sink.seq)
      .map(assertion(_))
  }

  "SimpleAkkaStream" should {

    "materialize source of ints" in {
      assertSourceContent(SimpleAkkaStream.source) { s =>
        s.take(5).shouldBe(Seq(1,2,3,4,5))
      }
    }
  }

}
