package com.logdown.mycodetub

import com.google.inject.Stage
import com.logdown.mycodetub.controller.BookStoreApi
import com.logdown.mycodetub.data.Book
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreControllerTest extends FeatureTest {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    "BookStoreController" should {
        "response created when POST request for add is made" in {
            val isbn = "9787512387744"
            server.httpPost(
                path = BookStoreApi.path_create,
                postBody =
                    s"""
                       |{
                       |"isbn":"${isbn}",
                       |"name":"Scala 學習手冊",
                       |"author":"Swartz, J.",
                       |"publishing":"OREILLY",
                       |"version":"初版",
                       |"price":48.00
                       |}
                    """.stripMargin,
                andExpect = Status.Created,
                withLocation = s"/bookstore/${isbn}"
            )
        }

        "list book's information whe GET request is made" in {
            val expectedIsbn = "9789869279987"
            val expectedBookJsonData =
                s"""
                  |{
                  |"isbn":"${expectedIsbn}",
                  |"name":"Growth Hack",
                  |"author":"Xdite",
                  |"publishing":"PCuSER電腦人文化",
                  |"version":"初版",
                  |"price":360.0
                  |}
                """.stripMargin

            val response = server.httpPost(
                path = BookStoreApi.path_create,
                postBody = expectedBookJsonData,
                andExpect = Status.Created,
                withLocation = BookStoreApi.path_get(expectedIsbn)
            )
            // get data
            server.httpGetJson[Book](
                path = response.location.get,
                withJsonBody = expectedBookJsonData
            )
        }

        "response ok when update book's information is success by using PUT" in {
            // create data
            server.httpPost(
                path = BookStoreApi.path_create,
                postBody =
                    """
                      |{
                      |"isbn":"9789869279000",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化",
                      |"version":"初版",
                      |"price":360.0
                      |}
                    """.stripMargin)

            // update data
            val response = server.httpPut(
                path = BookStoreApi.path_update,
                putBody =
                    """
                      |{
                      |"isbn":"9789869279000",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化2",
                      |"version":"初版",
                      |"price":880.0
                      |}
                    """.stripMargin,
                andExpect = Status.Ok,
                withLocation = "/bookstore/9789869279000"
            )

            // assert
            server.httpGetJson[Book](
                path = response.location.get,
                withJsonBody =
                    """
                      |{
                      |"isbn":"9789869279000",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化2",
                      |"version":"初版",
                      |"price":880.0
                      |}
                    """.stripMargin
            )
        }

    }
}
