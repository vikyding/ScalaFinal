 ScalaFinal is the whole Finder for Scala Final Project
 the Streaming Finder is the main Finder that stores the codes for project.
 Since the conflicts of version, the ScalaTest File UnitTest for functions cannot be ran in the Streaming. So we move the ScalaTest file  UnitTest to a new Finder named Tests.

1. The first step of the Project is to get the data, it was written in TwitterPopularTags.scala. In this file, we used the sparkStreaming    and TwitterApi to get real-time tweets. We did the tokenization to these tweets and then got the preprocessed data in Format String      and then we stored it into a file named tweets. For tokenization,we wrote a tokenizer.scala to operate many functions to remove the      meaningless symbols and some other content like urls,emotions. We wrote the UnitTest file to check each functions and regular            expressions in tokenizer file. The UnitTest file is put into the Tests finder. 

2. The second step of the project is to read the data from the file and then calculate tf-idf, vector for each documents. The outpus of     the each sub-steps have screenshops that are collected in the file named ScreeShots For Clustering Operation outputs for each step.

3. The third step of the project is using the vectors for all documents as the dataset to calculater the distance by K-Means and to         cluster the terms

