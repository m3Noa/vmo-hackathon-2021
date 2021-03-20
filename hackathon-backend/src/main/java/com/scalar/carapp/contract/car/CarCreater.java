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

public class CarCreater extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CarConstant.CATEGORY_CODE) || !argument.containsKey(CarConstant.NAME) 
					|| !argument.containsKey(CarConstant.DESCRIPTION) || !argument.containsKey(CarConstant.IMAGE_URL) 
					|| !argument.containsKey(CarConstant.PRICE_PER_DAY)) {
				throw new ContractContextException("a required key is missing: [categoryCode] or [name] or [description] or"
						+ " [imageUrl] or [pricePerDay]");
			}

			// validate valid status of category code
			String categoryCode = argument.getString(CarConstant.CATEGORY_CODE);
			String categoryId = this.getCategoryIdByCode(ledger, argument, property, categoryCode);
			if(categoryId.isEmpty()) {
				throw new ContractContextException("Car Category code " + categoryCode + " does not exist!");
			}
			
			// get data of asset_id [CarConstant.ASSET_HOLD_ID]
			Optional<Asset> carData = ledger.get(CarConstant.ASSET_HOLD_ID);
			
			if (carData.isPresent()) {
				return this.putMoreItem(ledger, argument, property, carData, categoryId);
			} else {
				return this.addFirstItem(ledger, argument, property, categoryId);
			}
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
	 * create new asset with name is [CarConstant.ASSET_HOLD_ID] and add first for car item
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return JsonObject
	 */
	private JsonObject addFirstItem(Ledger ledger, JsonObject argument, Optional<JsonObject> property, String categoryId) {
		try {
			JsonArrayBuilder dataListAppliance = Json.createArrayBuilder();
			dataListAppliance.add(this.initCarData(argument, categoryId));

			ledger.put(CarConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, dataListAppliance).build());

			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("message", "Car " + argument.getString(CarConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}

	}

	/**
	 * add more car item into object 'list' of asset [CarConstant.ASSET_HOLD_ID]
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param response
	 * @return JsonObject
	 */
	private JsonObject putMoreItem(Ledger ledger, JsonObject argument, Optional<JsonObject> property,
			Optional<Asset> response, String categoryId) {
		try {
			String name = argument.getString(CarConstant.NAME);

			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			JsonObject jsonByCode = null;

			// check car need unique name
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				if (jsonTemp != null && jsonTemp.getString(CarConstant.NAME).toString().equals(name) && 
						jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					jsonByCode = jsonTemp;
				}
			}

			// check exist car or not
			if (jsonByCode != null) {
				throw new ContractContextException("Car with name " + name + " already exist!");
			}

			// add more car
			JsonArrayBuilder dataListAppliance = Json.createArrayBuilder(jsonArray);
			dataListAppliance.add(this.initCarData(argument, categoryId));

			ledger.put(CarConstant.ASSET_HOLD_ID,
					Json.createObjectBuilder().add(BaseConstant.LIST, dataListAppliance).build());

			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("message", "Car " + argument.getString(CarConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * Initial car data from argument
	 * 
	 * @param argument
	 * @return JsonObject
	 */
	private JsonObject initCarData(JsonObject argument, String categoryId) {
		try {
			String uuid = argument.getString(CarConstant.ID);
			String name = argument.getString(CarConstant.NAME);
			String description = argument.getString(CarConstant.DESCRIPTION);
			String imageUrl = argument.getString(CarConstant.IMAGE_URL);
			
			double pricePerDay = Double.parseDouble(argument.getString(CarConstant.PRICE_PER_DAY));
			int rentalStatus = argument.getInt(CarConstant.RENTAL_STATUS);
			
			int isDeleted = argument.getInt(CarConstant.IS_DELETED);
			int isActive = argument.getInt(CarConstant.IS_ACTIVE);
			
			String createAt = argument.getString(CarConstant.CREATE_AT);
			String createBy = argument.getString(CarConstant.CREATE_BY);
			String updateAt = argument.getString(CarConstant.UPDATE_AT);
			String updateBy = argument.getString(CarConstant.UPDATE_BY);
			
			JsonObject json = Json.createObjectBuilder()
					.add(CarConstant.ID, uuid)
					.add(CarConstant.CATEGORY_ID, categoryId)
					.add(CarConstant.NAME, name)
					.add(CarConstant.DESCRIPTION, description)
					.add(CarConstant.IMAGE_URL, imageUrl)
					.add(CarConstant.PRICE_PER_DAY, pricePerDay)
					.add(CarConstant.RENTAL_STATUS, rentalStatus)
					
					.add(CarConstant.IS_DELETED, isDeleted)
					.add(CarConstant.IS_ACTIVE, isActive)
					
					.add(CarConstant.CREATE_AT, createAt)
					.add(CarConstant.CREATE_BY, createBy)
					.add(CarConstant.UPDATE_AT, updateAt)
					.add(CarConstant.UPDATE_BY, updateBy)
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
}
