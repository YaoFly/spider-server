package crawl;

import com.alibaba.fastjson.JSON;
import domain.WeiXinData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

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

    private SpiderThread[] spiderThreads;
    private SearchThread[] searchThreads;
    private Logger logger = LoggerFactory.getLogger(SpiderGlobal.class);
    Integer article = 0;

    private EntityManagerFactory emf;

    void init(){
        emf = Persistence.createEntityManagerFactory("SimplePU");
        initSpiderThread();
        initSearchThread();
        Runtime.getRuntime().addShutdownHook(new Thread(this::gracefullyShutdown));
    }

    private void initSearchThread(){
        searchThreads = new SearchThread[Define.SEARCH_THREAD_NUMS];
        for(int i=0;i<searchThreads.length;i++){
            searchThreads[i] = new SearchThread();
            searchThreads[i].start();
        }
    }
    private void initSpiderThread(){
        spiderThreads = new SpiderThread[Define.SPIDER_THREAD_NUMS];
        for(int i=0;i<spiderThreads.length;i++){
            spiderThreads[i] = new SpiderThread();
            spiderThreads[i].start();
        }
    }

    private void gracefullyShutdown(){
        logger.info("Stop Spider thread...");
        //直接终止
        for(SpiderThread spiderThread:spiderThreads){
            spiderThread.interrupt();
        }
        logger.info("Stop Search thread...");
        //直接终止
        for(SearchThread searchThread:searchThreads){
            searchThread.interrupt();
        }
        logger.info("Gracefully shutdown.");
        emf.close();
    }

    private int loadBalancing(BaseQueue[] bq){
        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
        for(int i =0;i<bq.length;i++){
            map.put(bq[i].queueSize(),i);
        }
        Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator();
        return entries.next().getValue();
    }

    public void spiderQueuePut(WeiXinData data){
        try {
            spiderThreads[loadBalancing(spiderThreads)].put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("InterruptedException spide:"+ JSON.toJSONString(data));
        }
    }

    public void searchQueuePut(WeiXinData data){
        try {
            searchThreads[loadBalancing(searchThreads)].put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("InterruptedException search:"+ JSON.toJSONString(data));
        }
    }

    public String spiderQueueSize(){
        int[] attr = new int[spiderThreads.length];
        for(int i=0;i<spiderThreads.length;i++){
            attr[i]=spiderThreads[i].queueSize();
        }
        return JSON.toJSONString(attr);
    }

    public String searchQueueSize(){
        int[] attr = new int[searchThreads.length];
        for(int i=0;i<searchThreads.length;i++){
            attr[i]=searchThreads[i].queueSize();
        }
        return JSON.toJSONString(attr);
    }

    public EntityManagerFactory getEmf() {
        return emf;
    }

    public void setEmf(EntityManagerFactory emf) {
        this.emf = emf;
    }
}
