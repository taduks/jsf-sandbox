package org.example.services;

import org.apache.commons.collections.map.MultiKeyMap;

import javax.faces.context.FacesContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;


public class MessageResourcesHelper {

	public static String BUNDLE_MESSAGE_RESOURCES = "i18n.Examples";
	public static String BUNDLE_OPTION_MESSAGES = "i18n.Entities";
	
	private static MultiKeyMap messageCache = new MultiKeyMap();
	private static MultiKeyMap optionValueCache = new MultiKeyMap();
	private static MultiKeyMap optionCaptionCache = new MultiKeyMap();
	private static Map<String, ResourceBundle> bundleCache = Collections.synchronizedMap(new HashMap<String, ResourceBundle>());


    public static String getMessage(String messageId, Object[] params) {
        Locale locale = Locale.getDefault();

        return getMessage(locale, messageId, params);
    }

    public static String getMessage(Locale locale, String messageId, Object[] params) {
    	String summary = null;
    	synchronized (messageCache) {
    		summary = (String) messageCache.get(locale, messageId);
		}
    	
    	boolean updateCache = (summary == null);
        
        if (summary == null) {
        	summary = getMessage(locale, BUNDLE_MESSAGE_RESOURCES, messageId);
        }
        if (summary == null) {
        	summary = getMessage(locale, BUNDLE_OPTION_MESSAGES, messageId);
        }
        if (updateCache) {
        	synchronized (messageCache) {
        		messageCache.put(locale, messageId, summary);
			}
        }
        
        return summary;
    }

    private static String getMessage(Locale locale, String bundleName, String messageId) {
    	ResourceBundle bundle = bundleCache.get(bundleName);
    	if (bundle == null) {
    		bundle = ResourceBundle.getBundle(bundleName);
    		bundleCache.put(bundleName, bundle);
    	}  		

    	String result = null;
   		try {
   			result = bundle.getString(messageId);
        }
        catch (MissingResourceException e) {
   		}
        
        return result;
    }

    public static String getMessage(FacesContext context, String messageId) {
        
        return getMessage(context, messageId, null);
    }

    public static String getMessage(FacesContext context, String messageId, Object[] params) {
        Locale locale = null;
        if (context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
        } else {
            locale = Locale.getDefault();
        }

        return getMessage(locale, messageId, params);
    }

    public static String getMessage(FacesContext context, String messageId, Object param0) {
        return getMessage(context, messageId, new Object[] { param0 });
    }

    public static String getMessage(FacesContext context, String messageId, Object param0, Object param1) {
        
        return getMessage(context, messageId, new Object[] { param0, param1 });
    }

    public static String getMessage(FacesContext context, String messageId, Object param0, Object param1,
        Object param2) {
        
        return getMessage(context, messageId, new Object[] { param0, param1, param2 });
    }

    public static String getMessage(FacesContext context, String messageId, Object param0, Object param1,
        Object param2, Object param3) {
        
        return getMessage(context, messageId, new Object[] { param0, param1, param2, param3 });
    }


