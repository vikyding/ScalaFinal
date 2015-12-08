package controllers

import java.io.{FileReader, File, FileWriter}


import models.{cluster, FilePath, FileName}

import org.apache.spark.{SparkContext, SparkConf}
import play.api._
import play.api.data.Form
import play.api.data.Forms._

import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._


class Application extends Controller {


  var fp:String=null
  var fn:String =null

  def InputFileForm: Form[FilePath] =Form {
    mapping (
      "filePath" ->text
    )(FilePath.apply)(FilePath.unapply)
  }

  def index = Action {

    Ok(views.html.index("try"))
  }

  def start = Action { implicit request =>
    fp=InputFileForm.bindFromRequest().get.name
    Future{LoadData.Load(fp)}
    Ok(views.html.loading())

  }


  def stop = Action{
    LoadData.Stop
    Ok(views.html.finish())
  }


  def FileForm: Form[FileName] =Form {
    mapping (
      "fileName" ->text
    )(FileName.apply)(FileName.unapply)
  }

  //def personForm: Form[Person] =Form {
   // mapping (
    //"name" ->text
    //)(Person.apply)(Person.unapply)
  //}

  //def addPerson= Action { implicit  request =>
   // val person=personForm.bindFromRequest().get
    //DB.save(person)
    //Redirect(routes.Application.index())
  //}

//  def getPersons = Action {
  //  val persons=DB.query[Person].fetch()
    //Ok(Json.toJson(persons))
  //}

  def result = Action {

    val fr=scala.io.Source.fromFile(new File(fn)).getLines

    Ok(views.html.result(fr.toList))
  }


  def clustering = Action { implicit request =>

    fn = FileForm.bindFromRequest().get.name

    val conf = new SparkConf().setAppName("SecondAppName").setMaster("local").set("master.clustering", "1g")
    System.setProperty("master.clustering", "1g")

    val sc = new SparkContext(conf)

    val stopwords=Twitter.stopWords

    val text=sc.textFile(fp).filter(_.length>0)

    Clustering.cluster(text,stopwords,fn)

    Redirect(routes.Application.result())

  }





}
