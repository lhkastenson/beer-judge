package models

import java.sql.Timestamp

case class Beer(
    id: Long,
    name: String,
    style: String,
    brewery: String,
    createdAt: Timestamp
)
