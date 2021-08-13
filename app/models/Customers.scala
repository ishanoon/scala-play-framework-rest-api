package models

import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._


case class Customers(Id : Long, name : String, orderId : String )

case class CustomerFormData(name : String, orderId : String)

object CustomerForm{
  val form =  Form(
    mapping(
      "name" -> nonEmptyText,
      "orderId" -> nonEmptyText
    )(CustomerFormData.apply)(CustomerFormData.unapply)
  )
}

class CustomerTableDef(tag : Tag) extends Table[Customers](tag, "customer"){
  def id =  column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name =  column[String]("name")
  def orderId = column[String]("orderId")

  override def * = (id, name, orderId) <> (Customers.tupled, Customers.unapply)
}

class CustomerList @Inject()(
  protected val dbConfigProvider : DatabaseConfigProvider
  )(implicit executionContext: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile]{
  var customerList = TableQuery[CustomerTableDef]

  def add(customerItem: Customers) : Future[String] = {
    dbConfig.db
      .run(customerList += customerItem)
      .map(res => " customer item added ")
      .recover {
        case ex: Exception => {
          printf(ex.getMessage)
          ex.getMessage
        }
      }
  }

  def delete(id : Long): Future[Int] = {
    dbConfig.db
      .run(customerList.filter(_.id === id ).delete)
  }

  def update(customerItem: Customers) : Future[Int] = {
    dbConfig.db
    .run(customerList.filter(_.id === customerItem.Id )
      .map(x => (x.name, x.orderId))
      .update(customerItem.name,customerItem.orderId )
    )
  }

  def get(id : Long): Future[Option[Customers]] = {
    dbConfig.db
      .run(customerList.filter(_.id === id ).result.headOption)
  }

  def listAll: Future[Seq[Customers]] = {
    dbConfig.db
      .run(customerList.result)
  }
}