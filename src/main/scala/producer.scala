import java.util.Properties
import java.io.File
import java.nio.file.FileSystems

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import akka.stream.alpakka.file.javadsl.DirectoryChangesSource
import akka.stream.alpakka.file.scaladsl.{Directory, DirectoryChangesSource}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
object producer {
  def main(args: Array[String]): Unit = {
    import akka.actor.ActorSystem
    import akka.stream.ActorMaterializer
    val system = ActorSystem.create()
    val mat = ActorMaterializer.create(system)
    val fs = FileSystems.getDefault
    val changes = DirectoryChangesSource(fs.getPath("C:\\Users\\nika.khvedelidze\\Desktop\\kafka mult dockers\\confluentKafka\\kafka\\src\\data"), pollInterval = 1.seconds, maxBufferSize = 1000)
    changes.runForeach {
      case (path, change) => println("Path: " + path + ", Change: " + change)
    }(mat)
    /**
     * slf4j logger implementation
     */
    val logger = LoggerFactory.getLogger(getClass.getSimpleName)
    logger.trace("Trace log message")
    logger.debug("Debug log message")
    logger.info("Info log message")
    logger.warn("Warning log message")
    logger.error("Error log message")
    val topic = "myTopic" //topic name
   val files = getListOfFiles("C:\\Users\\nika.khvedelidze\\Desktop\\confluentKafka\\kafka\\src\\data")

    /**
     * for loop 1 - iterates on csv file names
     * for loop 2 - iterates in csv files, filters key name and surname, formats name and surname in 1 string
     * calls write to kafka using topic, key and formatted string
     */
    for (i <- files){
      val f=io.Source.fromFile(i)
      for (line<-f.getLines()){
        var tokens=line.split(",").map(_.trim)
        var key=tokens(0)
        var name=tokens(1)
        var surname=tokens(2)
        var msg = String.format("{\"name\": %s, \"surname\": \"%s\"}", name, surname)
        writeToKafka(topic, key, msg)
      }
    }
  }

  /**
   * writeToKafka gets 3 parameters topic, key, msg and sends message
   * to kafka topic using key.
   * Important configuration of the producer - such as bootstrap server, acks, key/value serializer,
   * retries are read from the producerProperties.conf
   * @param topic
   * @param key
   * @param msg
   */

  def writeToKafka(topic: String, key: String, msg: String): Unit = {
    //val configPath = System.getProperty("C:\\Users\\nika.khvedelidze\\Desktop\\kafka mult dockers\\confluentKafka\\kafka\\src")

    val config = ConfigFactory.load("producerProperties")
    val props = new Properties()
    props.put("bootstrap.servers", config.getAnyRef("p.bootstrap"))
    props.put("acks", config.getAnyRef("p.acks"))
    props.put("key.serializer", config.getAnyRef("p.key"))
    props.put("value.serializer", xconfig.getAnyRef("p.value"))
    props.put("retries", config.getAnyRef("p.retry"))
    val producer = new KafkaProducer[String, String](props)
    val record = new ProducerRecord[String, String](topic, key, msg)
    producer.send(record)
    producer.close()
  }

  /**
   * getListOfFiles gets the directory and returns list of csv file names
   * @param dir
   * @return
   */
  def getListOfFiles(dir: String): List[String] = {
    val file = new File(dir )
    file.listFiles.filter(_.isFile)
      .filter(_.getName.endsWith("csv"))
      .map(_.getPath).toList
  }
}*/
