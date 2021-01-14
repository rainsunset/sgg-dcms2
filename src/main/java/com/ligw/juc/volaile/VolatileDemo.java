package com.ligw.juc.volaile;

import java.util.concurrent.TimeUnit;

/**
 * @Description: [ˈvɒlətaɪl]
 * @Author: Amo
 * @CreateDate: 2021/1/14
 */

public class VolatileDemo {
    // 验证volatile的可见性
    // 假如 int number = 0; number变量没有添加volatile关键词修饰，没有可见性。else 。。。。
    private void testVisibility() {
        MyData myData = new MyData();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\tcome in,number:" + myData.number);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myData.add260();
            System.out.println(Thread.currentThread().getName() + "\tupdated,number:" + myData.number);
        }, "showable").start();
        while (0 == myData.number) {

        }
        // main线程
        System.out.println(Thread.currentThread().getName() + "\tover,number:" + myData.number);
    }

    // 验证volatile不保证原子性
    // 不可分割，完整性 也即某个线程正在做某个具体业务是，中间不可以被加塞或者被分割，需要整体完整
    // 要么同时成功 要么同时失败
    private void testAtomic() {
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myData.increase();
                }
            }, "atomic:" + i).start();
        }

        // 线程大于2就交出线程(默认后台有两个线程 main线程和jc线程)
        while (Thread.activeCount() > 2) {
            System.out.println("ActiveThreadCount:" + Thread.activeCount());
            Thread.yield();
        }
        // 最终的结果会出现不等于20000的结果
        System.out.println(Thread.currentThread().getName() + "over,number:" + myData.number);
    }

    public static void main(String[] args) {
        VolatileDemo volatileDemo = new VolatileDemo();
//        volatileDemo.testVisibility();
        volatileDemo.testAtomic();
    }
}

class MyData {
    // 被volatile修饰后 只要有一个线程修改了number,就会及时通知其他线程著物理内存的值已经被修改
    volatile int number = 0;

    public void add260() {
        this.number = 60;
    }

    public void increase() {
        number++;
    }

}
