package com.scalar.carapp.contract.car;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarCategoryConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class CarUpdater extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CarConstant.CATEGORY_CODE) || !argument.containsKey(CarConstant.NAME) 
					|| !argument.containsKey(CarConstant.DESCRIPTION) || !argument.containsKey(CarConstant.IMAGE_URL) 
					|| !argument.containsKey(CarConstant.PRICE_PER_DAY)) {
				throw new ContractContextException("a required key is missing: [categoryCode] or [name] or [description] or"
						+ " [imageUrl] or [pricePerDay]");
			}

			String uuid = argument.getString(CarConstant.ID);
			String categoryCode = argument.getString(CarConstant.CATEGORY_CODE);
			
			// get data by CarConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CarConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset " + CarConstant.ASSET_HOLD_ID + " does not exist");
			}

			JsonArray dataList = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();

			if (!this.isExistCar(dataList, uuid)) {
				throw new ContractContextException("Car does not exist!");
			}
			
			boolean isValidCodeCategory = true;
			JsonArrayBuilder rs = Json.createArrayBuilder();
			for (int i = 0; i < dataList.size(); i++) {
				JsonObject jsonTemp = dataList.getJsonObject(i).asJsonObject();

				JsonObject car = null;
				if (jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(uuid)) {
					
					// get category code and set to flag status
					String categoryId = this.getCategoryIdByCode(ledger, argument, property, categoryCode);
					if(categoryId == null || categoryId.isEmpty()) {
						isValidCodeCategory = false;
					}else {						
						car = this.getDataUpdate(jsonTemp, argument, categoryId);
					}
				}else if(jsonTemp != null){
					car = this.cloneJsonObject(jsonTemp);
				}
				if(car != null) {					
					rs.add(car);
				}
			}
			
			// if category code invalid
			if(!isValidCodeCategory) {
				throw new ContractContextException("Car Category code does not exist!");
			}
			
			ledger.put(CarConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, rs).build());
			return Json.createObjectBuilder()
					.add("status", "Succeeded")
					.add("message", "Car updated successfully!")
					.build();
			
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
	/**
	 * get categoryId by category code
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param codeCategory
	 * @return
	 */
	private String getCategoryIdByCode(Ledger ledger, JsonObject argument, Optional<JsonObject> property, String codeCategory) {
		String categoryId = "";
		Optional<Asset> response = ledger.get(CarCategoryConstant.ASSET_HOLD_ID);
		
		if (!response.isPresent()) {
			throw new ContractContextException("Asset of Car Category does not exist!");
		}
		
		JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
			if(jsonTemp != null && jsonTemp.getInt(CarCategoryConstant.IS_DELETED) != BaseConstant.IS_DELETED 
					&& jsonTemp.getString(CarCategoryConstant.CODE).equals(codeCategory)) {
				categoryId = jsonTemp.getString(CarCategoryConstant.ID);
			}
		}
		return categoryId;
	}

	/**
	 * check exist of Car
	 * 
	 * @param listData
	 * @param id: ID of Car
	 * @return True/False
	 */
	private boolean isExistCar(JsonArray listData, String id) {
		boolean result = false;

		for (int i = 0; i < listData.size(); i++) {
			JsonObject jsonTemp = listData.getJsonObject(i).asJsonObject();

			if (jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(id) 
					&& jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * update Car: only allow update code, description, imageUrl, pricePerDay, updateAt, updateBy
	 * @param data
	 * @return JsonObject
	 */
	private JsonObject getDataUpdate(JsonObject data, JsonObject argument, String categoryId) {
		try {
			String name = argument.getString(CarConstant.NAME);
			String description = argument.getString(CarConstant.DESCRIPTION);
			String imageUrl = argument.getString(CarConstant.IMAGE_URL);
			double pricePerDay = Double.parseDouble(argument.get(CarConstant.PRICE_PER_DAY).toString());
			
			String updateAt = argument.getString(CarConstant.UPDATE_AT);
			String updateBy = argument.getString(CarConstant.UPDATE_BY);
			
			int rentalStatus = data.getInt(CarConstant.RENTAL_STATUS);
			
			JsonObject json = Json.createObjectBuilder()
					// fields will update
					.add(CarConstant.CATEGORY_ID, categoryId)
					.add(CarConstant.NAME, name)
					.add(CarConstant.DESCRIPTION, description)
					.add(CarConstant.IMAGE_URL, imageUrl)
					.add(CarConstant.PRICE_PER_DAY, pricePerDay)
					.add(CarConstant.RENTAL_STATUS, rentalStatus)
					
					.add(CarConstant.UPDATE_AT, updateAt)
					.add(CarConstant.UPDATE_BY, updateBy)
					
					// clone old data [not update]
					.add(CarConstant.ID, data.getString(CarConstant.ID).toString())
					.add(CarConstant.IS_ACTIVE, data.getInt(CarConstant.IS_ACTIVE))
					.add(CarConstant.IS_DELETED, data.getInt(CarConstant.IS_DELETED))
					
					.add(CarConstant.CREATE_AT, data.getString(CarConstant.CREATE_AT))
					.add(CarConstant.CREATE_BY, data.getString(CarConstant.CREATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
	/**
	 * Clone to new object
	 * @param data
	 * @return
	 */
	public JsonObject cloneJsonObject(JsonObject data) {
		try {
			JsonObject json = Json.createObjectBuilder()
					.add(CarConstant.ID, data.getString(CarConstant.ID))
					.add(CarConstant.CATEGORY_ID, data.getString(CarConstant.CATEGORY_ID))
					.add(CarConstant.NAME, data.getString(CarConstant.NAME))
					.add(CarConstant.DESCRIPTION, data.getString(CarConstant.DESCRIPTION))
					.add(CarConstant.IMAGE_URL, data.getString(CarConstant.IMAGE_URL))
					.add(CarConstant.PRICE_PER_DAY, Double.parseDouble(data.get(CarConstant.PRICE_PER_DAY).toString()))
					.add(CarConstant.RENTAL_STATUS, data.getInt(CarConstant.RENTAL_STATUS))
					
					.add(CarConstant.IS_DELETED, data.getInt(CarCategoryConstant.IS_DELETED))
					.add(CarConstant.IS_ACTIVE, data.getInt(CarCategoryConstant.IS_ACTIVE))
					
					.add(CarCategoryConstant.CREATE_AT, data.getString(CarCategoryConstant.CREATE_AT))
					.add(CarCategoryConstant.CREATE_BY, data.getString(CarCategoryConstant.CREATE_BY))
					.add(CarCategoryConstant.UPDATE_AT, data.getString(CarCategoryConstant.UPDATE_AT))
					.add(CarCategoryConstant.UPDATE_BY, data.getString(CarCategoryConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
