package com.scalar.base.model;

import java.text.ParseException;
import java.util.Date;

import com.scalar.base.utils.BaseUtils;

public class CarEntity {
	
	private String categoryCode;

	private String id;
	private String categoryId;
	private String name;
	private String description;
	private String imageUrl;

	private double pricePerDay;
	private int rentalStatus;
	private int isDeleted;
	private int isActive;

	private String createBy;
	private String updateBy;
	private Date createAt;
	private Date updateAt;

	public CarEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getRentalStatus() {
		return rentalStatus;
	}

	public void setRentalStatus(int rentalStatus) {
		this.rentalStatus = rentalStatus;
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

	public double getPricePerDay() {
		return pricePerDay;
	}

	public void setPricePerDay(double pricePerDay) {
		this.pricePerDay = pricePerDay;
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
