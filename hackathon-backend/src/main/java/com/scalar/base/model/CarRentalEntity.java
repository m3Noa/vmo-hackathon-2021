package com.scalar.base.model;

import java.text.ParseException;
import java.util.Date;

import com.scalar.base.utils.BaseUtils;

public class CarRentalEntity {

	private String id;
	private String code;

	private String carId;
	private String customerId;

	private Date fromDate;
	private Date dueDate;

	// field automation calculator
	private double basePrice;
	private double discount;
	private double tax;
	private double totalPrice;
	
	// status of car rental
	private int status = -1; 

	private String createBy;
	private String updateBy;
	private Date createAt;
	private Date updateAt;

	public CarRentalEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) throws ParseException {
		this.fromDate = BaseUtils.stringToDate(fromDate);
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) throws ParseException {
		this.dueDate = BaseUtils.stringToDate(dueDate);
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
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
