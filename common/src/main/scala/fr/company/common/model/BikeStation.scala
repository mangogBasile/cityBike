package fr.company.common.model


/*
 *This model will be use to model Bikestation
 */
case class BikeStation(  number: Int,
                         name: String,
                         address: String,
                         latitude: Double,
                         longitude: Double
                       )
{
  def mkstring = s"{number:${number}, name:${name}, address:${address}, latitude:${latitude}, longitude:${longitude}}"
}