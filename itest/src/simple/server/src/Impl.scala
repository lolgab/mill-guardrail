package com.example.server

import cats.effect._
import com.example.server.Resource

object MyHandler extends Handler[IO] {
  def getInventory(
      respond: Resource.GetInventoryResponse.type
  )(): IO[Resource.GetInventoryResponse] = ???
  def placeOrder(respond: Resource.PlaceOrderResponse.type)(
      body: _root_.com.example.server.definitions.Order
  ): IO[Resource.PlaceOrderResponse] = ???
  def getOrderById(
      respond: Resource.GetOrderByIdResponse.type
  )(
      orderId: Long
  ): IO[Resource.GetOrderByIdResponse] = ???
  def deleteOrder(
      respond: Resource.DeleteOrderResponse.type
  )(
      orderId: Long
  ): IO[Resource.DeleteOrderResponse] = ???
}
