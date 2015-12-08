package controllers

import models.FileName
import org.apache.spark.mllib.clustering.KMeans
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import java.io.{File, FileWriter}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.util.StringTokenizer
import org.apache.spark.mllib.linalg.{Vectors, Vector}
import org.apache.spark.rdd.RDD


import scala.math._


/**
  * Created by mengchending on 12/3/15.
  */





  object Clustering {

  def ReadFile(path:String):RDD[String]={
    val conf=new SparkConf()
      .setMaster("local[2]")
      .setAppName("firstSparkApp").set("spark.logConf", "true").set("spark.driver.host", "localhost")

    val sc=new SparkContext(conf)
    sc.textFile(path)
  }


  def cluster(text:RDD[String],stopwords:Array[String],filep:String): Unit ={

    val textFile=TfIdf.delimite(text,stopwords)

    val tweets=TfIdf.getTweets(textFile)

    val vocabulary= TfIdf.getVocabulary(textFile)

    val dictionary= TfIdf.getDictionary(vocabulary)

    val tups=TfIdf.getTups(textFile)

    val numOfFile= TfIdf.getNumOfFile(textFile)

    val tf=TfIdf.calculateTF(tups)

    val idf=TfIdf.calculateIDF(tups,numOfFile)

    val tfidf=TfIdf.calculateTFIDF(vocabulary,tf)

    val numOfFeatures= TfIdf.getNumOfFeatures(vocabulary)

    val dataset=Clustering.getDataset(tfidf,idf,numOfFeatures,dictionary)

    val clusters = KMeans.train(dataset,20,40)

    val WSSSE = clusters.computeCost(dataset)

    val clusterResult = tfidf.flatMap(e => {
      val id = e._1
      val tfs = e._2
      //calculate the number of terms in each document
      var numTerm = 0L
      tfs.foreach(f => { numTerm = numTerm + f._2 })
      val vector = Clustering.getVector(idf, numOfFeatures, tfs, numTerm, dictionary, e._1)
      val cluster = clusters.predict(vector)
      Array((id, cluster))
    })


    //clusterResult.foreach(f=>println(f))

    val fw = new FileWriter(new File(filep))

    val lines=textFile.flatMap(e => {

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

    val result =tweets.join(clusterResult)

    val find=result map( r => {
      val clusterid=r._2._2
      val tweetId=r._1
      ((clusterid,tweetId),1L)
    })

    val res=find.reduceByKey(_+_) map (
      rr=>
      {
        (rr._1._1,1L)
      }
      )

    val fres=res.reduceByKey(_+_)



    fres.collect.sortBy(f=>(f._1)).foreach(f => {

      fw.write(f._1.toString+" \tcluster"+"\thas"+"\t"+f._2+ "\ttweets\n")

      fw.flush

    })

    fw.flush()
    println("Within Set Sum of Squared Errors = " + WSSSE)
  }


    def getDataset(list: RDD[(Long, Array[(String, Long)])], IDFMap: Map[String, Double], numOfFeatures: Long, dictionary: Map[String, Long]): RDD[Vector] = {
      list.flatMap(e => {
        val tfs = e._2
        //calculate the number of terms in each document
        var numTerm = 0L
        tfs.foreach(f => {
          numTerm = numTerm + f._2
        })
        val vector = getVector(IDFMap, numOfFeatures, tfs, numTerm, dictionary, e._1)
        Array(vector)
      })


    }


    def getVector(IDFMap: Map[String, Double], numOfFeatures: Long, frequency: Array[(String, Long)], numTerms: Long, dictionary: Map[String, Long], Id: Long): Vector = {
      var indexArray = new Array[Int](0)
      var valueArray = new Array[Double](0)
      //      println("information for "+Id)
      frequency.foreach(
        f => {
          indexArray = indexArray ++ Array(dictionary(f._1).toInt)
          val idf = IDFMap(f._1)
          val tf = f._2.toDouble / numTerms.toDouble
          //      println("information of "+f._1+" tf"+tf+" idf"+idf)
          valueArray = valueArray ++ Array(tf * idf)
        })

      val result: Vector = Vectors.sparse(numOfFeatures.toInt, indexArray, valueArray)
      result
    }
  }


  object TfIdf {

    def getTweets(textFile: RDD[String]): RDD[(Long,String)] = {
      val con=textFile map ( tt=>{ tt.split(" ").filter(_.length>1)})
      con map (cc=>{
        val id=cc(0).toLong
        val cont=cc.take(1).mkString(" ")
        (id,cont)
      })


    }



    def delimite(textFile: RDD[String],stopwords:Array[String]):RDD[String]={


      val words= textFile map (_.split(" ").filter(_.length>0))
      val c=words map (f=>{f.filterNot(a=>{stopwords.contains(a)}) }.mkString(" "))
      c.foreach(aa=>println(aa))
      return c
    }

    def getNumOfFile(textFile: RDD[String]): Long = {
      textFile.count
    }

    def getVocabulary(textFile: RDD[String]): RDD[String] = {

      textFile.foreach(aa => println(aa))

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

      //lines.foreach(f => println(f + ""))


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


      //  tups.foreach(f => println(f + "\n"))
      return tups
    }


    //collect TF
    def calculateTF(tups: RDD[((Long, String), Long)]): RDD[((Long, String), Long)] = {
      val TF_count = tups.reduceByKey(_ + _)
      println("number of occurrence of each word in each document")
      TF_count.foreach(t => printf(t + "\n"))

      return TF_count

    }

    //calculate IDF
    def calculateIDF(tups: RDD[((Long, String), Long)], numOfFile: Long): Map[String, Double] = {

      val TF_count = calculateTF(tups)
      val pairs = TF_count.map { case (tuple, count) => (tuple._2, 1L) }
      val DF = pairs.reduceByKey(_ + _)
      println("number of documents a word appears in")
      DF.foreach(f => println(f + "\n"))
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
        f._2.foreach(f => print(f))
        println("")
      })
      return list


    }

    def getNumOfFeatures(vocabulary:RDD[String]):Long={
      vocabulary.count
    }

  }



