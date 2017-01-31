package com.seeyon.v3x.portal.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author dongyj
 *
 */
@Deprecated
public class MacroParse {
	//private static final Log log = LogFactory.getLog(MacroParse.class);
	private static Map<String,String> properties= new HashMap<String,String>();
	
	public static String getMacro(String key){
		return properties.get(key);
	}
	
	public static void readFromFile(File file){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			boolean isStart = true;
			StringBuffer body = new StringBuffer();
			String macroName = "";
			int count = 0;
			while((line = reader.readLine()) != null){
				if(isStart){
					macroName = parseFunName(line);
					if(macroName != null){
						isStart = false;
					}
					continue;
				}
				if(isEnd(line)){
					properties.put(macroName, body.toString());
					isStart = true;
					count = 0;
					body = new StringBuffer();
					continue;
				}
				body.append(line);
				if(count !=0){
					body.append("\n");
				}
				count ++;
			}
		} catch (FileNotFoundException e) {
			//log.error("解析宏配置",e);
		} catch (IOException e) {
			//log.error("解析宏配置",e);
		}
	}
	
	private static String parseFunName(String str){
		if(str != null &&str.indexOf("@") >=0){
			char[] cstr = str.toCharArray();
			StringBuilder sb = new StringBuilder();
			for(char c:cstr ){
				if(c != ' ' && c != '@'){
					sb.append(c);
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	private static boolean isEnd(String str){
		if(str != null && str.indexOf("@end") >=0){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		File f = new File("E:\\workspace\\V3XApp\\WebContent\\WEB-INF\\decorations\\layout\\seeyon_declare.properties");
	}
}
