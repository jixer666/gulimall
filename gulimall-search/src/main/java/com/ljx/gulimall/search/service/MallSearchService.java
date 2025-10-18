package com.ljx.gulimall.search.service;

import com.ljx.gulimall.search.model.dto.SearchParam;
import com.ljx.gulimall.search.model.vo.SearchResult;


public interface MallSearchService {
    SearchResult searchFromIndex(SearchParam searchParam);
}
