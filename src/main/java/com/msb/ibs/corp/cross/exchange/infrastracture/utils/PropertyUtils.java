package com.msb.ibs.corp.cross.exchange.infrastracture.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
public class PropertyUtils {
	public static boolean propertiesExist(String propertiesFile, String value) {
		Properties prop = new Properties();
		InputStream input = null;
		boolean exists = false;

		try {
			input = PropertyUtils.class.getResourceAsStream(propertiesFile);

			prop.load(input);

			exists = prop.getProperty(value) != null;

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return exists;
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (Objects.isNull(srcValue))
				emptyNames.add(pd.getName());
		}

		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}
}
