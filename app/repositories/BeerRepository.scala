package repositories

import models.Beer
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BeerRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext) {
    private val dbConfig = dbConfigProvider.get[JdbcProfile]

    import dbConfig._
    import profile.api._

    private class BeerTable(tag: Tag) extends Table[Beer](tag, "beers") {
        def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
        def name = column[String]("name")
        def style = column[String]("style")
        def brewery = column[String]("brewery")
        def createdAt = column[Timestamp]("created_at")
        def * = (id, name, style, brewery, createdAt) <> (Beer.tupled, Beer.unapply)
    }

    private val beers = TableQuery[BeerTable]

    def create(name: String, style: String, brewery: String): Future[Beer] = db.run {
    (beers.map(b => (b.name, b.style, b.brewery))
        .returning(beers.map(_.id))
        += (name, style, brewery)
    ).flatMap(id => beers.filter(_.id === id).result.head)
    }

    def list(): Future[Seq[Beer]] = db.run(beers.result)

    def findById(id: Long): Future[Option[Beer]] = db.run(beers.filter(_.id === id).result.headOption)

    def delete(id: Long): Future[Int] = db.run(beers.filter(_.id === id).delete)
}
