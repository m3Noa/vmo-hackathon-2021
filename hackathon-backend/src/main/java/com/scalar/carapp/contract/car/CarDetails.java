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
import javax.json.JsonObject;

public class CarDetails extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {

		try {
			// validate APIs parameters
			if (!argument.containsKey(CarConstant.ID)) {
				throw new ContractContextException("a required key is missing: [id]");
			}
			String uuid = argument.getString(CarConstant.ID);

			// get data by CarCategoryConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CarConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			JsonObject jsonResult = null;
			
			// search details category in list data legger [CarCategoryConstant.ASSET_HOLD_ID]
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
				
				if(jsonTemp != null && jsonTemp.getString(CarConstant.ID).toString().equals(uuid) && 
						jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					JsonObject category = this.getCategoryById(ledger, argument, property, jsonTemp.getString(CarConstant.CATEGORY_ID));
					jsonResult = this.initCarDetails(jsonTemp, category);
				}
			}

			// check exist car or not
			if (jsonResult == null) {
				throw new ContractContextException("Car does not exist!");
			}
			
			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("data", jsonResult)
					.build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
	/**
	 * get category by category Id
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param codeCategory
	 * @return
	 */
	private JsonObject getCategoryById(Ledger ledger, JsonObject argument, Optional<JsonObject> property, String categoryId) {
		JsonObject category = null;
		Optional<Asset> response = ledger.get(CarCategoryConstant.ASSET_HOLD_ID);
		
		if (!response.isPresent()) {
			throw new ContractContextException("Asset of Car Category does not exist!");
		}
		
		JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
			if(jsonTemp != null && jsonTemp.getInt(CarCategoryConstant.IS_DELETED) != BaseConstant.IS_DELETED 
					&& jsonTemp.getString(CarCategoryConstant.ID).equals(categoryId)) {
				category = jsonTemp;
			}
		}
		return category;
	}
	
	/**
	 * 
	 * @param argument
	 * @return
	 */
	private JsonObject initCarDetails(JsonObject dataCar, JsonObject category) {
		try {
			String uuid = dataCar.getString(CarConstant.ID);
			String name = dataCar.getString(CarConstant.NAME);
			String categoryId = dataCar.getString(CarConstant.CATEGORY_ID);
			String description = dataCar.getString(CarConstant.DESCRIPTION);
			String imageUrl = dataCar.getString(CarConstant.IMAGE_URL);
			
			double pricePerDay = Double.parseDouble(dataCar.get(CarConstant.PRICE_PER_DAY).toString());
			int isActive = dataCar.getInt(CarConstant.IS_ACTIVE);
			int rentalStatus = dataCar.getInt(CarConstant.RENTAL_STATUS);
			
			if(category == null) {
				category = Json.createObjectBuilder()
						.add(CarCategoryConstant.CODE, "OTHER")
						.add(CarCategoryConstant.NAME, "OTHER")
						.build();
			}
			
			JsonObject json = Json.createObjectBuilder()
					.add(CarConstant.ID, uuid)
					.add(CarConstant.CATEGORY_ID, categoryId)
					.add(CarConstant.NAME, name)
					.add(CarConstant.DESCRIPTION, description)
					.add(CarConstant.IMAGE_URL, imageUrl)
					.add(CarConstant.PRICE_PER_DAY, pricePerDay)
					.add(CarConstant.IS_ACTIVE, isActive)
					.add(CarConstant.RENTAL_STATUS, rentalStatus)
					.add(CarConstant.CATEGORY, category.asJsonObject())
					
					.add(CarConstant.CREATE_AT, dataCar.getString(CarConstant.CREATE_AT))
					.add(CarConstant.CREATE_BY, dataCar.getString(CarConstant.CREATE_BY))
					
					.add(CarConstant.UPDATE_AT, dataCar.getString(CarConstant.UPDATE_AT))
					.add(CarConstant.UPDATE_BY, dataCar.getString(CarConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
