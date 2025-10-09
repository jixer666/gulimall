package com.ljx.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.common.constant.CacheConstant;
import com.ljx.gulimall.product.model.dto.CategoryDto;
import com.ljx.gulimall.product.model.vo.Category2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.CategoryDao;
import com.ljx.gulimall.product.model.entity.CategoryEntity;
import com.ljx.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryDto> listTree() {
        List<CategoryEntity> categories = categoryDao.selectList(new LambdaQueryWrapper<>());
        return categories.stream()
                .filter(item -> item.getParentCid().equals(0L))
                .map(item -> {
                    CategoryDto categoryDto = BeanUtil.copyProperties(item, CategoryDto.class);
                    List<CategoryDto> childList = getCategoryChildList(item.getCatId(), categories);
                    categoryDto.setChildren(CollUtil.isEmpty(childList) ? null : childList);
                    return categoryDto;
                }).sorted(Comparator.comparingInt(CategoryDto::getSort))
                .collect(Collectors.toList());
    }

    private List<CategoryDto> getCategoryChildList(Long catId, List<CategoryEntity> categories) {
        if (CollUtil.isEmpty(categories)) {
            return new ArrayList<>();
        }
        return categories.stream().filter(item -> item.getParentCid().equals(catId))
                .map(item -> {
                    CategoryDto categoryDto = BeanUtil.copyProperties(item, CategoryDto.class);
                    List<CategoryDto> childList = getCategoryChildList(item.getCatId(), categories);
                    categoryDto.setChildren(CollUtil.isEmpty(childList) ? null : childList);
                    return categoryDto;
                }).sorted(Comparator.comparingInt(CategoryDto::getSort))
                .collect(Collectors.toList());
    }

    // 优化钱
    public List<CategoryEntity> getCategoryByPId(Long categoryId) {
        return categoryDao.selectList(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getParentCid, categoryId));
    }

    // 优化后
    public List<CategoryEntity> getCategoryByPId(List<CategoryEntity> categoryEntities, Long categoryId) {
        return categoryEntities.stream().filter(item -> item.getParentCid().equals(categoryId)).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Category2Vo>> getCategoryListWithCache() {
        String categoryListJson = stringRedisTemplate.opsForValue().get(CacheConstant.CATEGORY_LIST);
        if (StrUtil.isNotEmpty(categoryListJson)) {
            System.out.println("查询缓存");
            return JSONUtil.toBean(categoryListJson, Map.class);
        }
        if ("".equals(categoryListJson)) {
            // 空对象, 直接返回
            return new HashMap<>();
        }

//        return getCategoryListAndSetCacheByLocalLock();
        return getCategoryListAndSetCacheByRedisLock();
    }

    @Override
    @Cacheable(value = "categoryCache", key="#root.method.name")
    public Map<String, List<Category2Vo>> getCategoryListWithSpringCache() {
        System.out.println("查询数据库");
        return getCategoryList();
    }

    private Map<String, List<Category2Vo>> getCategoryListAndSetCacheByRedisLock() {
        String theadId = String.valueOf(Thread.currentThread().getId());
        // 给锁加过期时间，防止线程执行出错导致后续删锁失败，出现死锁现象
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(CacheConstant.CATEGORY_LOCK, theadId, CacheConstant.CATEGORY_LOCK_EXPIRE, TimeUnit.SECONDS);
        if (isLock) {
            System.out.println("加锁");
            Map<String, List<Category2Vo>> categoryListAndSetCache = getCategoryListAndSetCache();

            // 释放锁：先获取锁，再删除锁，不是一个原子操作，可能出现误删情况，详情见笔记《单机版 Redis 分布式锁》实现原理的版本四一章
//            String findThreadId = stringRedisTemplate.opsForValue().get(CacheConstant.CATEGORY_LOCK);
//            if (findThreadId.equals(theadId)) {
//                // 只删除自己线程的锁，防止误删
//                System.out.println("删锁");
//                stringRedisTemplate.delete(CacheConstant.CATEGORY_LOCK); // 删除锁，便于其他线程获取锁
//            }

            // 改用 Lua 脚本实现原子性
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), theadId);

            return categoryListAndSetCache;
        } else {
            // 重试，最多三次
            try {
                Thread.sleep(100);
                String s = stringRedisTemplate.opsForValue().get(CacheConstant.CATEGORY_LOCK_RETRY + theadId);
                Integer count = StrUtil.isEmpty(s) ? CacheConstant.CATEGORY_LOCK_DEFAULT_RETRY : Integer.parseInt(s);
                System.out.println("重试：" + count);
                if (count <= CacheConstant.CATEGORY_LOCK_MAX_RETRY) {
                    return getCategoryListAndSetCacheByRedisLock();
                }
                stringRedisTemplate.opsForValue().set(CacheConstant.CATEGORY_LOCK_RETRY + theadId, String.valueOf(count + 1), CacheConstant.CATEGORY_LOCK_COUNT_EXPIRE, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    /**
     * 加本地锁：防止缓存穿透
     *
     * @return
     */
    private Map<String, List<Category2Vo>> getCategoryListAndSetCacheByLocalLock() {
        synchronized (this) {
            return getCategoryListAndSetCache();
        }
    }

    private Map<String, List<Category2Vo>> getCategoryListAndSetCache() {
        // 再次确认缓存种是否存在
        String categoryListJson = stringRedisTemplate.opsForValue().get(CacheConstant.CATEGORY_LIST);
        if (StrUtil.isNotEmpty(categoryListJson)) {
            System.out.println("查询缓存");
            return JSONUtil.toBean(categoryListJson, Map.class);
        }
        System.out.println("查询数据库");
        Map<String, List<Category2Vo>> categoryList = getCategoryList();
        if (Objects.isNull(categoryList) || categoryList.isEmpty()) {
            // 缓存空对象：防止缓存穿透
            stringRedisTemplate.opsForValue().set(CacheConstant.CATEGORY_LIST, "", CacheConstant.CATEGORY_LIST_EXPIRE, TimeUnit.MINUTES);
            return new HashMap<>();
        }
        stringRedisTemplate.opsForValue().set(CacheConstant.CATEGORY_LIST, JSONUtil.toJsonStr(categoryList), CacheConstant.CATEGORY_LIST_EXPIRE, TimeUnit.MINUTES);
        return categoryList;
    }

    @Override
    public Map<String, List<Category2Vo>> getCategoryList() {
//        // 方案一：查询数据库
//        Map<String,  List<Category2Vo>> resultMap = new HashMap<>();
//        List<CategoryEntity> oneCategory = getCategoryByPId(0L);
//        if (CollUtil.isEmpty(oneCategory)) {
//            return resultMap;
//        }
//
//        for (CategoryEntity item : oneCategory) {
//            List<CategoryEntity> twoCategory = getCategoryByPId(item.getCatId());
//            resultMap.put(item.getCatId().toString(), twoCategory.stream().map(cItem -> {
//                Category2Vo category2Vo = new Category2Vo();
//                category2Vo.setId(cItem.getCatId().toString());
//                category2Vo.setCatalog1Id(cItem.getParentCid().toString());
//                category2Vo.setName(cItem.getName());
//                List<CategoryEntity> threeCategory = getCategoryByPId(cItem.getCatId());
//                if (CollUtil.isNotEmpty(threeCategory)) {
//                    category2Vo.setCatalog3List(threeCategory.stream().map(ccItem -> {
//                        Category2Vo.Category3Vo category3Vo = new Category2Vo.Category3Vo();
//                        category3Vo.setId(ccItem.getCatId().toString());
//                        category3Vo.setName(ccItem.getName());
//                        category3Vo.setCatalog2Id(ccItem.getParentCid().toString());
//                        return category3Vo;
//                    }).collect(Collectors.toList()));
//                }
//                return category2Vo;
//            }).collect(Collectors.toList()));
//        }
//        return resultMap;


        // 方案二：一次性全部查出
        List<CategoryEntity> categoryEntities = categoryDao.selectList(new LambdaQueryWrapper<>());
        Map<String, List<Category2Vo>> resultMap = new HashMap<>();
        List<CategoryEntity> oneCategory = getCategoryByPId(categoryEntities, 0L);
        if (CollUtil.isEmpty(oneCategory)) {
            return resultMap;
        }

        for (CategoryEntity item : oneCategory) {
            List<CategoryEntity> twoCategory = getCategoryByPId(categoryEntities, item.getCatId());
            resultMap.put(item.getCatId().toString(), twoCategory.stream().map(cItem -> {
                Category2Vo category2Vo = new Category2Vo();
                category2Vo.setId(cItem.getCatId().toString());
                category2Vo.setCatalog1Id(cItem.getParentCid().toString());
                category2Vo.setName(cItem.getName());
                List<CategoryEntity> threeCategory = getCategoryByPId(categoryEntities, cItem.getCatId());
                if (CollUtil.isNotEmpty(threeCategory)) {
                    category2Vo.setCatalog3List(threeCategory.stream().map(ccItem -> {
                        Category2Vo.Category3Vo category3Vo = new Category2Vo.Category3Vo();
                        category3Vo.setId(ccItem.getCatId().toString());
                        category3Vo.setName(ccItem.getName());
                        category3Vo.setCatalog2Id(ccItem.getParentCid().toString());
                        return category3Vo;
                    }).collect(Collectors.toList()));
                }
                return category2Vo;
            }).collect(Collectors.toList()));
        }
        return resultMap;
    }

//    @Cacheable(value = "testCache", key="'level1Category'")
    @Cacheable(value = "categoryCache", key="#root.method.name", sync = true)
    @Override
    public List<String> getCache() {
        System.out.println("测试缓存");
        return Arrays.asList("cache1", "cache2");
    }

    @Override
    @CacheEvict(value = "categoryCache", key="'getCategoryListWithSpringCache'")
    public void deleteCategorySpringCache() {

    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categoryCache", key = "'getCategoryListWithSpringCache'"),
            @CacheEvict(value = "categoryCache", key = "'getCache'")
    })
    @CacheEvict(value = "categoryCache", allEntries = true)
    public void testDeleteCacheAll() {

    }

    @Override
    @CachePut(value = "categoryCache", key="'getCache'")
    public List<String> updateCache() {
        return Arrays.asList("cache3", "cache4");
    }
}