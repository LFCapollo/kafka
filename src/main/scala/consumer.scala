import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Properties
import scala.collection.JavaConverters._
import com.typesafe.config.{ Config, ConfigFactory }
import org.slf4j.Logger
import org.slf4j.LoggerFactory
object consumer {

  def main(args: Array[String]): Unit = {
    consumeFromKafka("test1") //topic name
    /**
     * logger implementation
     */
    val logger = LoggerFactory.getLogger(getClass.getSimpleName)
    logger.trace("Trace log message")
    logger.debug("Debug log message")
    logger.info("Info log message")
    logger.warn("Warning log message")
    logger.error("Error log message")
  }

  /**
   * consumer configuration is read from consumerProperties.conf which includes:
   * bootstrap configuration, key/value deserializers, consumer group-id, and commit interval
   * configured  consumer subscribes to kafka topic. reads logs and pr  ints in console
   * counter is used for testing purposes to compare sent and received files
   * @param topic
   */
  def consumeFromKafka(topic: String) = {
    val config = ConfigFactory.load("consumerProperties")
    val props = new Properties()
    props.put("bootstrap.servers", config.getAnyRef("c.bootstrapServer"))
    props.put("key.deserializer", config.getAnyRef("c.keyDeserializer"))
    props.put("value.deserializer", config.getAnyRef("c.valueDeserializer"))
    props.put("auto.offset.reset", config.getAnyRef("c.autoOffsetReset"))
    props.put("group.id", config.getAnyRef("c.groupId"))
    props.put("auto.commit.interval.ms", config.getAnyRef("c.autoCommitIntervalMs"))
    var i=1 //counter
    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Arrays.asList(topic))
    while (true) {
      val record = consumer.poll(1000).asScala
      for (data <- record.iterator) {
        println(i + " " + data.value())

        i+= 1
      }
      }
  }
}
