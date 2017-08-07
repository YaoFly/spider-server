package dao;

import domain.Compare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ooopic on 2017/7/28.
 */
@Repository
public interface CompareDao {
    public Compare save(Compare compare);
    public List<Compare> findByTargetImgUrl(String url);
}
