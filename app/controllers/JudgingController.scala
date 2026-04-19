package controllers

import models.Judging
import play.api.libs.json._
import play.api.mvc._
import services.{BeerService, JudgingService}

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class JudgingController @Inject() (cc: ControllerComponents, judgingService: JudgingService, beerService: BeerService)(implicit ec: ExecutionContext) extends AbstractController {
    implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
        def reads(json: JsValue): JsResult[Timestamp] =
            json.validate[Long].map(new Timestamp(_))
        def writes(ts: Timestamp): JsValue = 
            JsString(ts.toInstant.toString)
    }

    implicit val judgingFormat: OFormat[Judging] = Json.format[Judging]

    def create(beerId: Long): Action[JsValue] = Action.async(parse.json) { request =>
        beerService.findById(beerId).flatMap {
            case None => scala.concurrent.Future.successful(NotFound)
            case Some(_) =>
                val judgeName = (request.body \ "judgeName").as[String]
                val aroma = (request.body \ "aroma").as[Int]
                val appearance = (request.body \ "appearance").as[Int]
                val flavor = (request.body \ "flavor").as[Int]
                val mouthfeel = (request.body \ "mouthfeel").as[Int]
                val overall = (request.body \ "overall").as[Int]
                val notes = (request.body \ "notes").asOpt[String]
                judgingService.create(beerId, judgeName, aroma, appearance, flavor, mouthfeel, overall, notes)
                    .map(j => Created(Json.toJson(j)))
        }
    }

    def list(beerId: Long): Action[AnyContent] = Action.async {
        judgingService.listByBeer(beerId).map(js => Ok(Json.toJson(js)))
    }

    def score(beerId: Long): Action[AnyContent] = Action.async {
        beerService.findById(beerId).flatMap {
            case None => scala.concurrent.Future.successful(NotFound)
            case Some(beer) =>
                judgingService.listByBeer(beerId).map { judgings =>
                    val count = judgings.size
                    if (count == 0) {
                        Ok(Json.obj("beerId" -> beerId, "beerName" -> beer.name, "judgeCount" -> count))
                    } else {
                        val total = judgings.map(j => j.aroma + j.appearance + j.flavor + j.mouthfeel + j.overall)
                        val avgScore = total.sum.toDouble / count
                        val breakdown = Json.obj(
                            "aroma" -> judgings.map(_.aroma).sum.toDouble / count,
                            "appearance" -> judgings.map(_.appearance).sum.toDouble / count,
                            "flavor" -> judgings.map(_.flavor).sum.toDouble / count,
                            "mouthfeel" -> judgings.map(_.mouthfeel).sum.toDouble / count,
                            "overall" -> judgings.map(_.overall).sum.toDouble / count
                        )
                        Ok(Json.obj(
                            "beerId" -> beerId,
                            "beerName" -> beer.name,
                            "judgeCount" -> count,
                            "averageScore" -> avgScore,
                            "scoreBand" -> judgingService.scoreBand(avgScore.round.toInt),
                            "breakdown" -> breakdown
                        ))
                    }
                }
        }
    }
}
