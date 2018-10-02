package com.github.skamdart.kafka.twitter

import org.apache.kafka.clients.producer._
import com.danielasfregola.twitter4s.TwitterStreamingClient
import java.util.Properties

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage
import scala.concurrent.Promise


case class TwitterProducer(topic: String) {
   // create a twitter client
  val client = TwitterStreamingClient()

  // properties
  val bootstrapServer = "127.0.0.1:9092"
  var props = new Properties()
  props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
  props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.setProperty(ProducerConfig.ACKS_CONFIG, "1")

  // create a kafka producer
  private val producer = new KafkaProducer[String, String](props)

  def produceTweet: PartialFunction[StreamingMessage, Unit] = {
    case tweet: Tweet =>
      val record = new ProducerRecord[String, String](topic, tweet.text)
      val p = Promise[(RecordMetadata, Exception)]()
      producer.send(record, (metadata: RecordMetadata, exception: Exception) => {
        p.success((metadata, exception))
        println(tweet.text)
      })
  }

  def streamTweets(): Unit = client.sampleStatuses(stall_warnings = true)(produceTweet)
}


object TwitterApp extends App {
  def main(): Unit = {
    TwitterProducer("tweets").streamTweets()
  }
}
