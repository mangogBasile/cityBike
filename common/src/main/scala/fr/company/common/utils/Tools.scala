package fr.company.common.utils

import java.io.File

import io.circe.parser
import io.circe.generic.semiauto.deriveDecoder
import fr.company.common.model._
import com.typesafe.scalalogging.StrictLogging

import scala.io.Source
import java.io._

import io.circe

import scala.util.matching.Regex
import scala.collection.mutable.ListBuffer



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
    * This funtion convert a json file to a long string. It also remove some space
    * @param jsonPath
    * @return
    */
  def multilineToOneline(jsonPath:String)= {

    var allLines = ""
    for (l <- Source.fromFile(jsonPath).getLines) {
      allLines = allLines + l.trim.replaceAll(": ", ":")
    }

    allLines
  }

  /**
    * This function control and filter all the data wich are not conform to the standard
    * @param allLines
    * @return List[String]
    */
  def integrationControl(allLines:String) = {
    val pattern = "\\{\"id\":\\d*,\"name\":(\"\\d* - ([a-zA-Z(?)?]+ )+(\\/? ([a-zA-Z(?)?]+ )+)?[a-zA-Z(?)?]+\"|null),\"address\":(\"([a-zA-Z(?)?]+ )+(\\/? ([a-zA-Z(?)?]+ )+)?[a-zA-Z(?)?]+\"|null),\"latitude\":(-?\\d*\\.\\d*|null),\"longitude\":(-?\\d*\\.\\d*|null)}".r

    pattern.findAllIn(allLines).toList
  }


  /**
    * This function read a json and add metrics. It returns List[BikeStation]
    * @param jsonPath
    * @param metricList
    * @param metricName
    * @return List[BikeStation]
    */
  def readJsonWithMetrics(jsonPath:String, metricList : scala.collection.mutable.Map[String, Long], metricName: String)= {

    val jsonString = multilineToOneline(jsonPath)
    metricList(metricName)= jsonString.length
    val blocks = integrationControl(jsonString)


    implicit val BikeStationDecoder = deriveDecoder[BikeStation]

    var bikeStationListBuffer = new ListBuffer[Either[circe.Error, BikeStation]]()
    blocks.foreach(bikeStation => bikeStationListBuffer += parser.decode[BikeStation](bikeStation))

    val (errors, bikeStationList) = bikeStationListBuffer.toList.foldRight[(List[circe.Error], List[BikeStation])](Nil,Nil) {
      case (Left(error), (e, i)) => (error :: e, i)
      case (Right(result), (e, i)) => (e, result :: i)
    }

    metricList("Number_of_regular_bike_station")= bikeStationList.length

    bikeStationList
  }



  /**
    * This function take a List[BikeStation], retrieve a list of ids then slice the list into two balanced groups. It return the id list of the first group
    * @param parsedJson
    * @return List[Int]
    */
  def getFirstList(parsedJson: List[BikeStation] ) = {

    val ids = parsedJson.map(b => b.id).sorted

    val list1 = ids.length % 2 match {
                                            case 0 => ids.slice(0,(ids.length/2) + 1)
                                            case _ => ids.slice(0,(ids.length+1)/2)
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

    val group1 = parsedJson.filter(bikeStation => firstList.contains(bikeStation.id))
    val group2 = parsedJson.filter(bikeStation => !firstList.contains(bikeStation.id))

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