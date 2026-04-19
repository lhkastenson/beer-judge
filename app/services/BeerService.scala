package services

import models.Beer
import repositories.BeerRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BeerService @Inject() (beerRepo: BeerRepository)(implicit ec: ExecutionContext) {
    def create(name: String, style: String, brewery: String): Future[Beer] =
        beerRepo.create(name, style, brewery)

    def list(): Future[Seq[Beer]] =
        beerRepo.list()

    def findById(id: Long): Future[Option[Beer]] = 
        beerRepo.findById(id)

    def delete(id: Long): Future[Boolean] =
        beerRepo.delete(id).map(_ > 0)
}
