package com.ljx.gulimall.search;

import java.util.concurrent.*;

public class GuliMallThreadTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture.runAsync(() -> {
//            System.out.println("无返回结果");
//        });

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("有返回结果");
//            return 10 / 2;
//        });

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("有返回结果");
//
//            return 10 / 2;
//        }).whenComplete((result, exception) -> {
//            try {
//                Thread.sleep(2000);
//                System.out.println("休眠结束");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        System.out.println(222);



//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("有返回结果");
//            return 10 / 2;
//        }).whenCompleteAsync((result, exception) -> {
//            try {
//                Thread.sleep(2000);
//                System.out.println("休眠结束");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("有返回结果");
//            return 10 / 2;
//        }).exceptionally(throwable -> {
//            System.out.println("异常");
//            return 10 / 3;
//        });

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("有返回结果");
//            return 10 / 2;
//        }).handle((res, error) -> {
//            System.out.println("异常：" + error.getMessage());
//            return 10 / 3;
//        });


//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务一");
//            return 1;
//        }).thenAccept((res) -> {
//            System.out.println("任务二，任务一返回值：" + res);
//        });

//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务一");
//            return 1;
//        }).thenApply((res) -> {
//            System.out.println("任务二，任务一返回值：" + res);
//            return "新结果，" + res;
//        });


//        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务一");
//            return 1;
//        });
//        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务二");
//            return 2;
//        });
//        f1.thenAcceptBoth(f2, (r1, r2) -> {
//            System.out.println("任务一结果：" + r1 + "，任务二结果：" + r2);
//        });
//
//
//        CompletableFuture<Void> ff1 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务一");
//        });
//        CompletableFuture<Void> ff2 = CompletableFuture.runAsync(() -> {
//            System.out.println("任务二");
//        });
//
//        ff1.runAfterBoth(ff2, () -> {
//            System.out.println("任务一和任务二完成");
//        });


//        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务一");
//            return 1;
//        });
//        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(1000);
//                System.out.println("任务二");
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            return 2;
//        });
//        f1.runAfterEither(f2, () -> {
//            System.out.println("任务一或者任二其中一个处理完成");
//        });
//
//        f1.acceptEither(f2, (res) -> {
//            System.out.println("任务一或者任二其中一个处理完成，处理结果：" + res);
//        });
//        CompletableFuture<String> future = f1.applyToEither(f2, (res) -> {
//            System.out.println("任务一或者任二其中一个处理完成，处理结果：" + res);
//
//            return "新结果" + res;
//        });



        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务一");
            return 1;
        });
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("任务二");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 2;
        });
        CompletableFuture<Integer> f3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("任务三");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 3;
        });

        CompletableFuture.allOf(f1, f2, f3).get();
        CompletableFuture.anyOf(f1, f2, f3).get();

        System.out.println("所有任务都执行完成");

//        System.out.println("结果：" + future.get());

    }



}
