package controller

import models.{Customers, CustomerForm}
import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.data.FormError

import services.CustomerService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomerController @Inject()(cc : ControllerComponents, customerService : CustomerService)
  extends AbstractController(cc){
  implicit val customerFormat = Json.format[Customers]

  def getAll = Action.async {
    implicit request : Request[AnyContent] =>
      customerService.listAllCustomers
        .map { customers =>
        Ok(Json.toJson(customers))
      }
  }
  def getById(id : Long) = Action.async {implicit request: Request[AnyContent] =>
    customerService.getCustomer(id)
      .map { item =>
      Ok(Json.toJson(item))
    }
  }

  def add() = Action.async { implicit request: Request[AnyContent] =>
    CustomerForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val newCustomer = Customers(0, data.name, data.orderId)
        customerService.addCustomer(newCustomer)
          .map( _ => Redirect(routes.CustomerController.getAll))
      })
  }

  def update(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    CustomerForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => {
        errorForm.errors.foreach(println)
        Future.successful(BadRequest("Error!"))
      },
      data => {
        val customer = Customers(id, data.name, data.orderId)
        customerService.updateCustomer(customer)
          .map(_ => Redirect(routes.CustomerController.getAll))
      })
  }
  def delete(id: Long) = Action.async { implicit request: Request[AnyContent] =>
      customerService.deleteCustomer(id)
      .map { res => Redirect(routes.CustomerController.getAll)
      }
    }

}