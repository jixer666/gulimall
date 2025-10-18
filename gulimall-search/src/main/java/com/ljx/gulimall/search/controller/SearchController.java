package com.ljx.gulimall.search.controller;

import com.ljx.gulimall.search.model.dto.SearchParam;
import com.ljx.gulimall.search.model.vo.SearchResult;
import com.ljx.gulimall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listHtml(SearchParam searchParam, Model model) {
        SearchResult searchResult = mallSearchService.searchFromIndex(searchParam);
        model.addAttribute("result", searchResult);

        return "list";
    }


}
