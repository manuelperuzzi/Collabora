package org.gammf.collabora.util

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}

/**
  * Simple class for represents a location on planet earth
  *
  * @param latitude the latitude
  * @param longitude the longitude
  */
case class Location(latitude: Double, longitude: Double)

object Location {
  implicit val locationReads: Reads[Location] = (
    (JsPath \ "latitude").read[Double] and
      (JsPath \ "longitude").read[Double]
    )(Location.apply _)

  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "latitude").write[Double] and
      (JsPath \ "longitude").write[Double]
    )(unlift(Location.unapply))

  import org.gammf.collabora.database._

  implicit object BSONtoLocation extends BSONDocumentReader[Location] {
    def read(doc: BSONDocument): Location =
      Location(
        latitude = doc.getAs[Double](LOCATION_LATITUDE).get,
        longitude = doc.getAs[Double](LOCATION_LONGITUDE).get
      )
  }

  implicit object LocationtoBSON extends BSONDocumentWriter[Location] {
    def write(location: Location): BSONDocument = {
      BSONDocument(
        LOCATION_LATITUDE -> location.latitude,
        LOCATION_LONGITUDE -> location.longitude
      )
    }
  }
}
