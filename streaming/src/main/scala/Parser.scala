
import org.apache.spark.streaming.dstream.DStream
import twitter4j.Status
/**
  * Created by mengchending on 11/20/15.
  */

object Parser {


  def Parse(tweetstream:DStream[Status]):DStream[(Long,String)] ={

    for(
      aa <-tweetstream

    ) yield (aa.getId,Tokenizer.tokenizeToString(aa.getText))

  }





}
