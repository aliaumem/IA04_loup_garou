package utc.tatinpic.semantics;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


import android.util.Log;

public class MessageContentBroker {
	/*
	 * First level content elements: action, args, answer, error-contents
	 */
	Map<String, Object> map;
	LinkedHashMap<String, Object> argmap;
	ObjectMapper messageMapper = new ObjectMapper();
	String message = null;
		
	public MessageContentBroker() {
		map = new HashMap<String,Object>();
		argmap = new LinkedHashMap<String, Object>();
		map.put(ContentOntology.ARGS, argmap);
	}
   public MessageContentBroker(String s) {
		
		try {
			
			map = messageMapper.readValue(s, Map.class);
			argmap = (LinkedHashMap<String, Object>) map.get(ContentOntology.ARGS);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
   /*
	 * add a first level value
	 */
	public void add(String key, Object value) {
		map.put(key, value);
	}
	/*
	 * Add args value as an object
	 */
	public void addArgs(Object args) {
		map.put(ContentOntology.ARGS, args);
	}
	public void putArg(String arg, Object value) {
		argmap.put(arg, value);
	}
	public String getType() {
		return (String) argmap.get(ContentOntology.TYPE);
	}
	public String getValue() {
		return (String) argmap.get(ContentOntology.VALUE);
	}
	public String getAction() {
		return (String) map.get(ContentOntology.ACTION);
	}
	public void putAction(String value) {
		map.put(ContentOntology.ACTION, value);
	}
	public ArrayList<String> getRef() {
		return (ArrayList<String>) argmap.get(ContentOntology.REF);
	}
	public String getTo() {
		return (String) argmap.get(ContentOntology.TO);
	}
	public ArrayList<Object> getAnswer() {
		Object o = map.get(ContentOntology.ANSWER);
		ArrayList<Object> ans;
		if (o == null) {
			ans = new ArrayList<Object>();
			map.put(ContentOntology.ANSWER, ans);
		}
		else
			ans = (ArrayList<Object>) o;
		return ans;
	}
	
	public Object jasonFirstAnswer(Class c) {
		return getRowAnswerObject(c, 0);
	}
	public Object jasonAnswer(Class c, int row) {
		return getRowAnswerObject(c, row);
	}
	
	public Object jasonArgs(Class c) {
		return  getParamObject(ContentOntology.ARGS, c);
	}
	
	public Object getParamObject(String key, Class c) {
		return getMapObject(key, c, map);
	}
	private Object getMapObject(String key, Class c, Map<String, Object> orgmap) {
		LinkedHashMap<String, Object> kmap = (LinkedHashMap<String, Object>) orgmap.get(key);
		StringWriter sw = new StringWriter();
		Object o = null;
		try {
			messageMapper.writeValue(sw, kmap);
			o = messageMapper.readValue(sw.toString(), c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	/*private Object getMapObject(Class cl, Map<String, Object> orgmap) {
		
		StringWriter sw = new StringWriter();
		Object o = null;
		ObjectMapper messageMapper = new ObjectMapper();
		try {
			messageMapper.writeValue(sw, orgmap);
			o = messageMapper.readValue(sw.toString(), cl);
			Log.v("mcb",sw.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	*/
	private Object getRowAnswerObject(Class cl, int row) {
		Object o = null;
		if (row >= 0) {
			StringWriter sw = new StringWriter();
		    ObjectMapper messageMapper = new ObjectMapper();
		try {
			Object ans = getAnswer().get(row);
			if (ans != null) {
				messageMapper.writeValue(sw, ans);
				o = messageMapper.readValue(sw.toString(), cl);
				Log.v("mcb",sw.toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		return o;
	}
	public String jasonString() {
		StringWriter sw = new StringWriter();
		try {
			messageMapper.writeValue(sw, map);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sw.toString();
	}
}
