/**
  * Created by mengchending on 11/17/15.
  */


import java.io.{File, FileWriter}

import org.apache.spark.streaming.{Time, Seconds, StreamingContext}

import org.apache.spark.streaming.twitter._
import org.apache.spark.SparkConf






object TwitterPopularTags {

  def main(args: Array[String]) {


    //val Array(consumerKey, consumerSecret, accessToken, accessTokenSecret) = args.take(4)
    val filters = args.takeRight(0)

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generat OAuth credentials
    System.setProperty("twitter4j.oauth.consumerKey", "WtRud6dwMty4RmontMqWecB7e")
    System.setProperty("twitter4j.oauth.consumerSecret", "otkWEuegb7pAgCsmyRQ7TPRwzoX78gyoLnBrEO2tJqNtRXkqVo")
    System.setProperty("twitter4j.oauth.accessToken", "3042551499-zXnAJPUxxZafFvAN7JAmJ8SftH3FYNw38NfXyj4")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "f7QKgpN7UdAxfjYn2X4PBsJxuY7EZf02dBouaD9yCW4y7")

    val sparkConf = new SparkConf().setAppName("TwitterPopularTags").setMaster("local[2]")

    val ssc = new StreamingContext(sparkConf, Seconds(2))

    val TwitterStream=TwitterUtils.createStream(ssc,None,filters).filter(_.getLang == "en")


    val TweetWords=Parser.Parse(TwitterStream)

    val originalTweet= for( rdd <- TweetWords) yield rdd._1.toString +" " + rdd._2


    //val list = prepare.reduceByKey((a, b) => a ++ b)

    val fw = new FileWriter(new File("/Users/mengchending/Desktop/result/tweets"))

    originalTweet.foreachRDD(rdd=> {
      rdd.collect.foreach(f => {
        fw.write(f + "\n")
        fw.flush
      })
    })

    fw.flush()
    //tweets.saveAsTextFiles("/Users/mengchending/Desktop/result/re")



    //val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
     // .map{case (topic, count) => (count, topic)}
    //  .transform(_.sortByKey(false))

    //val topCounts10 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(10))
    //  .map{case (topic, count) => (count, topic)}
    //  .transform(_.sortByKey(false))


    // Print popular hashtags
    //topCounts60.foreachRDD(rdd => {
    //  val topList = rdd.take(10)
    //  println("\nPopular topics in last 60 seconds (%s total):".format(rdd.count()))
     // topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
    //})



    ssc.start()
    ssc.awaitTermination()

  }
}