package com.scalar.carapp.constant;

public class CarConstant {
	
	// contract info
	public static final String ASSET_HOLD_ID = "cars-asset";
	
	public static final String CONTRACT_CREATE_NEW = "create-car-contract-v200";
	public static final String CONTRACT_DETAILS = "details-car-contract-v200";
	public static final String CONTRACT_LIST = "list-car-contract-v200";
	public static final String CONTRACT_DELETE = "delete-car-contract";
	public static final String CONTRACT_UPDATE = "update-car-contract";
	public static final String CONTRACT_LIST_AVAILABLE = "list-available-car-contract-v200";
	
	// object associations
	public static final String CATEGORY = "category";

	// fields
	public static final String ID = "id";
	
	public static final String CATEGORY_ID = "categoryId";
	public static final String CATEGORY_CODE = "categoryCode";
	
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String IMAGE_URL = "imageUrl";
	
	public static final String PRICE_PER_DAY = "pricePerDay";
	public static final String RENTAL_STATUS = "rentalStatus";

	public static final String IS_ACTIVE = "isActive";
	public static final String IS_DELETED = "isDeleted";

	public static final String CREATE_AT = "createAt";
	public static final String UPDATE_AT = "updateAt";
	public static final String CREATE_BY = "createBy";
	public static final String UPDATE_BY = "updateBy";
	
	public static final int NOT_RENTAL = 0;
	public static final int RENTED = 1;
	
	public static final String RENTED_STATUS = "rented";
	public static final String AVAILABLE_STATUS = "available";
	public static final String ANY_STATUS = "all";
}
