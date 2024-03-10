package com.msb.ibs.corp.cross.exchange.infrastracture.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Redis tools
 *
 * @author binhnt26
 *
 */
@Component
public class RedisUtils {

	private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static RedisUtils redisUtils;
	Gson gson = new Gson();

	/**
	 * initialization
	 */
	@PostConstruct
	public void init() {
		redisUtils = this;
		redisUtils.redisTemplate = this.redisTemplate;
	}

	public static Object get(String key) {
		try {
			return StringUtils.hasText(key) ? redisUtils.redisTemplate.opsForValue().get(key) : null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils get(String key) failure. {}", e.getMessage());
			return null;
		}
	}

	public static boolean set(String key, String value) {
		try {
			redisUtils.redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils set(String key, Object value) failure. {}", e.getMessage());
			return false;
		}
	}

	public static boolean set(String key, String value, long expire) {
		try {
			if (expire > 0) {
				redisUtils.redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
			} else {
				redisUtils.redisTemplate.opsForValue().set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils set(String key, Object value, long time) failure. {}", e.getMessage());
			return false;
		}
	}

	public static boolean delete(String key) {
		try {
			redisUtils.redisTemplate.opsForValue().getOperations().delete(key);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils delete(String key) failure. {}", e.getMessage());
			return false;
		}
	}

	public static boolean hset(String key, String hashKey, Object object) {
		try {
			redisUtils.redisTemplate.opsForHash().put(key, hashKey, object);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils hset(String key, String hashKey, Object object) failure. {}", e.getMessage());
			return false;
		}
	}

	public static boolean hset(String key, String hashKey, Object object, Integer expire) {
		try {
			redisUtils.redisTemplate.opsForHash().put(key, hashKey, object);
			if (expire > 0) {
				redisUtils.redisTemplate.expire(key, expire, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils hset(String key, String hashKey, Object object) failure. {}", e.getMessage());
			return false;
		}
	}

	public static Object hget(String key, String hashKey) {
		try {
			return StringUtils.hasText(key) ? redisUtils.redisTemplate.opsForHash().get(key, hashKey) : null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils hget(String key, String hashKey) failure. {}", e.getMessage());
			return new Exception("REDIS.ERROR");
		}
	}

	public static boolean deleteKey(String key) {
		try {
			return redisUtils.redisTemplate.opsForHash().getOperations().delete(key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils deleteKey(String key) failure. {}", e.getMessage());
			return false;
		}
	}

	public static Object hasKey(String key) {
		try {
			return redisUtils.redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils hasKey(String key) failure. {}", e.getMessage());
			return new Exception("REDIS.ERROR");
		}
	}

	public static void setExpireToken(String token, Long timeout) {
		try {
			redisUtils.redisTemplate.expire(token, timeout, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("RedisUtils setExpireToken(String token, Long timeout) failure. {}", e.getMessage());
		}
	}

	public <T> void set(String key, T object, long expireTimeSeconds) {
		String value = gson.toJson(object);
		if (StringUtils.hasText(value)) {
			this.set(key, value, expireTimeSeconds);
		}
	}
}
