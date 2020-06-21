package learn.concurrent.util;

import learn.concurrent.data.SimpleDataPo;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.logging.Logger;

/**
 * 获取简单数据工具栏
 *
 * @auther lumj
 * @date 2020-06-18
 */
public class DataUtil {
    private static int TEXT_SIZE = 100;

    private static Logger logger = Logger.getLogger(DynamicThreadPoolExecutor.class.getSimpleName());

    private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<>();

    private DataUtil() {
    }

    /**
     * 根据简单规则生成simple list数据 线程池
     *
     * @param num 返回数据集合的大小
     * @param getText 获取text, function传入值是数据在list集合中的index
     * @return SimpleDataPo 数据集合
     */
    public static List<SimpleDataPo> getSimpleDataPoList(int num, BiFunction<Integer, Random, String> getText) {
        SimpleDataPo[] simpleDataPoArray = new SimpleDataPo[num];
        ThreadPoolExecutor executor =
                DynamicThreadPoolExecutor.getExecutor(DynamicThreadPoolExecutor.Strategy.NUM_OF_CPU, num);
        final VariableInt variableInt = new VariableInt(0);
        for (int index = 0; index < num; index++) {
            variableInt.setValue(index);
            Runnable runnable = new Runnable() {
                private int index = variableInt.getValue();

                @Override
                public void run() {
                    Random random = randomThreadLocal.get();
                    if (random == null) {
                        randomThreadLocal.set(new Random());
                    }
                    simpleDataPoArray[index] = new SimpleDataPo(Long.valueOf(index),
                            "simpleData" + index, "",
                            getText.apply(index, randomThreadLocal.get()));
                }
            };
            executor.execute(runnable);
        }
        while (!executor.getQueue().isEmpty()) {
            logger.info("data " + executor.getQueue().size() + " to build");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException("get list failure");
            }
        }
        executor.shutdown();
        return Arrays.asList(simpleDataPoArray);
    }

    /**
     * 根据简单规则生成simple list数据, 串行
     *
     * @param num 返回数据集合的大小
     * @param getText 获取text, function传入值是数据在list集合中的index
     * @return SimpleDataPo 数据集合
     */
    public static List<SimpleDataPo> getSimpleDataPoListSerial(int num, BiFunction<Integer, Random, String> getText) {
        List<SimpleDataPo> simpleDataPos = new ArrayList<>();
        Random random = new Random();
        for (int index = 0; index < num; index++) {
            simpleDataPos.add(new SimpleDataPo(Long.valueOf(index),
                        "simpleData" + index, "", getText.apply(index, random)));
        }
        return simpleDataPos;
    }

    /**
     * 根据简单规则生成simple list数据
     *
     * @param num 返回数据集合的大小
     * @param getText 获取text, function传入值是数据在list集合中的index
     * @param threadCount 线程数组数量
     * @return SimpleDataPo 数据集合
     */
    public static List<SimpleDataPo> getSimpleDataPoListByThreadArray(
            int num, BiFunction<Integer, Random, String> getText, int threadCount) {
        SimpleDataPo[] simpleDataPoArray = new SimpleDataPo[num];
        Thread[] threads = new Thread[threadCount];
        int numSub = num/5;
        for (int index = 0; index < threadCount; index++) {
            threads[index] = new Thread(() -> {
                int dataIndex = Integer.parseInt(Thread.currentThread().getName());
                logger.info("init data " + (dataIndex * numSub) + " to " + ((dataIndex + 1) * numSub));
                Random random = new Random();
                for (int indexSub = dataIndex * (numSub - 1); indexSub < (dataIndex + 1) * (numSub + 1); indexSub++) {
                    simpleDataPoArray[indexSub] = new SimpleDataPo(Long.valueOf(indexSub),
                        "simpleData" + indexSub, "", getText.apply(indexSub, random));
                }
                logger.info("thread " + dataIndex + " finish");
            }, index + "");
            threads[index].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Arrays.asList(simpleDataPoArray);
    }

    /**
     * 根据简单规则生成simple list数据
     *
     * @param num 返回数据集合的大小
     * @param textSize text字段大小
     * @return SimpleDataPo 数据集合
     */
    public static List<SimpleDataPo> getSimpleDataPoList(int num, int textSize) {
        return getSimpleDataPoList(num, (integer, random) ->
                RandomStringUtils.random(
                        textSize, 0, 0, true, true, null, random));
    }

    /**
     * 根据简单规则生成simple list数据， 默认text大小为100
     *
     * @param num 返回数据集合的大小
     * @return SimpleDataPo 数据集合
     */
    public static List<SimpleDataPo> getSimpleDataPoList(int num) {
        return getSimpleDataPoList(num, TEXT_SIZE);
    }


    private static class VariableInt {
        int value;

        VariableInt(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
