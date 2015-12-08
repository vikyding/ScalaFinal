package controllers

import java.io.{File, FileWriter}

import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.SparkContext
import org.apache.spark.streaming.twitter._
import play.api.Play
import org.apache.spark.SparkConf
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import scala.collection.immutable.HashMap
/**
  * Created by mengchending on 12/7/15.
  */
object LoadData {

  var ssc:StreamingContext=null

    def Load(filePath:String){

      val args=Array[String]("")

      //val consumerKey = Play.current.configuration.getString("consumer_key").get
      //val consumerSecret = Play.current.configuration.getString("consumer_secret").get
      //val accessToken = Play.current.configuration.getString("access_token").get
      //val accessTokenSecret = Play.current.configuration.getString("access_token_secret").get

      // Authorising with your Twitter Application credentials
      //val twitter = new TwitterFactory().getInstance()
      //twitter.setOAuthConsumer(consumerKey, consumerSecret)
      //twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret))
      System.setProperty("twitter4j.oauth.consumerKey", "WtRud6dwMty4RmontMqWecB7e")
      System.setProperty("twitter4j.oauth.consumerSecret", "otkWEuegb7pAgCsmyRQ7TPRwzoX78gyoLnBrEO2tJqNtRXkqVo")
      System.setProperty("twitter4j.oauth.accessToken", "3042551499-zXnAJPUxxZafFvAN7JAmJ8SftH3FYNw38NfXyj4")
      System.setProperty("twitter4j.oauth.accessTokenSecret", "f7QKgpN7UdAxfjYn2X4PBsJxuY7EZf02dBouaD9yCW4y7")

      val conf=new SparkConf()
      .setMaster("local[2]")
      .setAppName("firstSparkApp").set("spark.logConf", "true").set("spark.driver.host", "localhost")

      val sc=new SparkContext(conf)

      ssc=new StreamingContext(sc,Seconds(2))

      val filters=args.takeRight(0)

      val TwitterStream=TwitterUtils.createStream(ssc,None,filters).filter(_.getLang == "en")


      val TweetWords=Twitter.Parser.Parse(TwitterStream)

      val originalTweet= for( rdd <- TweetWords) yield rdd._1.toString +" " + rdd._2

      //val list = prepare.reduceByKey((a, b) => a ++ b)

      val fw = new FileWriter(new File(filePath))
  println("success")
      originalTweet.foreachRDD(rdd=> {
        rdd.collect.foreach(f => {
          fw.write(f + "\n")
          fw.flush
        })
      })

      fw.flush()
println("great")
      ssc.start()

    }

  def Stop {
    ssc.stop()
    println("stop ssc")
  }

    //def StopLoad(ssc:StreamingContext)={
     // ssc.stop(false,true)
    //}

}
