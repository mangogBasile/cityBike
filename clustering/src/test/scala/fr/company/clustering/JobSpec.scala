package fr.company.clustering

import org.scalatest.{FlatSpec, Matchers}
import fr.company.common.utils.Tools._
import fr.company.common.model._


class JobSpec extends FlatSpec with Matchers {

  "getFirstList function" should "return list  number of the first group" in {

    val listBk = List(BikeStation(1, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(2, Some("a"), Some("b"), Some(2), Some(5)),
        BikeStation(3, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(4, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(5, Some("a"), Some("b"), Some(2), Some(5)))

    assert(getFirstList(listBk) == List(1,2,3))
  }


  "createTwoGroups function" should "return two groups of bikestation" in {

    val metricList = scala.collection.mutable.Map[String, Long]()
    val listBk = List(BikeStation(1, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(2, Some("a"), Some("b"), Some(2), Some(5)),
      BikeStation(3, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(4, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(5, Some("a"), Some("b"), Some(2), Some(5)))


    assert(createTwoGroups(listBk, metricList) == (List(BikeStation(1, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(2, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(3, Some("a"), Some("b"), Some(2), Some(5))),
                                      List(BikeStation(4, Some("a"), Some("b"), Some(2), Some(5)), BikeStation(5, Some("a"), Some("b"), Some(2), Some(5)))))
  }

}


