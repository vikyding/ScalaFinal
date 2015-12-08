import java.util.StringTokenizer

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

import scala.math._

/**
  * Created by mengchending on 12/2/15.
  */
object TfIdf {

  def delimite(textFile: RDD[String],stopwords:Array[String]):RDD[String]={

    val words= textFile map (_.split(" ").filter(_.length>0))
    val c=words map (f=>{f.filterNot(a=>{stopwords.contains(a)}) }.mkString(" "))
   // c.foreach(aa=>println(aa))
    return c
  }

  def getNumOfFile(textFile: RDD[String]): Long = {
    textFile.count
  }

  def getVocabulary(textFile: RDD[String]): RDD[String] = {

   // textFile.foreach(aa => println(aa))

    textFile.flatMap(e => {

      val trip: StringTokenizer = new StringTokenizer(e, " ")

      val id = trip.nextToken()

      if (!trip.hasMoreTokens()) {
        var termList: Array[(String, Long)] = new Array[(String, Long)](0)
        termList
      }
      else {
        val str = new StringTokenizer(trip.nextToken(), " ")

        var termList: Array[(String, Long)] = new Array[(String, Long)](0)
        while (str.hasMoreTokens) {
          val term = str.nextToken().trim()
          termList = termList ++ Array((term, 1L))

        }
        termList
      }
    }).distinct().keys.sortBy(identity)

  }

  def getTweets(textFile: RDD[String]): RDD[(Long,String)] = {
    val con=textFile map ( tt=>{ tt.split(" ").filter(_.length>1)})
    con map (cc=>{
      val id=cc(0).toLong
      val cont=cc.take(1).mkString(" ")
      (id,cont)
    })


  }

  def getDictionary(vocabulary: RDD[String]): Map[String,Long] = {
    vocabulary.zipWithIndex.collectAsMap.toMap

  }



  def getTups(textFile: RDD[String]): RDD[((Long, String), Long)] = {

    val lines = textFile.flatMap(e => {

      val trip = new StringTokenizer(e, " ")

      val id = trip.nextToken()

      if (!trip.hasMoreTokens()) {
        var termList: Array[(Long, String)] = new Array[(Long, String)](0)
        termList
      } else {
        val str = trip.nextToken()

        var termList = Array((id.trim().toLong, str))

        termList
      }
    })

    //lines.foreach(f => println(f + "**"))


    val tups = lines.flatMap {
      case (id, line) => {
        val str = new StringTokenizer(line, " ")
        var tuples: Array[((Long, String), Long)] = new Array[((Long, String), Long)](0)
        while (str.hasMoreTokens()) {
          val term = str.nextToken()
          tuples = tuples ++ Array(((id, term), 1L))
        }
        tuples
      }

    }


   //tups.foreach(f => println(f + "&&&"))
    return tups
  }


  //collect TF
  def calculateTF(tups: RDD[((Long, String), Long)]): RDD[((Long, String), Long)] = {
    val TF_count = tups.reduceByKey(_ + _)
    println("number of occurrence of each word in each document")
   // TF_count.foreach(t => println(t + "@"))

    return TF_count
  }

  //calculate IDF
  def calculateIDF(tups: RDD[((Long, String), Long)], numOfFile: Long): Map[String, Double] = {

    val TF_count = calculateTF(tups)
    val pairs = TF_count.map { case (tuple, count) => (tuple._2, 1L) }
    val DF = pairs.reduceByKey(_ + _)
    println("number of documents a word appears in")
    DF.foreach(f => println(f + "total"))
    val IDFList = DF.map {
      case (term, count) => {
        val idf = log10(numOfFile / (count.toDouble))
        (term, idf)
      }
    }

    IDFList.collectAsMap.toMap
  }

  //calculate TF-IDF
  def calculateTFIDF(vocabulary:RDD[String],TF_count:RDD[((Long, String), Long)]): RDD[(Long,Array[(String,Long)])]=
  {


    //calculate TF-IDF
    val prepare = TF_count.map {
      case (tuple, count) => (tuple._1, Array((tuple._2, count)))
    }
   val list=prepare.reduceByKey((a, b) => a ++ b)

    list.foreach(f => {
      println(f._1 + " ")
      f._2.foreach(ff => print(ff))
      println("")
    })
    return list


  }

  def getNumOfFeatures(vocabulary:RDD[String]):Long={
    vocabulary.count
  }

}
