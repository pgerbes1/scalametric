package com.scalametrics.controllers


  import com.scalametrics.models._
  import play.api.mvc._
  import play.api.libs.json._

  class Api extends Controller {
    def fitRegression = Action(parse.json) { request =>
          Ok(Json.prettyPrint(request.body))
        }
      }



