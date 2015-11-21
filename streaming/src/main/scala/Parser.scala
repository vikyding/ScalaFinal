
import org.apache.spark.streaming.dstream.DStream
import twitter4j.Status
/**
  * Created by mengchending on 11/20/15.
  */

object Parser {

  def Parser(tweetstream:DStream[Status]):DStream[(Long,String)] ={

    for(
      aa <-tweetstream

    ) yield (aa.getId,aa.getText)

  }

  def SplitAndFilter(tweet:String)={
    val rStart= """^(?:RT\s@|@)(?:\S*:)(\S*)$""".r
    val words= tweet match{
      case rStart(s)=>s
      case _=>
      {
        throw new IllegalArgumentException(tweet)
      }
    }


  }


}
