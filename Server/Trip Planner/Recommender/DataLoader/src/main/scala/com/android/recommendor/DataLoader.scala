package com.android.recommendor

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

/***
 * Spot数据集
 *
 * sight id:sid
 * sight name:name
 * sight location:location
 * sight description:desc
 * suggestion visiting time:visitingtime
 * sight type:genre
 */

case class Sight(sid:Int, name:String, city:String, tag:String, address:String,
                 desc: String, visitNum:Int, picUrl:String, price:String, detailUrl:String,
                 level:String, starLevel:Float)

/***
 * Rating数据集
 *
 * 1,31,2.5,1260759144
 * user id:uid
 * sight id:sid
 * user score: score
 * timestamp:timestamp
 */
case class Rating(uid:Int, sid:Int, score:Double, timestamp:Int)

/***
 * Tag数据集
 *
 * 15，1955，城市，1193435061
 * user id:uid
 * sight id:sid
 * sight tag: tag
 * timestamp:timestamp
 */
case class Tag(uid:Int, sid:Int, tag:String, timestamp:Int)

//把 mongo和ES的配置封装成样例类
/***
 *
 * @param uri   MongoDB的连接
 * @param db    MongoDB的数据库
 */
case class MongoConfig(uri:String, db:String)

/***
 *
 *
 * @param httpHosts         http主机列表，逗号分隔 9200
 * @param transportHosts    transport主机列表    9300
 * @param index             需要操作的索引
 * @param clustername       集群名称，默认配置名
 */
case class ESConfig(httpHosts:String, transportHosts:String,index:String,clustername:String)

object DataLoader {

  //定义常量
  val SIGHT_DATA_PATH = "C:\\Users\\40120\\Desktop\\Trip Planner\\Recommender\\DataLoader\\src\\main\\resources\\sight_comb.csv"
  val RATING_DATA_PATH = "C:\\Users\\40120\\Desktop\\Trip Planner\\Recommender\\DataLoader\\src\\main\\resources\\ratings.csv"
  val TAG_DATA_PATH = "C:\\Users\\40120\\Desktop\\Trip Planner\\Recommender\\DataLoader\\src\\main\\resources\\tags.csv"

  val MONGODB_SIGHT_COLLECTION = "Sight"
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_TAG_COLLECTION = "Tag"
  val ES_SIGHT_INDEx = "Sight"

  def main(args: Array[String]): Unit = {

     var config = Map(
       "spark.cores"->"local[*]",
       "mongo.uri"->"mongodb://localhost:27017/recommender",
       "mongo.db"->"recommender",
       "es.httpHosts"->"localhost:9200",
       "es.index"->"recommender",
       "es.cluster.name"->"elasticsearch"
     )

     //创建一个sparkConf
     val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("DataLoader")

     //创建一个SparkSession
     val spark = SparkSession.builder().config(sparkConf).getOrCreate()

     import spark.implicits._

     //加载数据
    /**
     * sid:Int, name:String, city:String, tag:String, address:String,
     * desc: String, visitNum:Int, picUrl:String, price:String, detailUrl:String,
     * level:String, starLevel:Float
     */
    val sightRDD = spark.sparkContext.textFile(SIGHT_DATA_PATH)
    val sightDF = sightRDD.map(
      item => {
        val attr = item.split("\\^")
        Sight(attr(0).toInt, attr(1).trim, attr(2).trim, attr(3).trim, attr(4).trim, attr(5).trim, attr(6).toInt, attr(7).trim, attr(8).trim, attr(9).trim, attr(10).trim, attr(11).toFloat)
      }
    ).toDF()

    val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }).toDF()

    val tagRDD = spark.sparkContext.textFile(TAG_DATA_PATH)
      val tagDF = tagRDD.map(item => {
        val attr = item.split(",")
        Tag(attr(0).toInt, attr(1).toInt, attr(2).trim, attr(3).toInt)
      }).toDF()




    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

     //将数据保存到mongoDB
     storeDataInMongoDB(sightDF, ratingDF, tagDF)

     //数据预处理
     //保存数据到es
     storeDataInES()

     spark.stop()
  }

  def storeDataInMongoDB(sightDF:DataFrame, ratingDF:DataFrame, tagDF:DataFrame)(implicit mongoConfig:MongoConfig): Unit ={
    //新建一个mongoDB连接
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    //如果mongoDB中已经有相应数据库，先删除
    mongoClient(mongoConfig.db)(MONGODB_SIGHT_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).dropCollection()
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).dropCollection()

    //将DF数据写入对应的mongodb表中
    sightDF.write
      .option("uri",mongoConfig.uri)
      .option("conllection",MONGODB_SIGHT_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
    ratingDF.write
      .option("uri",mongoConfig.uri)
      .option("conllection",MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
    tagDF.write
      .option("uri",mongoConfig.uri)
      .option("conllection",MONGODB_TAG_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //对数据表检索引
    mongoClient(mongoConfig.db)(MONGODB_SIGHT_COLLECTION).createIndex( MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex( MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION).createIndex( MongoDBObject("mid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex( MongoDBObject("uid" -> 1))
    mongoClient(mongoConfig.db)(MONGODB_TAG_COLLECTION).createIndex( MongoDBObject("mid" -> 1))

    mongoClient.close()
  }
  def storeDataInES(): Unit ={

  }

}
