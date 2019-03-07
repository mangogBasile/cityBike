package fr.company.common.utils

import java.io.File
import io.circe.parser
import io.circe.generic.semiauto.deriveDecoder
import fr.company.common.model._
import com.typesafe.scalalogging.StrictLogging
import scala.io.Source
import java.io._



object Tools extends StrictLogging {

  /**
    * This function creates a path that is the concatenation of several string
    * @param rootPath
    * @param domain
    * @param dateStaging
    * @return string
    */
  def makePath(rootPath: String, domain: String, dateStaging: String) = {
    rootPath + "/" + domain + "/" + dateStaging
  }


  /**
    * This function read a json and add metrics. It returns List[BikeStation]
    * @param jsonPath
    * @param metricList
    * @param metricName
    * @return List[BikeStation]
    */
  def readJsonWithMetrics(jsonPath:String, metricList : scala.collection.mutable.Map[String, Long], metricName: String): List[BikeStation] = {

    val jsonString = Source.fromFile(jsonPath).getLines.mkString
    metricList(metricName)= jsonString.length

    implicit val BikeStationDecoder = deriveDecoder[BikeStation]
    val decodeResult = parser.decode[List[BikeStation]](jsonString)

    decodeResult match {
      case Right(bikeStation) => {
                                  metricList("Number_of_bike_station")= bikeStation.length
                                  bikeStation
                                }
      case Left(error) => {
                            println(error.getMessage())
                            List[BikeStation]()
                          }
    }
  }


  /**
    * This function take a List[BikeStation], retrieve a list of numbers then slice the list into two balanced groups. It return the list number of the first group
    * @param parsedJson
    * @return List[Int]
    */
  def getFirstList(parsedJson: List[BikeStation] ): List[Int] = {

    val numbers = parsedJson.map(b => b.number).sorted

    val list1 = numbers.length % 2 match {
                                            case 0 => numbers.slice(0,(numbers.length/2) + 1)
                                            case _ => numbers.slice(0,(numbers.length+1)/2)
                                          }

    list1
  }


  /**
    * This function take a list of bikestation (List[BikeStation]) then return two balanced groups of Bike station
    * @param parsedJson
    * @param metricList
    * @return (List[BikeStation], List[BikeStation])
    */
  def createTwoGroups(parsedJson: List[BikeStation] , metricList : scala.collection.mutable.Map[String, Long]): (List[BikeStation], List[BikeStation]) = {

    val firstList = getFirstList(parsedJson)

    val group1 = parsedJson.filter(bikeStation => firstList.contains(bikeStation.number))
    val group2 = parsedJson.filter(bikeStation => !firstList.contains(bikeStation.number))

    metricList("Length_of_first_group")= group1.length
    metricList("Length_of_second_group")= group2.length

    (group1, group2)
  }


  /**
    * This function take a path to a subdirectory and creates all necessary subdirectories.
    * @param directories
    * @return "ok" or Throw Error
    */
  def creatDirectories(directories : String) = {

    val file = new File(System.getProperty("user.dir") + "/" +directories)

    file.mkdirs match {
      case true => "ok"
      case false => throw new IllegalArgumentException("Error: unable to create Directories, check if the directories exist")
    }
  }


  /**
    * This function write a  list of bikestation (List[BikeStation]) into a file
    * @param group
    * @param fileName
    */
  def writeGroup(group :List[BikeStation], fileName :String ): Unit = {

    val pw = new PrintWriter(new File(fileName))
    group.foreach(bikeStation => pw.write(bikeStation.mkstring + "\n"))
    pw.close
  }

  /**
    * This function write a  list of metrics (metricList) into a file
    * @param metricList
    * @param fileName
    */
  def writeMetrics(metricList : scala.collection.mutable.Map[String, Long], fileName :String ): Unit = {

    val pw = new PrintWriter(new File(fileName))
    metricList.foreach(metric =>  pw.write(s"${metric._1}:${metric._2} \n"))
    pw.close
  }

}