package fr.company.clustering

import com.typesafe.config.{Config, ConfigFactory}
import configs.{ConfigError, Configs}
import org.slf4j.{Logger, LoggerFactory}

final case class StagingConf(date: String, fileName :String, rootDirectory: String)
final case class WorkingConf(rootDirectory: String)
final case class MetricsConf(rootDirectory: String)




final case class clusteringConf(jobName: String, staging: StagingConf,working: WorkingConf,  metrics:MetricsConf)


/*
 * This object allow to retrieve the job configuration
 */
object Settings {

  val log: Logger = LoggerFactory.getLogger(getClass)

  val conf: Config = ConfigFactory.load()

  val config = Configs[clusteringConf].get(conf, "jobconf").valueOrThrow(configErrorsToException _)

  def configErrorsToException(err: ConfigError) =
    new IllegalStateException(err.entries.map(_.messageWithPath).mkString(","))
}
