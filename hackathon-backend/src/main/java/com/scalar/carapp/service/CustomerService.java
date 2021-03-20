package com.scalar.carapp.service;

import com.scalar.base.BaseConstant;
import com.scalar.base.BaseService;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.constant.CustomerConstant;
import com.scalar.carapp.repository.CustomerRepository;

import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerService extends BaseService{

	@Autowired
	private CustomerRepository customerRepository;

	@Cacheable(BaseConstant.CACHE_CUSTOMER)
	public ResponseEntity<String> list(String keywords) {
		try {
			String keywordSearch = keywords != null ? keywords : "";
			JsonObject argument = Json.createObjectBuilder()
					.add(BaseConstant.KEYWORDS, keywordSearch)
					.build();
			ThrowableFunction f = a -> customerRepository.list(a);
			return serve(f, argument);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@Cacheable(BaseConstant.CACHE_CUSTOMER_ALL)
	public ResponseEntity<String> listAll(String keywords) {
		try {
			String keywordSearch = keywords != null ? keywords : "";
			JsonObject argument = Json.createObjectBuilder()
					.add(BaseConstant.KEYWORDS, keywordSearch)
					.build();
			ThrowableFunction f = a -> customerRepository.listAll(a);
			return serve(f, argument);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	
	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CUSTOMER, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CUSTOMER_ALL, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CUSTOMER_DETAILS, allEntries = true)
	})
	public ResponseEntity<String> create(String id, String firstName, String lastName, String address, String mobilephone, String createBy) {
		int isDeleted = BaseConstant.IS_NOT_DELETED;
		int isActive = BaseConstant.IS_ACTIVED;
		
		String createAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateBy = "system";
		
		JsonObject argument = Json.createObjectBuilder()
				.add(CustomerConstant.ID, id)
				.add(CustomerConstant.FIRST_NAME, firstName)
				.add(CustomerConstant.LAST_NAME, lastName)
				.add(CustomerConstant.ADDRESS, address)
				.add(CustomerConstant.MOBILE_PHONE, mobilephone)
				
				.add(CustomerConstant.IS_DELETED, isDeleted)
				.add(CustomerConstant.IS_ACTIVE, isActive)
				
				.add(CustomerConstant.CREATE_AT, createAt)
				.add(CustomerConstant.CREATE_BY, createBy)
				.add(CustomerConstant.UPDATE_AT, updateAt)
				.add(CustomerConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> customerRepository.create(a);
		return serve(f, argument);
	}
	
	
	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CUSTOMER, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CUSTOMER_ALL, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CUSTOMER_DETAILS, allEntries = true)
	})
	public ResponseEntity<String> update(String id, String firstName, String lastName, String address, String mobilephone, String updateBy, int status) {
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		
		JsonObject argument = Json.createObjectBuilder()
				.add(CustomerConstant.ID, id)
				.add(CustomerConstant.FIRST_NAME, firstName)
				.add(CustomerConstant.LAST_NAME, lastName)
				.add(CustomerConstant.ADDRESS, address)
				.add(CustomerConstant.MOBILE_PHONE, mobilephone)
				
				.add(CustomerConstant.IS_ACTIVE, status)
				
				.add(CustomerConstant.UPDATE_AT, updateAt)
				.add(CustomerConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> customerRepository.update(a);
		return serve(f, argument);
	}

	@Cacheable(BaseConstant.CACHE_CUSTOMER_DETAILS)
	public ResponseEntity<String> details(String id) {
		JsonObjectBuilder argumentBuilder = Json.createObjectBuilder()
				.add(CustomerConstant.ID, id);
		ThrowableFunction f = a -> customerRepository.details(a);
		return serve(f, argumentBuilder.build());
	}
}
