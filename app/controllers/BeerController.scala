package controllers

import models.Beer
import play.api.libs.json._
import play.api.mvc._
import services.BeerService

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BeerController @Inject() (cc: ControllerComponents, beerService: BeerService) (implicit ec: ExecutionContext)
    extends AbstractController(cc) {
        implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
            def reads(json: JsValue): JsResult[Timestamp] = 
                json.validate[Long].map(new Timestamp(_))
            def writes(ts: Timestamp): JsValue =
                JsString(ts.toInstant.toString)
        }

        implicit val beerFormat: OFormat[Beer] = Json.format[Beer]

        def create(): Action[JsValue] = Action.async(parse.json) { request =>
            val name = (request.body \ "name").as[String]
            val style = (request.body \ "style").as[String]
            val brewery = (request.body \ "brewery").as[String]
            beerService.create(name, style, brewery).map(beer => Created(Json.toJson(beer)))    
        }

        def list(): Action[AnyContent] = Action.async {
            beerService.list().map(beers => Ok(Json.toJson(beers)))
        }

        def findById(id: Long): Action[AnyContent] = Action.async {
            beerService.findById(id).map {
                case Some(beer) => Ok(Json.toJson(beer))
                case None => NotFound
            }
        }

        def delete(id: Long): Action[AnyContent] = Action.async {
            beerService.delete(id).map {
                case true => NoContent
                case false => NotFound
            }
        }
}