    protected static ClassLoader getCurrentLoader(Object fallbackClass) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader == null) {
            loader = fallbackClass.getClass().getClassLoader();
        }

        return loader;
    }
    
    public static String getMessage(String messageId) {
    	return getMessage(messageId, false);
    }
    
    public static String getMessage(String messageId, boolean nullOnNotFound) {
    	String msg = MessageResourcesHelper.getMessage(messageId, null);
    	
    	if ((msg == null) && (!nullOnNotFound)) {
    		msg = "(no message found for " + messageId + ")";
		}
    	return msg;
    }

    public static String getEntityCaption(String entityName) {
		String path = "entity." + entityName;
		String entityCaption = MessageResourcesHelper.getMessage(path);
		return entityCaption;
    }


    public static String getJobCaption(String jobName) {
		String path = "job." + jobName;
		String jobCaption = MessageResourcesHelper.getMessage(path, null);		
		
		if (jobCaption == null) {
			jobCaption = "(no caption found for " + path + ")";
		}
		return jobCaption;
    }

	public static String getReportFieldCaption(String reportName, String fieldName) {	
		String path = "report." + reportName + ".param." + fieldName;
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		if (fieldCaption == null) {
			String altPath = "field.common." + fieldName;		
			fieldCaption = MessageResourcesHelper.getMessage(altPath, null);
		}
				
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}

	public static String getJobFieldCaption(String jobName, String fieldName) {	
		String path = "job." + jobName + ".param." + fieldName;
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		if (fieldCaption == null) {
			String altPath = "field.common." + fieldName;		
			fieldCaption = MessageResourcesHelper.getMessage(altPath, null);
		}
		
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}


	
	public static String getJobOptionFieldValueCaption(String jobName, String fieldName, Integer optionNo) {
		if (optionNo == null) {
			optionNo = 0;
		}
		
		String path = "job." + jobName + "." + fieldName + "." + optionNo.toString();
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		if (fieldCaption == null) {
			String altPath = "field.common." + fieldName;		
			fieldCaption = MessageResourcesHelper.getMessage(altPath, null);
		}
		
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}
	
	public static String getParamItemFieldCaption(String jobName, String paramName, String fieldNo) {
		String path = "job." + jobName + "." + "param" + "." + paramName + "." + fieldNo;
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		if (fieldCaption == null) {
			String altPath = "field.common." + paramName;		
			fieldCaption = MessageResourcesHelper.getMessage(altPath, null);
		}
		
				
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}
	
	/**
	 * RamunasD
	 * @param reportName
	 * @param paramName
	 * @param fieldNo
	 * @return
	 */
	public static String getReportParamFieldCaption(String reportName, String paramName, String fieldNo) {
		String path = "report." + reportName + "." + "param" + "." + paramName + "." + fieldNo;
			
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		if (fieldCaption == null) {
			String altPath = "field.common." + paramName;		
			fieldCaption = MessageResourcesHelper.getMessage(altPath, null);
		}
		
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}
	
	public static String getReportParamFieldCaption(String reportName, String paramName) {
		String path = "report." + reportName + "." + "param" + "." + paramName;
			
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		if (fieldCaption == null) {
			String altPath = "field.common." + paramName;		
			fieldCaption = MessageResourcesHelper.getMessage(altPath, null);
		}
		
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}
	
	private static String getOptionFieldValueCaption(String className, String fieldName, Integer optionNo) {
		if (optionNo == null) {
			optionNo = 0;
		}
		
		String path = "option." + className + "." + fieldName + "." + optionNo.toString();
		String fieldCaption = MessageResourcesHelper.getMessage(path, null);
		
		if (fieldCaption == null) {
			fieldCaption = "(no caption found for " + path + ")";
		}
		
		return fieldCaption;
	}
	
	@SuppressWarnings("unchecked")
	private static void prepareOptionMap(String suggestionClass, String suggestionField) {
		List<Integer> values = null;
		List<String> captions = new ArrayList<String>();

		Class<?> clazz = null;
		try {
			clazz = Class.forName(suggestionClass);
		} catch (ClassNotFoundException e1) {			
			e1.printStackTrace();
		}
		
		for (Class<?> c : clazz.getClasses()) {
			if (c.getSimpleName().equals(suggestionField)) {
				Method m = null;
				try {        									
					Constructor constructor = c.getConstructor(clazz);

					Object entityInstance = clazz.newInstance();
					Object instance = constructor.newInstance(entityInstance);

					m = c.getMethod("getList");
					values = (List<Integer>) m.invoke(instance);
				} catch (Exception e) {
					System.out.print(e.getMessage());
				} 
				break;

			}    		
		}
		if (values == null) {
			for (Class<?> c : clazz.getClasses()) {
				if (c.getSimpleName().equals(suggestionField)) {
					Method m = null;
					try {        									
						Constructor constructor = c.getConstructor(clazz);

						Object entityInstance = clazz.newInstance();
						Object instance = constructor.newInstance(entityInstance);

						m = c.getMethod("getList");
						values = (List<Integer>) m.invoke(instance);
					} catch (Exception e) {
						System.out.print(e.getMessage());
					} 
					break;

				}
			}
		}
		for (Integer i : values) {
			captions.add(
					MessageResourcesHelper.getOptionFieldValueCaption(
							clazz.getSimpleName(), 
							suggestionField, 
							i
					)
			);
		}
		
		synchronized (optionValueCache) {
			optionValueCache.put(suggestionClass, suggestionField, values);
		}
		
		synchronized (optionCaptionCache) {
			optionCaptionCache.put(suggestionClass, suggestionField, captions);
		}
		
	}
	@SuppressWarnings("unchecked")
	public static List<Integer> getOptionValues(String suggestionClass, String suggestionField) {
		synchronized (optionValueCache) {
			List<Integer> result = (List<Integer>) optionValueCache.get(suggestionClass, suggestionField);
			
			if (result == null) {
				prepareOptionMap(suggestionClass, suggestionField);
				result = (List<Integer>) optionValueCache.get(suggestionClass, suggestionField);
			}
			return result;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getOptionCaptions(String suggestionClass, String suggestionField) {
		
		synchronized (optionCaptionCache) {
			List<String> result = null;
			result = (List<String>) optionCaptionCache.get(suggestionClass, suggestionField);

			if (result == null) {
				prepareOptionMap(suggestionClass, suggestionField);
				result = (List<String>) optionCaptionCache.get(suggestionClass, suggestionField);
			}
			return result;
		}
	}
	
	public static String getOptionText(Class<?> suggestionClass, String suggestionField, Integer indexValue) {
		List<Integer> values = getOptionValues(suggestionClass.getName(), suggestionField);
		List<String> captions = getOptionCaptions(suggestionClass.getName(), suggestionField);
		
		if ((values != null) && (captions != null)) {
			try { 
				return captions.get(values.indexOf(indexValue));
			} catch (Exception e) {
				// do nothing
			}
			
		}
			
		return "(no option resources found for class " + suggestionClass.getSimpleName() + "." + suggestionField + ")";
		
		
	}

}
