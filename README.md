 ScalaFinal is the whole Finder for Scala Final Project
 the ScalaFinalProjectwithUI finder is the final version of the project. It includes all codes,tests. It uses Play Framework to interact  with the stream. 

 the Streaming Finder is the a finder that stores the funciton codes for project.
 the ScalaTest File UnitTest stores the tests incase of some version conflicts.
 
When using activator to run the ScalaFinalProjectwithUI, the steps are below:
1. execute activator run under ScalaFinalProjectwithUI
2. the first page is index page, input the directory(/directory/filename) that you want to store the real-time tweets in. then click 'load'
3. the second page is showing that the project is loading the real-time data, click stop when you want to stop loading. the number of tweets you download is related the loading time.
4. the third page is cluster page, input the directory(/directory/filename) that you want to store result of clustering in.
5. the final page is result page,it shows the reults of the clustering 
  " 0 cluster has 3 tweets"  "0" stands of the cluster Id, "3" stands of number of tweets in this cluster.
  

When using the Streaming finder to run the project, the steps are below:
1. The first step of the Project is to get the data, it was written in TwitterPopularTags.scala. In this file, we used the sparkStreaming    and TwitterApi to get real-time tweets. We did the tokenization to these tweets and then got the preprocessed data in Format String      and then we stored it into a file named tweets. For tokenization,we wrote a tokenizer.scala to operate many functions to remove the      meaningless symbols and some other content like urls,emotions. We wrote the UnitTest file to check each functions and regular            expressions in tokenizer file. The UnitTest file is put into the Tests finder. 

2. The second step of the project is to read the data from the file and then calculate tf-idf, vector for each documents. The outpus of     the each sub-steps have screenshops that are collected in the file named ScreeShots For Clustering Operation outputs for each step.

3. The third step of the project is using the vectors for all documents as the dataset to calculater the distance by K-Means and to         cluster the terms

