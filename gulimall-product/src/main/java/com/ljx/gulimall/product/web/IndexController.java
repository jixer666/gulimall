package com.ljx.gulimall.product.web;

import com.ljx.gulimall.product.model.entity.CategoryEntity;
import com.ljx.gulimall.product.model.vo.Category2Vo;
import com.ljx.gulimall.product.service.CategoryService;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-26  16:33
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;


    @GetMapping({"/", "/index"})
    public String getIndexPage(Model model) {
        List<CategoryEntity> categories = categoryService.getCategoryByPId(0L);

        model.addAttribute("categories", categories);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Category2Vo>> getCategoryList() {
        return categoryService.getCategoryListWithSpringCache();
    }

    @ResponseBody
    @GetMapping("/index/json/catalog/delete")
    public void deleteCategory() {
        categoryService.deleteCategorySpringCache();
    }


    @ResponseBody
    @GetMapping("/hello")
    public String getHello() {
        RLock lock = redissonClient.getLock("test-lock");
        lock.lock();
        try {
            System.out.println("加锁" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        System.out.println("释放锁");
        return "hello";
    }

    @ResponseBody
    @GetMapping("/write")
    public String write() {
        ReadWriteLock lock = redissonClient.getReadWriteLock("test-wr-lock");
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            System.out.println("加写锁" + Thread.currentThread().getId());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            writeLock.unlock();
        }
        System.out.println("释放写锁");
        return "hello";
    }

    @ResponseBody
    @GetMapping("/read")
    public String read() {
        ReadWriteLock lock = redissonClient.getReadWriteLock("test-wr-lock");
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            System.out.println("加读锁" + Thread.currentThread().getId());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            readLock.unlock();
        }
        System.out.println("释放读锁");
        return "hello";
    }


    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("test-semaphore");
//        semaphore.acquire();
        boolean b = semaphore.tryAcquire();
        return "ok" + b;
    }

    @ResponseBody
    @GetMapping("/go")
    public String ok() throws InterruptedException {
        RSemaphore semaphore = redissonClient.getSemaphore("test-semaphore");
        semaphore.release();
        return "ok";
    }

    @ResponseBody
    @GetMapping("/cache")
    public List<String> getCache() {
        return categoryService.getCache();
    }

    @ResponseBody
    @GetMapping("/cache/delete/all")
    public void deleteCacheAll() {
        categoryService.testDeleteCacheAll();
    }

    @ResponseBody
    @GetMapping("/cache/update")
    public List<String> updateCache() {
        return categoryService.updateCache();
    }
}
