package cn.zqf.core.service;

public interface SolrManagerService {
    void saveItemToSolr(Long id);

    void deleteItemFromSolr(Long id);
}
