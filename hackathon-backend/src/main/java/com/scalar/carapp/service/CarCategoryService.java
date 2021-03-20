package com.scalar.carapp.service;

import com.scalar.base.BaseConstant;
import com.scalar.base.BaseService;
import com.scalar.base.utils.BaseUtils;
import com.scalar.carapp.constant.CarCategoryConstant;
import com.scalar.carapp.repository.CarCategoryRepository;

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
public class CarCategoryService extends BaseService{

	@Autowired
	private CarCategoryRepository carCategoryRepository;

	@Cacheable(BaseConstant.CACHE_CAR_CATEGORY)
	public ResponseEntity<String> list(String keywords) {
		try {
			String keywordSearch = keywords != null ? keywords : "";
			JsonObject argument = Json.createObjectBuilder()
					.add(BaseConstant.KEYWORDS, keywordSearch)
					.build();
			ThrowableFunction f = a -> carCategoryRepository.list(a);
			return serve(f, argument);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@CacheEvict(value= BaseConstant.CACHE_CAR_CATEGORY, allEntries = true)
	public ResponseEntity<String> create(String id, String code, String name, String createBy) {
		int isDeleted = BaseConstant.IS_NOT_DELETED;
		int isActive = BaseConstant.IS_ACTIVED;
		
		String createAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		String updateBy = "system";
		
		JsonObject argument = Json.createObjectBuilder()
				.add(CarCategoryConstant.ID, id)
				.add(CarCategoryConstant.CODE, code)
				.add(CarCategoryConstant.NAME, name)
				
				.add(CarCategoryConstant.IS_DELETED, isDeleted)
				.add(CarCategoryConstant.IS_ACTIVE, isActive)
				
				.add(CarCategoryConstant.CREATE_AT, createAt)
				.add(CarCategoryConstant.CREATE_BY, createBy)
				.add(CarCategoryConstant.UPDATE_AT, updateAt)
				.add(CarCategoryConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> carCategoryRepository.create(a);
		return serve(f, argument);
	}
	
	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CAR_CATEGORY_DETAIL, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CAR_CATEGORY, allEntries = true)
	})
	public ResponseEntity<String> update(String id, String code, String name, String updateBy) {
		
		String updateAt = BaseUtils.dateToStringFormat(new Date(), BaseConstant.DATE_FORMAT);
		
		JsonObject argument = Json.createObjectBuilder()
				.add(CarCategoryConstant.ID, id)
				.add(CarCategoryConstant.CODE, code)
				.add(CarCategoryConstant.NAME, name)
				
				.add(CarCategoryConstant.UPDATE_AT, updateAt)
				.add(CarCategoryConstant.UPDATE_BY, updateBy)
				.build();
		ThrowableFunction f = a -> carCategoryRepository.update(a);
		return serve(f, argument);
	}

	@Cacheable(BaseConstant.CACHE_CAR_CATEGORY_DETAIL)
	public ResponseEntity<String> details(String id) {
		JsonObjectBuilder argumentBuilder = Json.createObjectBuilder()
				.add(CarCategoryConstant.ID, id);
		ThrowableFunction f = a -> carCategoryRepository.details(a);
		return serve(f, argumentBuilder.build());
	}
	
	@Caching(evict = {
		    @CacheEvict(value = BaseConstant.CACHE_CAR_CATEGORY_DETAIL, allEntries = true),
		    @CacheEvict(value = BaseConstant.CACHE_CAR_CATEGORY, allEntries = true)
	})
	public ResponseEntity<String> delete(String id) {
		int isDeleted = BaseConstant.IS_DELETED;
		
		JsonObjectBuilder argumentBuilder = Json.createObjectBuilder()
				.add(CarCategoryConstant.ID, id)
				.add(CarCategoryConstant.IS_DELETED, isDeleted);
		ThrowableFunction f = a -> carCategoryRepository.delete(a);
		return serve(f, argumentBuilder.build());
	}
}
