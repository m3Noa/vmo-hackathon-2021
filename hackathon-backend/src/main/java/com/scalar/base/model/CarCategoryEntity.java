package com.scalar.base.model;

import java.text.ParseException;
import java.util.Date;

import com.scalar.base.utils.BaseUtils;

public class CarCategoryEntity {

	private String id;
	private String code;
	private String name;
	
	private int isDeleted;
	private int isActive;
	private String createBy;
	private String updateBy;
	private Date createAt;
	private Date updateAt;

	public CarCategoryEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) throws ParseException {
		this.createAt = BaseUtils.stringToDate(createAt);
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(String updateAt) throws ParseException {
		this.updateAt = BaseUtils.stringToDate(updateAt);
	}

}
