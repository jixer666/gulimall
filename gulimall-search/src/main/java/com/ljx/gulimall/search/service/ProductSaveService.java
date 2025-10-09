package com.ljx.gulimall.search.service;

import com.ljx.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    Boolean produceStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
