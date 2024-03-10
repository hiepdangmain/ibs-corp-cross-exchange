package com.msb.ibs.corp.cross.exchange.infrastracture.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.msb.ibs.common.enums.LanguageEnum;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
@Component
public class TranslateUtils {
	private static MessageSource messageSource;
	private static final String LANGUAGE_BASE = LanguageEnum.VI.getValue();
	private static Pattern twopart = Pattern.compile("([a-zA-Z]{2})[-_]([a-zA-Z]{2})");

	@Autowired
	public TranslateUtils(MessageSource messageSource) {
		TranslateUtils.messageSource = messageSource;
	}

	public static String toLocale(String msg, HttpServletRequest request) {
		String headerLanguage = LANGUAGE_BASE;
		String lang = LanguageEnum.VI.getLang();
		String country = LanguageEnum.VI.getCountry();

		if (Objects.nonNull(request)) {
			String header = request.getHeader("Accept-Language");

			if (StringUtils.hasText(header) && LanguageEnum.contains(header)) {
				headerLanguage = header;
			}
		}

		Matcher m = twopart.matcher(headerLanguage);
		if (m.matches()) {
			if (StringUtils.hasText(m.group(1))) {
				lang = m.group(1);
			}
			if (StringUtils.hasText(m.group(2))) {
				country = m.group(2);
			}
		}

		Locale locale = new Locale(lang, country);

		String message = messageSource.getMessage(msg, null, locale);

		return message;
	}

	public static Locale toMessage(HttpServletRequest request) {
		String headerLanguage = LANGUAGE_BASE;
		String lang = LanguageEnum.VI.getLang();
		String country = LanguageEnum.VI.getCountry();

		if (Objects.nonNull(request)) {
			String header = request.getHeader("Accept-Language");

			if (StringUtils.hasText(header) && LanguageEnum.contains(header)) {
				headerLanguage = header;
			}
		}

		Matcher m = twopart.matcher(headerLanguage);
		if (m.matches()) {
			if (StringUtils.hasText(m.group(1))) {
				lang = m.group(1);
			}
			if (StringUtils.hasText(m.group(2))) {
				country = m.group(2);
			}
		}

		Locale locale = new Locale(lang, country);

		return locale;
	}

	public static String toLocale(String msg, String language) {
		String headerLanguage = LANGUAGE_BASE;
		String lang = LanguageEnum.VI.getLang();
		String country = LanguageEnum.VI.getCountry();

		if (StringUtils.hasText(language) && LanguageEnum.contains(language)) {
			headerLanguage = language;
		}

		Matcher m = twopart.matcher(headerLanguage);
		if (m.matches()) {
			if (StringUtils.hasText(m.group(1))) {
				lang = m.group(1);
			}
			if (StringUtils.hasText(m.group(2))) {
				country = m.group(2);
			}
		}

		Locale locale = new Locale(lang, country);

		String message = messageSource.getMessage(msg, null, locale);

		return message;
	}

	public static Locale toMessage(String language) {
		String headerLanguage = LANGUAGE_BASE;
		String lang = LanguageEnum.VI.getLang();
		String country = LanguageEnum.VI.getCountry();

		if (StringUtils.hasText(language) && LanguageEnum.contains(language)) {
			headerLanguage = language;
		}

		Matcher m = twopart.matcher(headerLanguage);
		if (m.matches()) {
			if (StringUtils.hasText(m.group(1))) {
				lang = m.group(1);
			}
			if (StringUtils.hasText(m.group(2))) {
				country = m.group(2);
			}
		}

		Locale locale = new Locale(lang, country);

		return locale;
	}

	public static String toLocale(String msg, String language, HttpServletRequest request) {
		String headerLanguage = LANGUAGE_BASE;
		String lang = LanguageEnum.VI.getLang();
		String country = LanguageEnum.VI.getCountry();

		if (Objects.nonNull(request)) {
			String header = request.getHeader("Accept-Language");

			if (StringUtils.hasText(header) && LanguageEnum.contains(header)) {
				headerLanguage = header;
			}
		} else {
			if (StringUtils.hasText(language) && LanguageEnum.contains(language)) {
				headerLanguage = language;
			}
		}

		Matcher m = twopart.matcher(headerLanguage);
		if (m.matches()) {
			if (StringUtils.hasText(m.group(1))) {
				lang = m.group(1);
			}
			if (StringUtils.hasText(m.group(2))) {
				country = m.group(2);
			}
		}

		Locale locale = new Locale(lang, country);

		String message = messageSource.getMessage(msg, null, locale);

		return message;
	}

	public static Locale toMessage(String language, HttpServletRequest request) {
		String headerLanguage = LANGUAGE_BASE;
		String lang = LanguageEnum.VI.getLang();
		String country = LanguageEnum.VI.getCountry();

		if (Objects.nonNull(request)) {
			String header = request.getHeader("Accept-Language");

			if (StringUtils.hasText(header) && LanguageEnum.contains(header)) {
				headerLanguage = header;
			}
		} else {
			if (StringUtils.hasText(language) && LanguageEnum.contains(language)) {
				headerLanguage = language;
			}
		}

		Matcher m = twopart.matcher(headerLanguage);
		if (m.matches()) {
			if (StringUtils.hasText(m.group(1))) {
				lang = m.group(1);
			}
			if (StringUtils.hasText(m.group(2))) {
				country = m.group(2);
			}
		}

		Locale locale = new Locale(lang, country);

		return locale;
	}
}
