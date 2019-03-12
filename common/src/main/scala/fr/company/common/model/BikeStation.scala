package fr.company.common.model


/*
 *This model will be use to model Bikestation
 */
case class BikeStation(  id: Double,
                         name: Option[String],
                         address: Option[String],
                         latitude: Option[Double],
                         longitude: Option[Double]
                       )
{
  def mkstring = s"{id:${id}, name:${name.getOrElse("null")}, address:${address.getOrElse("null")}, latitude:${latitude.getOrElse("null")}, longitude:${longitude.getOrElse("null")}}"
}