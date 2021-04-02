package com.mlog.procmgr.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class Utils {
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	private static String configPath = null;
	private static Properties config = null;
	
	private static Properties dataBaseProperies = null;

	public static Map<String, Object> beatStrToObject(String beatStr) {
		Map<String, Object> jsonObj = new HashMap<String, Object>();

		if (beatStr == null) {
			return jsonObj;
		}

		String[] list = beatStr.replace("{", "").replaceAll("}", "").split(",");
		for (String data : list) {
			String item[] = data.split("=");

			if (item.length == 2) {
				jsonObj.put(item[0].trim(), item[1].trim());
			}
		}

		return jsonObj;
	}
	
	/**
	 * Convert JSON format string to token object.
	 * @param <T>
	 * @param jsonStr
	 * @return
	 */
	public static <T> Object jsonStrToObject(String jsonStr, Class<T> classType) {
		Object jsonObj = null;
		if (jsonStr == null) {
			return jsonObj;
		}

		try {
			Gson gson = new GsonBuilder().create();
			jsonObj = gson.fromJson(jsonStr, classType);
		} catch (Exception e) {
			logger.error("", e);
		}

		return jsonObj;
	}
	
	/**
	 * Convert JSON format string to Map.
	 * @param string
	 * @return
	 */
	public static Map<String, Object> jsonStrToObject(String jsonStr) {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		if (jsonStr == null) {
			return null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
			};

			jsonMap = mapper.readValue(jsonStr, typeRef);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

		return jsonMap;
	}

	/**
	 * Convert JSON format string to Map.
	 * @param string
	 * @return
	 */
	public static List<HashMap<String, Object>> jsonStrToList(String jsonStr) {
		List<HashMap<String, Object>> jsonList = new ArrayList<HashMap<String, Object>>();
		if (jsonStr == null) {
			return null;
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<HashMap<String, Object>>> typeRef = new TypeReference<List<HashMap<String, Object>>>() {
			};

			jsonList = mapper.readValue(jsonStr, typeRef);
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}

		return jsonList;
	}

	/**
	 * Convert object to JSON format string.
	 * @param object
	 * @return
	 */
	public static String objectToJsonStr(Object json) {
		String jsonStr = null;
		if (json == null) {
			return null;
		}

		try {
			Gson gson = new GsonBuilder().create();
			jsonStr = gson.toJson(json);
		} catch (Exception e) {
			logger.error("", e);
		}

		return jsonStr;
	}

	public static Properties getProperties() {
		if (config == null) {
			config = new Properties();
		} else {
			return config;
		}

		try {
			if (configPath == null) {
				configPath = "./conf/server.properties";
			}
			
			FileInputStream fis = new FileInputStream(configPath);
			config.load(fis);

			fis.close();
		} catch (Exception e) {
			logger.error("", e);
			
			return null;
		}

		return config;
	}
	
	public static void setConfigPath(String configPath) {
		Utils.configPath = configPath;
		logger.info("Set config path(" + configPath + ")");
	}
	
	public static String getProperty(String key) {
		if (config == null) {
			config = getProperties();
		}

		if (config == null) {
			logger.error("Could not load propeties.");
			return null;
		}

		String value = config.getProperty(key);
		if (value == null || value.length() == 0) {
			logger.error("failure get property [{}]=[{}]", key, value);
			return null;
		} else {
			logger.debug("success get property [{}]=[{}]", key, value);
		}

		return value;
	}
	
	public static int loadProperties(String path) {
		configPath = path;
		return 0;
	}

    public static int loadLogConfigs(String path) {
    	LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();

        try {
            configurator.doConfigure(path);
        } catch (JoranException e) {
            e.printStackTrace();
        }
        
        return 0;
    }


	/**
	 * 파일의 내용을 스트링으로 반환.
	 * @param path
	 * @return
	 */
	public static String readFile(String path) {
		String jsonStr = "";

		File file = new File(path);
		
		if (file.exists() == false) {
			logger.error("Could not find file in {}", path);
			return null;
		}

		try {
			String temp;

			BufferedReader br = new BufferedReader(new FileReader(file));
			while ((temp = br.readLine()) != null) {
				jsonStr += temp;
			}

			br.close();
		} catch (Exception e) {
			logger.error("", e);
		}

		return jsonStr;
	}

	/**
	 * 데이터베이스 프로퍼티 반환.
	 * @return
	 */
	public synchronized static Properties getDataBaseProperties() {
		if (dataBaseProperies != null) {
			return dataBaseProperies;
		} else {
			dataBaseProperies = new Properties();
		}
		
		String jsonInfo = readFile(getProperty(Constant.DATABASE_INFO_PATH));
		if (jsonInfo == null) {
			return null;
		}

		HashMap<String, Object> store = (HashMap<String, Object>) jsonStrToObject(jsonInfo);
		if (store == null) {
			return null;
		}

		for (String key : store.keySet()) {
			String val = store.get(key).toString();

//			if ("url".equals(key)) {
//				val = String.format(val, getDatabasePort());
//				System.out.println(val);
//			}

			dataBaseProperies.setProperty(key, val);
		}

		return dataBaseProperies;
	}

	/**
	 * 데이터베이스 프로퍼티 반환.
	 * @return
	 */
	public synchronized static Properties getDataBaseProperties(String dataSource) {
		Properties dataBaseProperies = new Properties();
//		if (dataBaseProperies != null) {
//			return dataBaseProperies;
//		} else {
//			dataBaseProperies = new Properties();
//		}

		String jsonInfo = readFile(getProperty(Constant.DATABASE_INFO_PATH));
		if (jsonInfo == null) {
			return null;
		}

		HashMap<String, Object> test = (HashMap<String, Object>) jsonStrToObject(jsonInfo);
		if (test == null) {
			return null;
		}
		
		logger.debug(">> {}", dataSource);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> store = (HashMap<String, Object>)test.get(dataSource);

		for (String key : store.keySet()) {
			String val = store.get(key).toString();

			System.out.println("key:" + key + " / val:" + val);
			dataBaseProperies.setProperty(key, val);
		}

		return dataBaseProperies;
	}


	public static String convDateFormat(String time, String sourceFormat, String targetFormat) {
		String convDateFormat = "";

		try {
			if (time != null) {
				time = time.substring(0, time.length() - 1) + "0";
			}

			DateFormat sourceDateFormat = new SimpleDateFormat(sourceFormat);
			Date date = sourceDateFormat.parse(time);

			DateFormat targetDateFormat = new SimpleDateFormat(targetFormat);
			convDateFormat = targetDateFormat.format(date);
		} catch (Exception e) {
			logger.error("", e);
		}

		return convDateFormat;
	}


	public static int getProcessName() {
		int id = -1;

		String name = ManagementFactory.getRuntimeMXBean().getName();
		if (name != null) {
			id = Integer.valueOf(name.split("@")[0]);
		}
//		System.out.println(name);
		
		return id;
	}	

	
/**
    * shell에 등록된 변수에 해당되는 값을 가져온다.
    * @param param shell에 등록된 환경 변수명
    * @return 환경 변수의 값
    * */
    public static String getEnv(String parm) {

    	String value = null;
        String sTemp = null;
        int pos;

        BufferedReader br = null;
        Process process = null;

        try {
            process = Runtime.getRuntime().exec("env");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((sTemp = br.readLine()) != null) {
                if (sTemp.indexOf(parm) >= 0) {
                    pos = sTemp.indexOf("=");
                    value = sTemp.substring(pos + 1);
                }
            }
            br.close();
            br = null;

        } catch (Exception e) {}
        if (process != null) process.destroy();
        process = null;

        return value;

    }
	    
    //다른 포맷이 필요하다면 case에 추가하세요
    /**
	* <pre>
	* 현재 날짜(또는 일자,시간)를 다양한 포멧으로 리턴한다.
	* iCase
	*  1 : yyyyMMddHHmmss
	*  2 : yyyyMMddHHmm
	*  3 : dd
	*  4 : yyyyMMdd
	*  5 : yyyy/MM/dd HH:mm:ss
	*  6 : MM/dd HH:mm:ss
	*  7 : HH
	*  8 : mm
	*  9 : HHmm
	* </pre>
	* @param iCase 숫자에 따라서 시간 포맷이 달라진다.
	* */
    static public String getTime(int iCase) {
	   Calendar cal = Calendar.getInstance(new Locale("Korean", "Korea"));
	   SimpleDateFormat df = null;

	   switch (iCase) {
	   case 1: df = new SimpleDateFormat("yyyyMMddHHmmss"); break;
	   case 2: df = new SimpleDateFormat("yyyyMMddHHmm");   break;
	   case 3: df = new SimpleDateFormat("yyyyMMddHH");     break;
	   case 4: df = new SimpleDateFormat("yyyyMMdd");       break;
	   case 5: df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); break;
	   case 6: df = new SimpleDateFormat("MM/dd HH:mm:ss"); break;
	   case 7: df = new SimpleDateFormat("HH");             break;
	   case 8: df = new SimpleDateFormat("mm");             break;
	   case 9: df = new SimpleDateFormat("mmss");           break;
	   case 10: df = new SimpleDateFormat("yyyyMM");        break;
	   default: break;
	   }
	   return df.format(cal.getTime());
    }
    
    static public String getRandomId() {
    	String randomId = null;
    	
    	return randomId;
    }

}
