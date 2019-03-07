package fr.company.clustering

import com.typesafe.scalalogging.StrictLogging
import fr.company.common.utils.Tools._


object Job extends App with StrictLogging {

  //timer
  val start = System.currentTimeMillis()
  logger.info(s"#### [${Settings.config.jobName}] started ... ####" )

  val metricList = scala.collection.mutable.Map[String, Long]()


  //we create all paths
  val stagingPath = makePath(Settings.config.staging.rootDirectory, "staging", Settings.config.staging.date)
  val workingPath = makePath(Settings.config.working.rootDirectory, "working", Settings.config.staging.date)
  val metricsPath = makePath(Settings.config.metrics.rootDirectory, "metrics", Settings.config.staging.date)

  //we read json data
  logger.info(s"Reading json file in the path ${stagingPath}")
  val stagingFile = stagingPath + "/" + Settings.config.staging.fileName
  val parsedJson = readJsonWithMetrics(stagingFile, metricList , "Number_of_lines_in_file")


  //we create two groups of Bike stations
  logger.info(s"We create two balanced groups of stations using their number")
  val groups = createTwoGroups(parsedJson, metricList)


  //we write the two groups in the working directory
  creatDirectories(workingPath)
  writeGroup(groups._1,  workingPath + "/" + "group1.txt")
  writeGroup(groups._2,  workingPath + "/" + "group2.txt")


  //we write job metrics in the metrics directory
  creatDirectories(metricsPath)
  val end = System.currentTimeMillis()
  val duration = end - start
  metricList("Time_processing_in_millisecond")= duration
  writeMetrics(metricList, metricsPath + "/" + Settings.config.jobName + "_metrics.txt")

  logger.info(s"#### [${Settings.config.jobName}] end of the job, DURATION in millisecond : ${duration} ####" )
}
