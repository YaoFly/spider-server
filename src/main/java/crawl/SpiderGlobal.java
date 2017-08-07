package crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ooopic on 2017/4/14.
 */
public class SpiderGlobal {
    private SpiderGlobal() {}
    private static SpiderGlobal instance;

    public synchronized static SpiderGlobal getInstance() {
        if (instance == null) {
            instance = new SpiderGlobal();
        }
        return instance;
    }

    ThreadPoolExecutor spiderThreads = new ThreadPoolExecutor(1,5,1, TimeUnit.HOURS, new LinkedBlockingQueue<>());
    ThreadPoolExecutor searchThreads = new ThreadPoolExecutor(10,20,1, TimeUnit.HOURS, new LinkedBlockingQueue<>());
    ThreadPoolExecutor downloadThreads = new ThreadPoolExecutor(1,5,1, TimeUnit.HOURS, new LinkedBlockingQueue<>());
    private Logger logger = LoggerFactory.getLogger(SpiderGlobal.class);
    private Integer article = 0;

    private EntityManagerFactory emf;

    void init(){
        emf = Persistence.createEntityManagerFactory("SimplePU");
        Runtime.getRuntime().addShutdownHook(new Thread(this::gracefullyShutdown));
    }

    private void gracefullyShutdown(){
        logger.info("Stop Spider thread...");
        //直接终止
        spiderThreads.shutdown();
        logger.info("Stop Search thread...");
        //直接终止
        searchThreads.shutdown();
        logger.info("Stop Download thread...");
        //直接终止
        downloadThreads.shutdown();
        emf.close();
        logger.info("Gracefully shutdown.");
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }
    synchronized Integer getArticle() {return article;}
    synchronized void articleInc(){article++;}
}
