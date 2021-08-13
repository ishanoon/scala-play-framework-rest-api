package services
import com.google.inject.Inject

import models.{Customers, CustomerList}
import scala.concurrent.Future

class CustomerService @Inject() (customers: CustomerList) {
  def addCustomer(customer : Customers): Future[String] = {
    customers.add(customer)
  }
   def deleteCustomer(id : Long): Future[Int] = {
     customers.delete(id)
   }

  def updateCustomer(customer : Customers): Future[Int] = {
    customers.update(customer)
  }

  def getCustomer(id: Long): Future[Option[Customers]] = {
    customers.get(id)
  }

  def listAllCustomers: Future[Seq[Customers]] = {
    customers.listAll
  }
}