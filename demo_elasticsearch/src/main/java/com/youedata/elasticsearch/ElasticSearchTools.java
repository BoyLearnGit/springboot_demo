package com.youedata.elasticsearch;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.stereotype.Component;

import static com.youedata.elasticsearch.StringUtils.*;

/**
 * @author whx 下午6:52:53
 * @desc elasticsearch的java工具类
 */
@Configuration
@Component
public class ElasticSearchTools {
    
    @Autowired
    private  ElasticSearchPropertie properties;
    
    private ThreadLocal<TransportClient> transportClientLocal = new ThreadLocal<TransportClient>();
    
     
    
    private static TransportClient getInstance() throws UnknownHostException {

            Settings settings = Settings.builder()
                    .put("cluster.name", "my-application").put("client.transport.sniff", true).build();
            TransportClient transportClient = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.10.173"), 9300));
            return transportClient;
   }
    
    
    /** * 给索引增加mapping。 * @param index 索引名 * @param type mapping所对应的type */  
    public void addMapping(String index, String type) {  
        try {  
            TransportClient transportClient = getInstance();
            
            XContentBuilder builder=XContentFactory.jsonBuilder()  
                        .startObject()//注意不要加index和type  
                            .startObject("properties")  
                            .startObject("value").field("type", "string").field("store", "yes").field("analyzer", Const.ES_ANALYZER_KEYWORD).endObject()
                            .endObject()  
                        .endObject(); 
           
            System.out.println(builder.string());  
            PutMappingRequest mappingRequest = Requests.putMappingRequest(index).source(builder).type(type);  
            transportClient.admin().indices().putMapping(mappingRequest).actionGet();  
        } catch (ElasticsearchException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  

    /**
     *@Title: addIndex 
     *@Description: TODO  单个索引增加
     *@param @param object  要增加的数据
     *@param @param index   索引，类似数据库
     *@param @param type    类型，类似表
     *@param @param id      id  
     *@return void
     *@throws
     */
    
    public static boolean addDocument(JSONObject object, String index, String type, String id) {
        try {
            TransportClient transportClient = getInstance();
//        	Map map=new HashMap();
//        	map.put("index.analysis.analyzer.default.type", "keyword");
            IndexResponse response = transportClient.prepareIndex(index, type, id).setSource(object).get();
            boolean created = response.getResult() != null;
			System.out.println("创建一条记录:" + response.getResult().name());
			//client.close();
			return created;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public <T> boolean addDocument(T object, String index, String type, String id) {
        try {
            TransportClient transportClient = getInstance();
//          Map map=new HashMap();
//          map.put("index.analysis.analyzer.default.type", "keyword");
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());
            IndexResponse response = transportClient.prepareIndex(index, type, id).setSource(JSONObject.fromObject(object, jsonConfig)).get();
            boolean created = response.getResult() != null;
            System.out.println("创建一条记录:" + response.getResult().name());
            //client.close();
            return created;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     *@Title: updateDocument 
     *@Description: TODO 更新某条信息 ,如果改动很多，直接用新增的也可以，只要id相同即可
     *@param @param index
     *@param @param type
     *@param @param id
     *@return void
     *@throws
     */
    public void updateDocument(String index, String type, String id, String jsondata) {
        try {
            TransportClient transportClient = getInstance();
            UpdateRequest  updateRequest = new UpdateRequest(index, type, id);
            updateRequest.doc();
            updateRequest.script(new Script(jsondata));
            transportClient.update(updateRequest).get();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 

    }
    
    public static <T> void addDocuments4Bulk(List<T> jsonlist, String index, String type) {
        long bulkBuilderLength = 0;
        try {
            TransportClient transportClient = getInstance();
            BulkRequestBuilder bulkBuilder = transportClient.prepareBulk();
            for (Object object: jsonlist) {
                JsonConfig jsonConfig = new JsonConfig();
                jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());
                IndexRequestBuilder indexRequestBuilder = transportClient.prepareIndex(index, type, randomUUID())
                        .setSource(JSONObject.fromObject(object, jsonConfig));
                bulkBuilder.add(indexRequestBuilder);
                bulkBuilderLength++;
                if(bulkBuilderLength % 1000== 0){
                      System.out.println("bulkBuilderLength:"+bulkBuilderLength);
                      BulkResponse bulkRes =bulkBuilder.execute().actionGet();
                      if(bulkRes.hasFailures()){
                         System.out.println("##### Bulk Request failure with error: " + bulkRes.buildFailureMessage());
                      }
                      bulkBuilder = transportClient.prepareBulk();
                }
                
            }
            if(bulkBuilder.numberOfActions() > 0){ 
                BulkResponse bulkRes = bulkBuilder.execute().actionGet();
                if(bulkRes.hasFailures()){
                      System.out.println("##### Bulk Request failure with error: " +   bulkRes.buildFailureMessage());
                }
                 bulkBuilder = transportClient.prepareBulk();
            }
          
           
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    public void deleteByQuery(Map<String, String> queryMap, String index, String type) {  
        try {
            TransportClient transportClient = getInstance();
            BulkRequestBuilder bulkRequest = transportClient.prepareBulk();  
            
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {  
                BoolQueryBuilder bb = QueryBuilders.boolQuery();
                boolQueryBuilder = bb.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
            }  
            
            SearchResponse response = transportClient.prepareSearch(index).setTypes(type)  
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)  
                    .setQuery(boolQueryBuilder)  
                    .setExplain(true).execute().actionGet();  
            
            
            for(SearchHit hit : response.getHits()){  
                String id = hit.getId();  
                bulkRequest.add(transportClient.prepareDelete(index, type, id).request());  
            }  
            BulkResponse bulkResponse = bulkRequest.get();  
            if (bulkResponse.hasFailures()) {  
                for(BulkItemResponse item : bulkResponse.getItems()){  
                    System.out.println(item.getFailureMessage());  
                }  
            }else {  
                System.out.println("delete ok");  
            } 
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
         
    }  
    
    
    public <T> void updateByQuery(Map<String, String> queryMap, T object, String index, String type) { 
        try {
            TransportClient transportClient = getInstance();
            BulkRequestBuilder bulkRequest = transportClient.prepareBulk();  
            UpdateRequestBuilder updateRequestBuilder = transportClient.prepareUpdate();
            JsonConfig jsonConfig = new JsonConfig();
            jsonConfig.registerJsonValueProcessor(Date.class , new JsonDateValueProcessor());
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {  
                BoolQueryBuilder bb = QueryBuilders.boolQuery();
                boolQueryBuilder = bb.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
            }  
            
            SearchResponse response = transportClient.prepareSearch(index).setTypes(type)  
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)  
                    .setQuery(boolQueryBuilder)  
                    .setExplain(true).execute().actionGet();  
            
            
            for(SearchHit hit : response.getHits()){  
                String id = hit.getId();  
                UpdateRequest  updateRequest = new UpdateRequest(index, type, id);
                updateRequest.doc(JSONObject.fromObject(object, jsonConfig));
                transportClient.update(updateRequest).get();
            }  
            
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        
        
    }
    
    /**
     * 模糊匹配查询
     * @param index
     * @param type
     * @param queryMap
     * @param fields
     * @return
     * @throws UnknownHostException 
     */
    public Page<Map<String, Object>> queryDocuments(String index, String type,Map<String, String> queryMap, 
                                                    Map<String,String> sortMaps, String[] fields, int pageNum,int pageSize) 
                                                    throws UnknownHostException {
        
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        Page<Map<String, Object>> page= new Page<Map<String, Object>>();
        TransportClient transportClient = getInstance();
      //** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **//*
        //构造全文或关系的查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        if (queryMap != null) {
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {  
                if (entry.getKey().equals("goodsCategory")) {
                    boolQueryBuilder.must(QueryBuilders.wildcardQuery(entry.getKey(), entry.getValue()));
                } else if (entry.getKey().equals("goodsAppCategory")) {
                    if (!"".equals(entry.getValue())) {
                        boolQueryBuilder.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
                    }
                } else if (entry.getKey().equals("goodsType")) {
                    if (!"0".equals(entry.getValue())) {
                        boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
                    }
                }
                else {
                    boolQueryBuilder.must(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
                }
            } 
        }
        //构建排序条件
        SortBuilder sortBuilder = null;
        if (sortMaps != null) {
            for (Object key : sortMaps.keySet()) {
                sortBuilder = SortBuilders.fieldSort((String) key).order(trim((String) sortMaps.get(key)).equals("ASC") ? SortOrder.ASC : SortOrder.DESC);
            }
        } 
        SearchResponse response;
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder);
        
        if (sortBuilder != null) {
            searchRequestBuilder = searchRequestBuilder.addSort(sortBuilder);
        }
        
        if (fields != null) {
            searchRequestBuilder = searchRequestBuilder.setFetchSource(fields, null);
        }
        
        searchRequestBuilder = searchRequestBuilder.setFrom((pageNum-1)*pageSize).setSize(pageSize);//from 开始的个数  
        
        response = searchRequestBuilder.setExplain(true).execute().actionGet();

      //取值
        long total = response.getHits().getTotalHits();
        page.setCurrentPage(pageNum);
        page.setPageSize(pageSize);
        page.setTotalRecord((int) total);
        
        if(total==0){
            return null;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
            for(SearchHit searchHit:searchHits){
                lists.add(searchHit.getSourceAsMap());
            }
            page.setList(lists);
            return page;
        }
    }
    
    
    public long queryCountByKeyword(String index, String type,String keyword, Map<String, String> queryMap) throws UnknownHostException {
        TransportClient transportClient = getInstance();
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        Page<Map<String, Object>> page= new Page<Map<String, Object>>();
        SearchResponse response;
        
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        QueryBuilder queryBuilder = null;
        if (keyword != null && !keyword.equals("")) {
            queryBuilder = QueryBuilders.multiMatchQuery(keyword, "goodsName", "goodsId", "videoTheme" , "goodsContent");  
        }
        
        if (queryMap != null) {
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {  
                if (entry.getKey().equals("goodsCategory")) {
                    boolQueryBuilder.must(QueryBuilders.wildcardQuery(entry.getKey(), entry.getValue()));
                } 
                else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
                
            } 
        }
        
        if (queryBuilder != null) {
            boolQueryBuilder.must(queryBuilder);
        }
        
        
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setQuery(boolQueryBuilder);
        
        if (type != null) {
            searchRequestBuilder = searchRequestBuilder.setTypes(type);
        }
        
  
        response = searchRequestBuilder.setExplain(true).execute().actionGet();
        
        return response.getHits().getTotalHits();
 
    }
    
    public Page<Map<String, Object>> queryDocumentsByKeyword(String index, String type, String keyword, Map<String, String> queryMap,
                                                            Map<String,String> sortMaps, String[] fields,
                                                            int pageNum,int pageSize) 
                                                                    throws UnknownHostException  {  
        
        TransportClient transportClient = getInstance();
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        Page<Map<String, Object>> page= new Page<Map<String, Object>>();
        SearchResponse response;
        
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        
        QueryBuilder queryBuilder = null;
        if (keyword != null && !keyword.equals("")) {
            queryBuilder = QueryBuilders.multiMatchQuery(keyword, "goodsName", "goodsId", "videoTheme" , "goodsContent");  
        }
        
        if (queryMap != null) {
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {  
                if (entry.getKey().equals("goodsCategory")) {
                    boolQueryBuilder.must(QueryBuilders.wildcardQuery(entry.getKey(), entry.getValue()));
                } 
                else {
                    boolQueryBuilder.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                }
                
            } 
        }
        
        if (queryBuilder != null) {
            boolQueryBuilder.must(queryBuilder);
        }
        
      //构建排序条件
        SortBuilder sortBuilder = null;
        if (sortMaps != null) {
            for (Object key : sortMaps.keySet()) {
                sortBuilder = SortBuilders.fieldSort((String) key).order(trim((String) sortMaps.get(key)).equals("ASC") ? SortOrder.ASC : SortOrder.DESC);
            }
        } 
        
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setQuery(boolQueryBuilder);
        
        if (type != null) {
            searchRequestBuilder = searchRequestBuilder.setTypes(type);
        }
        
        if (sortBuilder != null) {
            searchRequestBuilder = searchRequestBuilder.addSort(sortBuilder);
        }
        
        if (fields != null) {
            searchRequestBuilder = searchRequestBuilder.setFetchSource(fields, null);
        }
        searchRequestBuilder = searchRequestBuilder.setFrom((pageNum-1)*pageSize).setSize(pageSize);
        
  
        response = searchRequestBuilder.setExplain(true).execute().actionGet();
        
        long total = response.getHits().getTotalHits();
        page.setCurrentPage(pageNum);
        page.setPageSize(pageSize);
        page.setTotalRecord((int) total);
        if(total==0){
            return null;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
            for(SearchHit searchHit:searchHits){
                lists.add(searchHit.getSourceAsMap());
            }
            page.setList(lists);
            return page;
        }
    }  

    /**
     *@Title: addIndex 
     *@Description: TODO  单个索引增加
     *@param @param object  要增加的数据
     *@param @param index   索引，类似数据库
     *@param @param type    类型，类似表
     *@param @param id      id  
     *@return void
     *@throws
     */
    
    public static boolean addDocumentByBuilder(XContentBuilder builder, String index, String type) {
        try {
            TransportClient transportClient = getInstance();
            CreateIndexResponse response = transportClient.admin().indices().prepareCreate(index).execute().actionGet();
            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
            transportClient.admin().indices().putMapping(mapping).actionGet();
            System.out.println("创建索引成功！");
            //client.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     *@Title: addIndex 
     *@Description: TODO  单个索引增加
     *@param @param object  要增加的数据
     *@param @param index   索引，类似数据库
     *@param @param type    类型，类似表
     *@param @param id      id  
     *@return void
     *@throws
     */
    
    public static boolean addDocumentByAnalyzer(String analyzer, String index) {
        try {
            TransportClient transportClient = getInstance();
            CreateIndexResponse response = transportClient.admin().indices().prepareCreate(index)
                    .setSettings(Settings.builder().put("index.analysis.analyzer.default.type",analyzer))
                    .execute().actionGet();
//            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);
            
//          client.admin().indices().analyze(new AnalyzeRequest(index).analyzer(analyzer)).actionGet();
            System.out.println("创建索引成功！");
            //client.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean addDocument(String index) {
        try {
            TransportClient transportClient = getInstance();
            XContentBuilder settingsBuilder = XContentFactory.jsonBuilder()  
                    .startObject()  
                    .startObject("analysis")  
                    .startObject("analyzer")  
                    .startObject("SeparatorAnalyzer")  
                    .field("type", "pattern")  
                    .field("pattern", ",") 
                    .endObject()  
                    .endObject()  
                    .endObject()  
                    .endObject(); 
            
            CreateIndexResponse response = transportClient.admin().indices().prepareCreate(index).setSettings(settingsBuilder)
                    .execute().actionGet();
            
            System.out.println("创建索引"+response.isAcknowledged());
            //client.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *@Title: getIndex 
     *@Description: TODO 获取某条信息
     *@param @param index
     *@param @param type
     *@param @param id
     *@param @return
     *@return Map<String,Object>
     *@throws
     */
    public Map<String, String> getDocument(String index, String type, String id,String[] fields) {
        try {
            //es 5.1.1写法
            TransportClient transportClient = getInstance();
            GetResponse response = transportClient.prepareGet(index, type, id).setFetchSource(fields, null).get();   
            Map map = response.getSource();
            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    
    /**
     *@Title: getIndex 
     *@Description: TODO 获取某条信息
     *@param @param index
     *@param @param type
     *@param @param id
     *@param @return
     *@return Map<String,Object>
     *@throws
     */
    public Map<String, String> getDocument(String index, String type, String id) {
        try {
            //es 5.1.1写法
            TransportClient transportClient = getInstance();
            GetResponse response = transportClient.prepareGet(index, type, id).get();    
            Map map = response.getSource();
            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    

    /**
     *@Title: delDocument 
     *@Description: TODO 删除某条信息
     *@param @param index
     *@param @param type
     *@param @param id
     *@return void
     *@throws
     */
    public boolean delDocument(String index, String type, String id) {
        try {
            TransportClient transportClient = getInstance();     
            DeleteResponse response = transportClient.prepareDelete(index, type, id).get();
            boolean founded = true;
            System.out.println("删除一条记录:" + founded);
            //client.close();
            return founded;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       return false;
    }
    
    /**
     *@Title: delDocument 
     *@Description: TODO 更新某条信息 ,如果改动很多，直接用新增的也可以，只要id相同即可
     *@param @param index
     *@param @param type
     *@param @param id
     *@return void
     *@throws
     */
    public void batchDelDocument(String index, String type,List lists) {
        try {
            
           TransportClient transportClient = getInstance(); 
           for(int i=0;i<lists.size();i++){
                String id = lists.get(i).toString();
                DeleteResponse response = transportClient.prepareDelete(index, type, id).get();
                boolean founded = true;//response.isFound();
                System.out.println("删除一条记录:" + founded);
           }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
   
    /** 
     * 判断一个index中的type是否有数据 
     * @param index 
     * @param type 
     * @return 
     * @throws Exception 
     */  
    public Boolean existDocOfType(String index, String type) throws Exception {
        
        TransportClient transportClient = getInstance(); 
        SearchRequestBuilder builder = transportClient.prepareSearch(index).setTypes(type)  
                .setSearchType(SearchType.QUERY_THEN_FETCH)  
                .setSize(1);  
        SearchResponse response = builder.execute().actionGet();  
        long docNum = response.getHits().getTotalHits();  
        if (docNum == 0) {  
            return false;  
        }  
        return true;  
    }
    
   

    /* 
    * 删除索引 
    * */  
    public boolean deleteIndex(String indexName) {  
        try {
            
            TransportClient transportClient = getInstance();
            IndicesExistsResponse indicesExistsResponse = transportClient.admin().indices().prepareExists(indexName).execute().actionGet();  
            if (indicesExistsResponse.isExists()) {  
                return transportClient.admin().indices().prepareDelete(indexName).execute().actionGet().isAcknowledged();  
            }else {  
                return true;  
            }  
        }catch (Exception e) {  
            e.getStackTrace();
        }finally{
            //search//client.close();
        }
         
        return false;  
    }
    
       
    /**
     *@Title: delDocument 
     *@Description: TODO 更新某条信息 ,如果改动很多，直接用新增的也可以，只要id相同即可
     *@param @param index
     *@param @param type
     *@param @param id
     *@return void
     *@throws
     */
    public void batchUpdateDocument(String index, String type, String id, Map<String,Object> map) {
        try {
            TransportClient transportClient = getInstance();
            UpdateRequest updateRequest = new UpdateRequest(index, type, id);
           for(Map.Entry<String, Object> entry : map.entrySet()){
            updateRequest.doc(XContentFactory.jsonBuilder().startObject().field(entry.getKey(), entry.getValue()).endObject());
            transportClient.update(updateRequest).get();
           }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     *@Title: getDocuments 
     *@Description: TODO 通过多个id，去查询一个list,暂时没有太大的用处 ，需要的话 请自己修改入参 调整
     *@param @param index
     *@param @param type
     *@param @param id
     *@param @return
     *@return List<Map<String,Object>>
     *@throws
     */
    public List<Map<String, Object>> getDocuments(String index, String type, String id1, String id2) {
        try {
            TransportClient transportClient = getInstance();
            //client.prepareMultiGet().add("twitter", "tweet", "1").add("twitter", "tweet", "2", "3", "4").add("another", "type", "foo").get();
            MultiGetResponse multiGetItemResponses = transportClient.prepareMultiGet().add(index, type, id1, id2).get();
            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
            for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists()) {
                    lists.add(response.getSource());
                }
            }
            return lists;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    

    /**
     *@Title: addDocuments  批量新增记录  注意 下面有个map.get(id) 也就是物理表的id
     *@Description: TODO
     *@param @param list
     *@param @param index
     *@param @param type
     *@return void
     *@throws
     */
    public void addDocuments(List<Map<Object, Object>> list, String index, String type) {
        try {
            TransportClient transportClient = getInstance();

            BulkRequestBuilder bulkRequest = transportClient.prepareBulk();

            for (Map<Object, Object> map : list) {
                //遍历map所有field,构造插入对象
                XContentBuilder xb = XContentFactory.jsonBuilder().startObject();
                for (Object key : map.keySet()) {
                    xb.field((String) key, map.get(key));
                }
                xb.endObject();
                //id尽量为物理表的主键
                bulkRequest.add(transportClient.prepareIndex(index, type, trim((String) map.get("id"))).setSource(xb));
            }
            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                System.err.println("");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            //client.close();
        }

    }
    
    
    public long queryCount(String index,String type,Map<Object, Object> queryMaps){
        
        try {

            TransportClient transportClient = getInstance();
            //构造精确的并且查询
            BoolQueryBuilder bb1 = QueryBuilders.boolQuery();
            if (queryMaps != null) {
                for (Object key : queryMaps.keySet()) {
                    bb1 = bb1.must(QueryBuilders.termQuery((String) key, queryMaps.get(key)));
                }
            }
            IndicesExistsResponse indicesExistsResponse = transportClient.admin().indices().prepareExists(index).execute().actionGet();  
            if (indicesExistsResponse.isExists()) {  
                //client = TransportClient.builder().build();
                SearchResponse searchResponse = transportClient.prepareSearch(index).setTypes(type)
                         .setQuery(bb1)
                         .execute()
                         .actionGet();
                return searchResponse.getHits().getTotalHits();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            //client.close();
        }
        return 0;
    }
    
    public long queryCount(String index,String type){
            try {
                TransportClient transportClient = getInstance();
                IndicesExistsResponse indicesExistsResponse = transportClient.admin().indices().prepareExists(index).execute().actionGet();  
                if (indicesExistsResponse.isExists()) {  
                    SearchResponse searchResponse = transportClient.prepareSearch(index).setTypes(type)
                             .execute()
                             .actionGet();
                    return searchResponse.getHits().getTotalHits();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally{
                //client.close();
            }
            return 0;
     }
    
    public long queryCount(String index){
        try {
            TransportClient transportClient = getInstance();
            IndicesExistsResponse indicesExistsResponse = transportClient.admin().indices().prepareExists(index).execute().actionGet();  
            if (indicesExistsResponse.isExists()) {  
                SearchResponse searchResponse = transportClient.prepareSearch(index)
                         .execute()
                         .actionGet();
                return searchResponse.getHits().getTotalHits();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            //client.close();
        }
        return 0;
 }
    
    
    
    public List<Map<String, Object>> queryDocuments(String index, String type,int from, int size,List<Map<Object, Object>> rangeLists,String logicOperator,List<Map<Object, Object>> queryLists, Map<Object, Object> sortMaps,String[] fields) throws Exception{
        TransportClient transportClient = getInstance();
        //** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **//*
        //构造全文或关系的查询
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
         BoolQueryBuilder bb1 = QueryBuilders.boolQuery();
         if(logicOperator!=""){
             if (queryLists != null && queryLists.size() > 0) {
                    //构造精确的并且查询
                    for (Map<Object, Object> map : queryLists) {
                        if (map != null && (!map.isEmpty())) {
                            String comparisonOperator = map.get("comparisonOperator").toString();
                          //构造全文或关系的查询
                            BoolQueryBuilder bb = QueryBuilders.boolQuery();
                            if(comparisonOperator.equals(Const.OPERATOR_EQUAL)){
                                bb = bb.must(QueryBuilders.termQuery((String) map.get("accessPoint"), map.get("value")));
                                //bb = bb.must(QueryBuilders.matchPhraseQuery((String) map.get("accessPoint"), map.get("value")));
                                //bb = bb.must(QueryBuilders.matchQuery((String) map.get("accessPoint"), map.get("value")));
                            }else if(comparisonOperator.equals(Const.OPERATOR_LESSANDEQUALTHAN)){
                                
                            }else if(comparisonOperator.equals(Const.OPERATOR_LIKE)){
                                bb = bb.must(QueryBuilders.wildcardQuery((String)map.get("accessPoint"), "*"+(String)map.get("value")+"*"));
                            }
                            
                            if(logicOperator.equals(Const.OPERATOR_AND)){
                                bb1 = bb1.must(bb);
                            }else if(logicOperator.equals(Const.OPERATOR_OR)){}{
                                bb1 = bb1.should(bb);
                            }
                            
                        } 
                    }
                }
         }
        //构造范围查询参数
        QueryBuilder qb = null;
        if (rangeLists != null && rangeLists.size() > 0) {

            for (Map<Object, Object> map : rangeLists) {
                if (map != null && (!map.isEmpty())) {
                    qb = QueryBuilders.rangeQuery(trim((String) map.get("field"))).from(map.get("from")).to(map.get("to"));
                } 
            }
        }
        
      //构造排序参数
        SortBuilder sortBuilder = null;
        if (sortMaps != null) {
            for (Object key : sortMaps.keySet()) {
                sortBuilder = SortBuilders.fieldSort((String) key).order(trim((String) sortMaps.get(key)).equals("ASC") ? SortOrder.ASC : SortOrder.DESC);
            }
        }

        //构造查询
        SearchResponse response = null;
        if(sortBuilder==null){
            if(size>10000){
                response = transportClient.prepareSearch(index)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(bb1).setFetchSource(fields, null)
                        .setScroll(new TimeValue(20000))
                        .setSize(size).execute().actionGet();
            }if(size==-1){
                response = transportClient.prepareSearch(index)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(bb1) // Query
                        .setPostFilter(qb)
                        .setScroll(new TimeValue(20000))
                        .setSize(10000)
                        .setExplain(true).setFetchSource(fields, null)
                        .execute()
                        .actionGet();
            }else{
                response = transportClient.prepareSearch(index)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setFrom(from).setSize(size)
                    .setQuery(bb1) // Query
                    .setPostFilter(qb)
                    .setExplain(true).setFetchSource(fields, null)
                    .execute()
                    .actionGet();
            }
        }else{
            if(size>10000){
                    // 设置Scroll参数,执行查询并返回结果 
                    response = transportClient.prepareSearch(index)
                            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                            .setQuery(bb1)
                            .setScroll(new TimeValue(20000)).setFetchSource(fields, null)
                            .setSize(size).execute().actionGet();
            }if(size==-1){
                response = transportClient.prepareSearch(index)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(bb1) // Query
                        .setPostFilter(qb)
                        .addSort(sortBuilder)
                        .setScroll(new TimeValue(20000))
                        .setSize(10000)
                        .setFetchSource(fields, null)                       
                        .setExplain(true)
                        .execute()
                        .actionGet();
            }else{
                response = transportClient.prepareSearch(index)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setFrom(from).setSize(size)
                        .setQuery(bb1) // Query
                        .setPostFilter(qb)
                        .addSort(sortBuilder).setFetchSource(fields, null)
                        .setExplain(true)
                        .execute()
                        .actionGet();
           }
        }
        
        
        long total = response.getHits().getTotalHits();
        if(total==0){
            //client.close();
            return lists;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
           
            for(SearchHit searchHit:searchHits){
                Map<String, Object> map = new HashMap();
                map.put("esdocid", searchHit.getId());
                map.put("data", searchHit.getSourceAsString());
                lists.add(map);
            }
            System.out.println("数组个数："+lists.size());
            //client.close();
            return lists;
        }
    }
    
    
    public List<Map<String, Object>> queryDocuments(String index, String type,List<Map<Object, Object>> rangeLists, Map<Object, Object> queryMaps, Map<Object, Object> fullTextQueryMaps, Map<Object, Object> sortMaps) throws Exception{

             
        TransportClient transportClient = getInstance();
        //** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **//*
        //构造全文或关系的查询
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        BoolQueryBuilder bb = QueryBuilders.boolQuery();
        if (fullTextQueryMaps != null) {
            for (Object key : fullTextQueryMaps.keySet()) {
                bb = bb.should(QueryBuilders.matchQuery((String) key, fullTextQueryMaps.get(key)));
            }
        }

        //构造精确的并且查询
        BoolQueryBuilder bb1 = QueryBuilders.boolQuery();
        if (queryMaps != null) {
            bb1 = bb1.must(bb);
            for (Object key : queryMaps.keySet()) {
                bb1 = bb1.must(QueryBuilders.termQuery((String) key, queryMaps.get(key)));
            }
        }
        //构造范围查询参数
        QueryBuilder qb = null;
        if (rangeLists != null && rangeLists.size() > 0) {

            for (Map<Object, Object> map : rangeLists) {
                if (map != null && (!map.isEmpty())) {
                    qb = QueryBuilders.rangeQuery(trim((String) map.get("field"))).from(map.get("from")).to(map.get("to"));
                } 
            }
        }
        
      //构造排序参数
        SortBuilder sortBuilder = null;
        if (sortMaps != null) {
            for (Object key : sortMaps.keySet()) {
                sortBuilder = SortBuilders.fieldSort((String) key).order(trim((String) sortMaps.get(key)).equals("ASC") ? SortOrder.ASC : SortOrder.DESC);
            }
        }

        //构造查询
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(bb) // Query
                .setSize(1000)
                .setPostFilter(qb)
                .addSort(sortBuilder).setExplain(true);; // Filter
        //查询
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        //forSearchResponse(response);
        //System.out.println("总共查询到有：" + response.getHits().getTotalHits());
        long total = response.getHits().getTotalHits();
        if(total==0){
            ////client.close();
            return lists;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
            for(SearchHit searchHit:searchHits){
                lists.add(searchHit.getSourceAsMap());
            }
           // //client.close();
            return lists;
        }
    }
    public List<Map<String, Object>> queryDocuments(String index, String type, Map<Object, Object> queryMaps) throws Exception{
        TransportClient transportClient = getInstance();
        /** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **/
  
         List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();

        //构造精确的并且查询
        BoolQueryBuilder bb1 = QueryBuilders.boolQuery();
        if (queryMaps != null) {
           
            for (Object key : queryMaps.keySet()) {
                bb1 = bb1.must(QueryBuilders.termQuery((String) key, queryMaps.get(key)));
            }
        }
    


        //构造查询
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom(0).setSize(50)
                .setQuery(bb1)// Query
                .setExplain(true); // Filter
        //查询
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        //forSearchResponse(response);
        //System.out.println("总共查询到有：" + response.getHits().getTotalHits());
        long total = response.getHits().getTotalHits();
        
        if(total==0){
            //client.close();
            return null;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
          
            for(SearchHit searchHit:searchHits){
                 lists.add(searchHit.getSourceAsMap());
            }
            
            //client.close();
            return lists;
        }
    }
    public List<Map<String, Object>> queryDocumentsByRange(String index, String type, List<Map<Object, Object>> rangeLists) throws Exception{
        TransportClient transportClient = getInstance();
        /** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **/
        
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
         //构造范围查询参数
        QueryBuilder qb = null;
        if (rangeLists != null && rangeLists.size() > 0) {

            for (Map<Object, Object> map : rangeLists) {
                if (map != null && (!map.isEmpty())) {
                    qb = QueryBuilders.rangeQuery(trim((String) map.get("field"))).from(trim((String) map.get("from"))).to(trim((String) map.get("to")));
                }
            }
        }
        //构造查询
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setPostFilter(qb) // Query
                .setExplain(true); // Filter
        //查询
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        //forSearchResponse(response);
        //System.out.println("总共查询到有：" + response.getHits().getTotalHits());
        long total = response.getHits().getTotalHits();
        //client.close();
        if(total==0){
            return null;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
            
            for(SearchHit searchHit:searchHits){
                lists.add(searchHit.getSourceAsMap());
            }
            
            
            return lists;
        }
    }

    public String queryDocuments(String index){
        try {
            TransportClient transportClient = getInstance();
            //SearchResponse searchResponse = client.prepareSearch().get();                 // Query;
            SearchResponse response = transportClient.prepareSearch(index)//可以同时搜索多个索引prepareSearch("index","index2")               // Query
                     .execute()
                     .actionGet();
            //forSearchResponse(response);
            //System.out.println("总共查询到有：" + response.getHits().getTotalHits());
            long total = response.getHits().getTotalHits();
           
            if(total==0){
                //client.close();
                return "";
            }else{
                SearchHit[] searchHits = response.getHits().getHits();
                JSONArray jsonArray = new JSONArray();
                for(SearchHit searchHit:searchHits){
                    //System.out.println("查询结果:"+searchHit.getSourceAsString());
                    jsonArray.add(searchHit.getSourceAsString());
                }
                //client.close();
                return jsonArray.toString();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
        
    }
    

    public void forSearchResponse(SearchResponse response) {
        System.out.println("总共查询到有：" + response.getHits().getTotalHits());
        for (SearchHit hit1 : response.getHits()) {
            Map<String, Object> source1 = hit1.getSourceAsMap();
            if (!source1.isEmpty()) {
                for (Iterator<Map.Entry<String, Object>> it = source1.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    System.out.println(entry.getKey() + "=======" + entry.getValue());
                }
            }
        }
    }
    

    /**
     *@Title: queryDocuments 
     *@Description: TODO
     *@param @param index  相当于库
     *@param @param type   想当于表
     *@param @param from 记录从哪开始
     *@param @param size 数量
     *@param @param rangeLists  范围 参数比如价格   key为   field,from,to
     *@param @param queryMaps  精确查询参数
     *@param @param sortMaps  排序参数  key为   field  value传大写的 ASC , DESC
     * *@param @param fields  要高亮的字段
     *@param @return
     *@return List<Map<String,Object>>
     *@throws
     */
    public List<Map<String, Object>> queryDocuments(String index, String type, int from, int size, List<Map<Object, Object>> rangeLists, Map<Object, Object> queryMaps, Map<Object, Object> sortMaps, List<String> fields, Map<Object, Object> fullTextQueryMaps) {
        try {
  
            TransportClient transportClient = getInstance();

            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
            /** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **/
            //构造全文或关系的查询
            BoolQueryBuilder bb = QueryBuilders.boolQuery();
            if (fullTextQueryMaps != null) {
                for (Object key : fullTextQueryMaps.keySet()) {
                    bb = bb.should(QueryBuilders.matchQuery((String) key, fullTextQueryMaps.get(key)));
                }
            }

            //构造精确的并且查询
            BoolQueryBuilder bb1 = QueryBuilders.boolQuery();
            if (queryMaps != null) {
                bb1 = bb1.must(bb);
                for (Object key : queryMaps.keySet()) {
                    bb1 = bb1.must(QueryBuilders.termQuery((String) key, queryMaps.get(key)));
                }
            }
            /** 上面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **/
            //match全文检索，但是并且的关系， 或者的关系要用
            /*MatchQueryBuilder tq = null;
            if (queryMaps != null) {
                for (Object key : queryMaps.keySet()) {
                    tq = QueryBuilders.matchQuery((String) key, queryMaps.get(key));
                }
            }*/

            //term是代表完全匹配，即不进行分词器分析，文档中必须包含整个搜索的词汇
            /*  TermQueryBuilder tq = null;
                if (queryMaps != null) {
                    for (Object key : queryMaps.keySet()) {
                        tq = QueryBuilders.termQuery((String) key, queryMaps.get(key));
                    }
                }*/

            //构造范围查询参数
            QueryBuilder qb = null;
            if (rangeLists != null && rangeLists.size() > 0) {

                for (Map<Object, Object> map : rangeLists) {
                    if (map != null && (!map.isEmpty())) {
                        qb = QueryBuilders.rangeQuery(trim((String) map.get("field"))).from(trim((String) map.get("from"))).to(trim((String) map.get("to")));
                    }
                }
            }
            //构造排序参数
            SortBuilder sortBuilder = null;
            if (sortMaps != null) {
                for (Object key : sortMaps.keySet()) {
                    sortBuilder = SortBuilders.fieldSort((String) key).order(trim((String) sortMaps.get(key)).equals("ASC") ? SortOrder.ASC : SortOrder.DESC);
                }
            }

            //构造查询
            SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index).setTypes(type).setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(bb1) // Query
                    .setPostFilter(qb) // Filter
                    .setFrom(from).setSize(size).addSort(sortBuilder).setExplain(true);
            //构造高亮字段
            if (fields != null && fields.size() > 0) {
                for (String field : fields) {
                    //searchRequestBuilder.addHighlightedField(field);
                }
               // searchRequestBuilder.setHighlighterEncoder("UTF-8").setHighlighterPreTags("<span style=\"color:red\">").setHighlighterPostTags("</span>");
            }

            //查询
            SearchResponse response = searchRequestBuilder.execute().actionGet();

            //取值
            SearchHits hits = response.getHits();
      
            //client.close();
            return lists;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }


    public List<Map<String, Object>> queryDocumentsByKeyword(String index, String type, String keyword,int size) throws UnknownHostException{
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        TransportClient transportClient = getInstance();
        SearchResponse response = null;
        if(size<=10000){
        if("".equals(keyword)){
            response = transportClient.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setScroll(new TimeValue(20000))
                    .setSize(size)
                    .setExplain(true)
                    .get();
        } else {
            QueryBuilder query = QueryBuilders.queryStringQuery(keyword);// Query
             response = transportClient.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(query)  
                    .setScroll(new TimeValue(20000))
                    .setSize(size)
                    .setExplain(true)
                    .get();
        }
        }else if(size>10000){
            if("".equals(keyword)){
                response = transportClient.prepareSearch(index)
                        .setTypes(type)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setFrom(0).setSize(size)
                        .setExplain(true)
                        .get();
            } else {
                QueryBuilder query = QueryBuilders.queryStringQuery(keyword);// Query
                 response = transportClient.prepareSearch(index)
                        .setTypes(type)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(query)  
                        .setFrom(0).setSize(10000)
                        .setExplain(true)
                        .get();
            }
        }
    
        //取值
        long total = response.getHits().getTotalHits();
        if(total==0){
            return null;
        }else{
            SearchHit[] searchHits = response.getHits().getHits();
            for(SearchHit searchHit:searchHits){
                lists.add(searchHit.getSourceAsMap());
            }
            return lists;
        }
    }
    /**
     * 分页查询结果
     * @param index
     * @param type
     * @param keyword
     * @param startNum
     * @param size
     * @return
     * @throws UnknownHostException
     */
    public List<Map<String, Object>> queryDocumentsByKeywordByPage(String index, String type, String keyword,int startNum,int size) throws UnknownHostException{
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        TransportClient transportClient = getInstance();
        SearchResponse response = null;
        if(size<=10000){

            if("".equals(keyword)){
                response = transportClient.prepareSearch(index)
                        .setTypes(type)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setFrom(startNum).setSize(size)
                        .setExplain(true)
                        .get();
            } else {
                QueryBuilder query = QueryBuilders.queryStringQuery(keyword);// Query
                 response = transportClient.prepareSearch(index)
                        .setTypes(type)
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(query)  
                        .setFrom(startNum).setSize(10000)
                        .setExplain(true)
                        .get();
            }
        }else{
            return null;
        }
    
       //取值
       long total = response.getHits().getTotalHits();
    if(total==0){
        return null;
    }else{
        SearchHit[] searchHits = response.getHits().getHits();
        for(SearchHit searchHit:searchHits){
            lists.add(searchHit.getSourceAsMap());
        }
        return lists;
    }
    }
    
    
    public long queryDocumentsByKeywordCount(String index, String type, String keyword) throws UnknownHostException{
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        TransportClient transportClient = getInstance();
        SearchResponse response = null;
        
        if("".equals(keyword)){
            response = transportClient.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setExplain(true)
                    .get();
        } else {
            QueryBuilder query = QueryBuilders.queryStringQuery(keyword);// Query
             response = transportClient.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(query)  
                    .setExplain(true)
                    .get();
        }
        
    
       //取值
       long total = response.getHits().getTotalHits();
       return total;
    }
    
    public static List<Map<String, Object>> queryDocumentsByTerm(String index, String type, String keyword,String[] fields) throws UnknownHostException{
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        TransportClient transportClient = getInstance();
        SearchResponse response = null;
        if("".equals(keyword)){
            response = transportClient.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setExplain(true)
                    .get();
        } else {
//          QueryBuilder query = QueryBuilders.termQuery("name", keyword);// Query
            QueryBuilder query = QueryBuilders.queryStringQuery(keyword);
             response = transportClient.prepareSearch(index)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(query)  
                    .setExplain(true).setFetchSource(fields, null)
                    .get();
        }
    
       //取值
       long total = response.getHits().getTotalHits();
    if(total==0){
        return null;
    }else{
        SearchHit[] searchHits = response.getHits().getHits();
        for(SearchHit searchHit:searchHits){
            lists.add(searchHit.getSourceAsMap());
        }
        return lists;
    }
    }
    
     public List queryCountByFields(String index, String type,String logicOperator,List<Map> queryLists,List fields) throws Exception{
        List list=new ArrayList();  
         TransportClient client  = getInstance();
            //** 下面这一段是构造bool嵌套，就是构造一个在满足精确查找的条件下，再去进行多字段的或者关系的全文检索 **//*
            //构造全文或关系的查询
            List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
             BoolQueryBuilder bb1 = QueryBuilders.boolQuery();
             if(logicOperator!=""){
                 if (queryLists != null && queryLists.size() > 0) {
                        //构造精确的并且查询
                        for (Map<Object, Object> map : queryLists) {
                            if (map != null && (!map.isEmpty())) {
                                String comparisonOperator = map.get("comparisonOperator").toString();
                              //构造全文或关系的查询
                                BoolQueryBuilder bb = QueryBuilders.boolQuery();
                                if(comparisonOperator.equals(Const.OPERATOR_EQUAL)){
                                    bb = bb.must(QueryBuilders.termQuery((String) map.get("accessPoint"), map.get("value")));
                                    //bb = bb.must(QueryBuilders.matchPhraseQuery((String) map.get("accessPoint"), map.get("value")));
                                   //bb = bb.must(QueryBuilders.matchQuery((String) map.get("accessPoint"), map.get("value")));
                                }else if(comparisonOperator.equals(Const.OPERATOR_LESSANDEQUALTHAN)){
                                    
                                }else if(comparisonOperator.equals(Const.OPERATOR_LIKE)){
                                    bb = bb.must(QueryBuilders.wildcardQuery((String)map.get("accessPoint"), (String)map.get("value")));
                                }
                                
                                if(logicOperator.equals(Const.OPERATOR_AND)){
                                    bb1 = bb1.must(bb);
                                }
                                
                            } 
                        }
                    }
             }
            
            for(int i=0;i<fields.size();i++){
                Map map=new HashMap();
                String field=(String) fields.get(i);
            //构造查询
             AggregationBuilder aggregation=AggregationBuilders.sum(field).field(field);
             SearchResponse  response = client.prepareSearch(index).addAggregation(aggregation)
                            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                            .setQuery(bb1) // Query
                            .setExplain(true)
                            .execute()
                            .actionGet();
             Sum filedSum=response.getAggregations().get(field);
             map.put(field, filedSum.getValueAsString());
             list.add(map);
            }
            
            
           return list;
        }
    
    public void statTest(String index)throws Exception{
        TransportClient transportClient = getInstance();
        AggregationBuilder aggregation=AggregationBuilders.sum("id").field("id");
        SearchResponse  response = transportClient.prepareSearch(index).addAggregation(aggregation).get();
        Sum sum=response.getAggregations().get("id");
        System.out.println(sum.getValue());
        
    }
    
    public static void analyzerTest(String index)throws Exception{
        TransportClient transportClient = getInstance();
        AnalyzeRequestBuilder request=AnalyzeAction.INSTANCE.newRequestBuilder(transportClient);
        request.setAnalyzer("standard");
//      request.setAnalyzer("standard");
//      request.setAnalyzer("ik_smart");
//      request.setAnalyzer("ik_max_word");m,
//        request.setIndex(index);
        request.setText("DIODE SCHOTTKY 40V 500MA SOD123");
        AnalyzeResponse response = request.execute().actionGet();
        List<AnalyzeToken> analyzeTokens = response.getTokens();
        List<String> results = new ArrayList<String>();
        for (AnalyzeToken token : analyzeTokens) {
            System.out.println(token.getTerm());
            results.add(token.getTerm());
        }
        
        
        

        
    }
    
    public static void queryTest(String index,String type,String keyword)throws Exception{
        TransportClient transportClient = getInstance();
      QueryBuilder query = QueryBuilders.queryStringQuery("*"+keyword+"*");// Query
//        QueryBuilder query =QueryBuilders.matchQuery("application", keyword);
        AggregationBuilder aggregation=AggregationBuilders.terms("aa").field("thirdcategory");

        SearchResponse response = transportClient.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(query)
                .addAggregation(aggregation)
                .setExplain(true)
                .get();
        Terms qwe=response.getAggregations().get("aa");
        List<StringTerms.Bucket> list= (List<StringTerms.Bucket>) qwe.getBuckets();
        for (Terms.Bucket b:list){
            System.out.println(b.getKeyAsString()+b.getDocCount());
        }
        SearchHit[] searchHits = response.getHits().getHits();
        List lists=new ArrayList();
        for(SearchHit searchHit:searchHits){
            System.out.println(JSONObject.fromObject(searchHit.getSourceAsMap()).toString());
//              lists.add(searchHit.getSource());
        }
    }
    
     
    public static void main(String[] args) {
//        String str="{'id':1,'name':'李彦路','sex':'男','age':'25','desc':[{'desc1':'高大威猛'},{'desc1':'英勇帅气'},{'desc1':'风流倜傥'},{'desc1':'绝世普通青年'}]}";
//      String str1="{'settings':{'analysis':{'analyzer':{'std_keyword':{'type':'keyword'}}}}}";
//        JSONObject o=JSONObject.fromObject(str);
//      JSONObject o1=JSONObject.fromObject(str1);
//      JSONObject all=new JSONObject();
//      all.p
//      XContentBuilder builder=XContentFactory.jsonBuilder().
//      addDocumentByBuilder()
//      addDocumentByAnalyzer("keyword","test1","type");
//      addDocument(o, "test1", "people", "1");
//        String[] fields=new String[]{"name","desc.desc1"};
//        try {
//          statTest("test");
         //analyzerTest("rkysjbz_rkysj");
//          Map map =new HashMap();
//          map.put("name", "李彦路");
//          List queryLists =new ArrayList();
//          Map queryMap=new HashMap();
//          queryMap.put("comparisonOperator",Const.OPERATOR_EQUAL);
//          queryMap.put("accessPoint", "name");
//          queryMap.put("value", "李彦路");
//          queryLists.add(queryMap);
//          List list=new ArrayList();
//          list.add("id");
//          List resutl=queryCountByFields("test","people",Const.OPERATOR_AND,queryLists,list);
            //queryTest("test1","people","英勇帅气");
            //long queryCount = queryCount("government_zwsj");
            //System.out.println(queryCount);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//      String[] fields=new String[]{};
//      try {
//          List<Map<String, Object>> list=queryDocumentsByTerm("test","people","李彦路",fields);
//          for(int i=0;i<list.size();i++){
//              Map<String, Object> map=list.get(i);
//              for(Map.Entry<String, Object> entry:map.entrySet()){
//                  System.out.println(entry.getKey());
//                  System.out.println(entry.getValue());
//                  
//              }
//          }
//      } catch (UnknownHostException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
        //根据condition生成mapping
//        try {
//            XContentBuilder builder=XContentFactory.jsonBuilder().startObject().field("dynamic",false).startObject("properties");
//            JSONObject field_analyzer=new JSONObject();
//            JSONObject fieldObject=new JSONObject();
//            fieldObject.put("type", "text");
//            fieldObject.put("store", true);
//            fieldObject.put("analyzer", "ik_max_word");
//            field_analyzer.put("pn_analyzer", fieldObject);
//            JSONObject field_analyzer1=new JSONObject();
//            JSONObject fieldObject1=new JSONObject();
//            fieldObject1.put("type", "text");
//            fieldObject1.put("store", true);
//            fieldObject1.put("analyzer", "standard");
//            field_analyzer1.put("application_analyzer", fieldObject1);
//            //料号
////            builder.startObject("pn").field("type", "text").field("store", true).field("analyzer", "keyword").field("fielddata",true).field("fields",field_analyzer).endObject();
//            builder.startObject("pn").field("type", "text").field("store", true).field("analyzer", "keyword").field("fielddata",true).endObject();
//            //一级分类
//            builder.startObject("firstcategory").field("type", "text").field("store", true).field("analyzer", "keyword").field("fielddata",true).endObject();
//            //二级分类
//            builder.startObject("secondcategory").field("type", "text").field("store", true).field("analyzer", "keyword").field("fielddata",true).endObject();
//            //三级分类
//            builder.startObject("thirdcategory").field("type", "text").field("store", true).field("analyzer", "keyword").field("fielddata",true).endObject();
//            //规格
//            builder.startObject("application").field("type", "text").field("store", true).field("analyzer", "standard").field("fielddata",true).endObject();
//            builder.endObject().endObject();
////            addDocumentByBuilder(builder, "test", "test");
//            String[] name={"电","子","元","件","二","极","管","发","光"};
//            List list=new ArrayList();
//            for (int i=0;i<10000;i++){
//                JSONObject object=new JSONObject();
//                Random r=new Random();
//                int a=r.nextInt(100000000);
//                int b=r.nextInt(100);
//                int c=r.nextInt(100);
//                int d=r.nextInt(100);
//                int e=r.nextInt(9);
//                int f=r.nextInt(9);
//                int g=r.nextInt(9);
//                int h=r.nextInt(9);
//
//                int m=r.nextInt(9);
//                int j=r.nextInt(9);
//                int k=r.nextInt(9);
//                int l=r.nextInt(9);
//
//                int n=r.nextInt(9);
//                int p=r.nextInt(9);
//                int q=r.nextInt(9);
//                int s=r.nextInt(9);
//
//                int id=r.nextInt(10000);
//                object.put("pn","sdfasdwer"+a);
//                object.put("firstcategory",name[e]+name[f]+name[g]+name[h]);
//                object.put("secondcategory",name[m]+name[j]+name[k]+name[l]);
//                object.put("thirdcategory",name[n]+name[p]+name[q]+name[s]);
//                object.put("application","DIODE SCHOTTKY "+c+"V "+d+"MA SOD"+b);
//                list.add(object);
////            addDocument(object,"test","test",id+"");
//            }
////            addDocuments4Bulk(list,"test","test");
//
////            JSONObject object=new JSONObject();
////            object.put("pn","sdfasdwer123123127");
////            object.put("firstcategory","电子元件");
////            object.put("secondcategory","二极管哈");
////            object.put("thirdcategory","发光二极管哈");
////            object.put("application","DIODE SCHOTTKY 40V 500MA SOD123");
////            addDocument(object,"test","test","126");
//
//            queryTest("test","test","件元管件");

//            analyzerTest("test");

//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String[] str=new String[]{"startDateTime"};
        try {
            List list=queryDocumentsByTerm("yedapipubyfsj0006","4051E3B614F3421D931DD6B68AB0714A","20050",str);
            System.out.printf(list.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
    
}