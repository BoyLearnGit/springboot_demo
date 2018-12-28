package com.youedata.elasticsearch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import net.sf.json.JSONObject;

@RestController
@RequestMapping(value="/es")
public class ESController {
	
	
	private String indexdb = "esdb";
	private String indextab = "tab1";
	@Autowired
	private ElasticSearchTools  es;
	
	@RequestMapping(value="/cretDB")
	public Object cretDB() {
		es.addMapping(indexdb, indextab);
		return null;
	}
	@RequestMapping(value="/add")
	public Object add() {
		JSONObject obj = new JSONObject();
		es.addDocument(obj,indexdb,indextab,"1");
		return null;
	}
	@RequestMapping(value="/del")
	public Object del() {
		Map<String,String> map = new  HashMap<String,String>();
		es.deleteByQuery(map,indexdb,indextab);
		return null;
	}
	@RequestMapping(value="/upd")
	public Object upd() {
		JSONObject obj = new JSONObject();
		es.updateDocument(indexdb,indextab,"1","haha");
		return null;
	}
	@RequestMapping(value="/qur")
	public Object qur() {
		
		return null;
	}
	
	
}
