package com.scalar.carapp.constant;

public class CarRentalConstant {
	
	// contract info
	public static final String ASSET_HOLD_ID = "car-rental-asset";
	
	public static final String CONTRACT_CREATE_NEW = "create-car-rental-contract-v200"; 
	public static final String CONTRACT_DETAILS = "details-car-rental-contract-v200";
	public static final String CONTRACT_UPDATE = "update-car-rental-contract"; 
	public static final String CONTRACT_UPDATE_STATUS = "update-car-rental-status-contract";
	public static final String CONTRACT_LIST = "list-car-rental-contract-v200";
	
	// objects associations
	public static final String CUSTOMER = "customer";
	public static final String CAR = "car";
	
	// fields
	public static final String ID = "id";
	
	public static final String CAR_ID = "carId";
	public static final String CUSTOMER_ID = "customerId";
	
	public static final String FROM_DATE = "fromDate";
	public static final String DUE_DATE = "dueDate";
	
	// field automation calculator
	public static final String BASE_PRICE = "basePrice";
	public static final String DISCOUNT = "discount";
	public static final String TAX = "tax";
	public static final String TOTAL_PRICE = "totalPrice";
	public static final String STATUS = "status";

	public static final String CREATE_AT = "createAt";
	public static final String UPDATE_AT = "updateAt";
	public static final String CREATE_BY = "createBy";
	public static final String UPDATE_BY = "updateBy";
	
	/**
	 * STATUS_VALUE
	 * 0: new
	 * 1: started
	 * 2: returned
	 * 3: cancelled
	 */
	public static final int[] STATUS_VALUE = { 0, 1, 2, 3 };
	public static final String DATE_DIFF = "dateDiff";
	public static final String STATUS_NAME = "statusName";
	public static final String RENTAL_DAYS = "rentalDays";
}
