package com.z.core.meilisearch;

import com.meilisearch.sdk.SearchRequest;
import com.z.core.meilisearch.dao.MeiliSearchMapper;
import com.z.core.meilisearch.json.SearchResult;
import com.z.model.meilisearch.MainDO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class TestService {
    private static final Log log = LogFactory.getLog(TestService.class);

    @Resource
    private MeiliSearchMapper meiliSearchMapper;

    @Scheduled(cron = "*/10 * * * * ?" )
    public void test() {
        log.info("test-->start");

        //根据标签分页查询
//        SearchRequest searchRequest4 = SearchRequest.builder()
//                .limit(12)
//                .sort(new String[]{"createTime:desc"})
//                .offset(0)
////                .filter(new String[]{"tags.id=" + "10010" + " AND status=1 AND isDelete=0"})
//                .build();
//        SearchResult<MainDO> search4 = meiliSearchMapper.search(searchRequest4);
//        log.info("hits====>"+search4);
//        for (HashMap<String, Object> hit : search4.getHits()) {
//            log.info("------------");
//            hit.forEach((k,v)->{
//                log.info("k:"+k+"--->"+v);
//            });
//        }
        //保存Or编辑
        List<MainDO> articleCardDTOS = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            MainDO tmp = MainDO.builder()
                    .id(Long.valueOf(i))
                    .idToString(String.valueOf(i))
                    .seedsName("name" + i)
                    .isDelete(0)
                    .status(1)
                    .createTime(LocalDateTime.now())
                    .date1(new Date())
                    .classFiledId(10010+i).build();
            articleCardDTOS.add(tmp);
        }
        Boolean aBoolean = meiliSearchMapper.add(articleCardDTOS) > 0 ? Boolean.TRUE : Boolean.FALSE;
//按id删除
//        meiliSearchMapper.delete(String.valueOf(1));

//        meiliSearchMapper.deleteAll();

        //根据类目分页查询
        SearchRequest searchRequest3 = SearchRequest.builder()
                .limit(200)
                .offset(0)
                .build();
        StringBuffer sb1 = new StringBuffer();
        sb1.append("status =1 AND isDelete=0").append(" AND ").append("classFiledId =").append(10010);
        searchRequest3.setFilter(new String[]{sb1.toString()});
        searchRequest3.setSort(new String[]{"createTime:desc"});
        SearchResult<MainDO> search3 = meiliSearchMapper.search(searchRequest3);
        log.info("hits====>"+search3);
        for (HashMap<String, Object> hit : search3.getHits()) {
            log.info("------------");
            hit.forEach((k,v)->{
                log.info("k:"+k+"--->"+v);
            });
        }
        log.info("test-->end");
    }


}
