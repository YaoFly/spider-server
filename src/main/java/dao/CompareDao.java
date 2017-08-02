package dao;

import domain.Compare;

import java.util.List;

/**
 * Created by ooopic on 2017/7/28.
 */
public interface CompareDao {
    public Compare save(Compare compare);
    public List<Compare> findByTargetFile(String url);
}
