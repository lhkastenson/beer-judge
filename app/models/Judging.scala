package models

import java.sql.Timestamp

case class ValidationError(field: String, message: String)

case class Judging(
    id: Long,
    beerId: Long,
    judgeName: String,
    aroma: Int,
    appearance: Int,
    flavor: Int,
    mouthfeel: Int,
    overall: Int,
    notes: Option[String],
    createdAt: Timestamp
)
