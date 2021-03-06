/**
 * Ethan Petuchowski
 * 3/8/14
 *
 * Goal:
 * -----
 * Efficiently download and analyze comments from YouTube
 * by passing them between different actors that do different
 * pieces of the whole pipeline.
 *
 * Problem this Addresses:
 * -----------------------
 * My app currently does the following:
 *
 *  1. Download next page of youtube comments (network)
 *  2. Parse comments into components (CPU)
 *  3. Calculate sentiment value of comments (CPU)
 *  4. Store comments in database (I/O)
 *  5. Repeat as necessary
 *
 * NOTE: Each repetition has to wait on the last, but this is for nought!
 *
 * I'd like to parallelize this by introducing actors which are each
 * responsible for a different piece of the action, but I could have
 * a bunch of them doing performing each task at the same time, with
 * no hard limit as far as the program itself is concerned.
 */

package na.ethan.AkkaRetrieval

import akka.actor._
import com.google.gdata.client.youtube.{YouTubeQuery, YouTubeService}
import java.net.URL
import com.google.gdata.data.youtube.{CommentEntry, CommentFeed, VideoEntry}
import scala.collection.JavaConverters._

case class IDToLookFor(id: String)
case object RetrieveNextPage
case class ParseYouTubeComments(comments: List[CommentEntry])

// TODO create abstract base Retriever class

case class YouTubeVideoManager(var id: String) extends Actor {
    def this() = this("")

    // TODO find out how many total (first-level) comments the video has to its name
    // TODO create YouTubeCommentPageRetrievers to retrieve all the pages (viz. #TotalComments / RESULTS_PER_PAGE)
    //      and of course tell them to do so

    def receive = ???
}

/**
 * Downloads a given page of comments from youtube
 */
case class YouTubeCommentPageRetriever(var id: String, var page_num: Int, var has_more: Boolean) extends Actor {

    def this() = this("", 0, true) // it doesn't seem like you can accomplish this with default arguments

    // TODO this should be invoked by the YouTubeVideoManager to download 1 page,
    // TODO it should *be able to assume* that that page of comments exists

    val parser = context.actorOf(Props[YouTubeCommentParser], "YouTubeCommentParser")
    val lines = scala.io.Source.fromFile("/etc/googleIDKey").mkString.split("\n")
    val service : YouTubeService = new YouTubeService(lines(0), lines(1))

    def getComments = {
        val videoEntryUrl = "http://gdata.youtube.com/feeds/api/videos/" + id
        val videoEntry = service.getEntry(new URL(videoEntryUrl), classOf[VideoEntry])
        val comments = videoEntry.getComments
        if (comments != null) {
            val commentsUrl: String = comments.getFeedLink.getHref
            println(commentsUrl)

            // Get the comment feed use a new query
            val youtubeQuery = new YouTubeQuery(new URL(commentsUrl))
            val RESULTS_PER_PAGE = 50 // this is the max
            youtubeQuery.setMaxResults(RESULTS_PER_PAGE)
            youtubeQuery.setStartIndex(page_num * RESULTS_PER_PAGE + 1) // TODO find out if this actually works
            val commentUrlFeed = youtubeQuery.getUrl
            println("commentUrlFeed" + commentUrlFeed)
            val commentFeed = service.getFeed(commentUrlFeed, classOf[CommentFeed])
            val entriesList = commentFeed.getEntries.asScala.toList

            val numResults = commentFeed.getTotalResults
            println(s"Total Results = $numResults")

            has_more = numResults >= RESULTS_PER_PAGE

            parser ! ParseYouTubeComments(entriesList)
        }
    }

    def receive: Actor.Receive = {
        case IDToLookFor(id) => {
            println(s"looking for: $id")
            this.id = id
            page_num = 0
            has_more = true
        }
        case RetrieveNextPage => {
            if (has_more) {
                println(s"retrieving page: $page_num")
                getComments
                page_num += 1
            } else {
                println("\nthere are no more comments\n")
            }
        }
    }
}

/**
 * Downloads a given page of comments from Google Plus
 */
case class GPlusRetriever extends Actor {
    var gplus_id = ""
    var page_num = 0
    val parser = context.actorOf(Props[GPlusCommentParser], "GPlusCommentParser")

    def receive: Actor.Receive = {
        case IDToLookFor(id) => {
            println(s"looking for: $id")
            gplus_id = id
            page_num = 0
        }
        case RetrieveNextPage => {
            println(s"retrieving page: $page_num")
            page_num += 1
        }
    }
}

/**
 * Turns a page of comments into a list of comment components
 */
case class YouTubeCommentParser extends Actor {
    def receive = {
        case ParseYouTubeComments(comments) => {
            comments.foreach(c => println(c.getPlainTextContent))
            // TODO analyze the sentiment of all de comments
        }
    }
}

/**
 * Turns a page of comments into a list of comment components
 */
case class GPlusCommentParser extends Actor {
    def receive = {
        case _ => ???
    }
}

/**
 * Returns the sentiment value of a given piece of text
 */
case class SentimentAnalyzer extends Actor {
    def receive = {
        case _ => ???
    }
}

case object HelloGDataWithAkka extends App {
    println("First Line")
    val system = ActorSystem(name="helloGData")

    // TODO ask the YouTubeVideoManager to retrieve ALL the pages for a given video, instead of
    //      trying to download a set number of pages

    val retriever : ActorRef = system.actorOf(props=Props[YouTubeCommentPageRetriever], name="retriever")
    val inbox = Inbox.create(system)
    retriever ! IDToLookFor("ADos_xW4_J0")  // <== "Intro to Google Data...etc."
    retriever ! RetrieveNextPage
    retriever ! RetrieveNextPage
    println("Final Line")
}
