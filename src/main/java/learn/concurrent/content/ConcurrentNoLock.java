package learn.concurrent.content;

import learn.concurrent.data.SimpleDataPo;
import learn.concurrent.util.DataUtil;
import org.joda.time.DateTimeUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 减少上下文切换：
 * 1、无锁并发编程：
 *    通过数据的id按照hash算法取模分段，用不同的线程处理数据
 *
 * 这个有点无语，要是数据是隔离的，可以直接使用并行流或fork/join框架，根本不会相互影响
 * 归根到底还是最优线程池的创建问题
 */
public class ConcurrentNoLock {
    public static void main(String[] args) {
        List<SimpleDataPo> simpleDataPoList = DataUtil.getSimpleDataPoList(100000, 10000);

        // 任务：查找统计字符：a
        long timeMillis1 = DateTimeUtils.currentTimeMillis();
        AtomicInteger count = new AtomicInteger(0);
        simpleDataPoList.parallelStream().forEach(simpleDataPo -> {
            char[] chars = simpleDataPo.getText().toCharArray();
            for (char ch : chars) {
                if (ch == 'a') {
                    count.addAndGet(1);
                }
            }
        });
        long timeMillis2 = DateTimeUtils.currentTimeMillis();
        System.out.println(count.get());
        System.out.println("-----------use time: " + (timeMillis2 - timeMillis1) + " ms");

        // 任务：替换字符：a -> A
        long timeMillis3 = DateTimeUtils.currentTimeMillis();
        simpleDataPoList.parallelStream().forEach(simpleDataPo -> {
            String text = simpleDataPo.getText();
            text.replaceAll("a", "A");
        });
        long timeMillis4 = DateTimeUtils.currentTimeMillis();
        System.out.println("-----------use time: " + (timeMillis4 - timeMillis3) + " ms");
    }

    private static void executeTask(Consumer<SimpleDataPo> task, SimpleDataPo simpleDataPo) {
        task.accept(simpleDataPo);
    }
}

