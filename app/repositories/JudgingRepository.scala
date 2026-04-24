package repositories

import models.Judging
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import java.sql.Timestamp
import javax.inject.{Singleton, Inject}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JudgingRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
    private val dbConfig = dbConfigProvider.get[JdbcProfile]

    import dbConfig._
    import profile.api._

    private class JudgingTable(tag: Tag) extends Table[Judging](tag, "judgings") {
        def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
        def beerId = column[Long]("beer_id")
        def judgeName = column[String]("judge_name")
        def aroma = column[Int]("aroma")
        def appearance = column[Int]("appearance")
        def flavor = column[Int]("flavor")
        def mouthfeel = column[Int]("mouthfeel")
        def overall = column[Int]("overall")
        def notes = column[Option[String]]("notes")
        def createdAt = column[Timestamp]("created_at")
        def * = (id, beerId, judgeName, aroma, appearance, flavor, mouthfeel, overall, notes, createdAt) <> (Judging.tupled, Judging.unapply)
    }

    private val judgings = TableQuery[JudgingTable]

    def create(beerId: Long, judgeName: String, aroma: Int, appearance: Int, flavor: Int, mouthfeel: Int, overall: Int, notes: Option[String]): Future[Judging] = db.run {
        (judgings.map(j => (j.beerId, j.judgeName, j.aroma, j.appearance, j.flavor, j.mouthfeel, j.overall, j.notes))
            .returning(judgings.map(_.id))
            += (beerId, judgeName, aroma, appearance, flavor, mouthfeel, overall, notes)
        ).flatMap(id => judgings.filter(_.id === id).result.head)
    } 

    def listByBeer(beerId: Long): Future[Seq[Judging]] = db.run {
        judgings.filter(_.beerId === beerId).result
    }
}
