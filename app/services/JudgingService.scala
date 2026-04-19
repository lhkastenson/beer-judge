package services

import models.Judging
import repositories.JudgingRepository

import javax.inject.{Singleton, Inject}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JudgingService @Inject() (judgingRepo: JudgingRepository)(implicit ec: ExecutionContext) {
    def create(beerId: Long, judgeName: String, aroma: Int, appearance: Int, flavor: Int, mouthfeel: Int, overall: Int, notes: Option[String]) = 
        judgingRepo.create(beerId, judgeName, aroma, appearance, flavor, mouthfeel, overall, notes)

    def listByBeer(beerId: Long): Future[Seq[Judging]] = 
        judgingRepo.listByBeer(beerId)

    def scoreBand(total: Int): String = total match {
        case t if t <= 13 => "Problematic"
        case t if t <= 20 => "Fair"
        case t if t <= 29 => "Good"
        case t if t <= 37 => "Very Good"
        case t if t <= 44 => "Excellent"
        case _ => "World Class"
    }
}
