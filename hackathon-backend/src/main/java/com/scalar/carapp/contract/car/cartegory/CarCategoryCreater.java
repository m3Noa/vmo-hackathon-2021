package com.scalar.carapp.contract.car.cartegory;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarCategoryConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;

import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

public class CarCategoryCreater extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			if (!argument.containsKey(CarCategoryConstant.CODE) || !argument.containsKey(CarCategoryConstant.NAME)) {
				throw new ContractContextException("a required key is missing: [code] or [name]");
			}

			// get data of asset_id [CarCategoryConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarCategoryConstant.ASSET_HOLD_ID);

			if (response.isPresent()) {
				return this.putCarCategory(ledger, argument, property, response);
			} else {
				return this.addFirstCarCategory(ledger, argument, property);
			}
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * create new asset with name is [CarCategoryConstant.ASSET_HOLD_ID] and add first for car category item
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @return JsonObject
	 */
	private JsonObject addFirstCarCategory(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			JsonArrayBuilder dataListAppliance = Json.createArrayBuilder();
			dataListAppliance.add(this.initCarCategoryData(argument));

			ledger.put(CarCategoryConstant.ASSET_HOLD_ID, Json.createObjectBuilder().add(BaseConstant.LIST, dataListAppliance).build());

			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("message", "Car Category " + argument.getString(CarCategoryConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}

	}

	/**
	 * add more car category item into object 'list' of asset [CarCategoryConstant.ASSET_HOLD_ID]
	 * 
	 * @param ledger
	 * @param argument
	 * @param property
	 * @param response
	 * @return JsonObject
	 */
	private JsonObject putCarCategory(Ledger ledger, JsonObject argument, Optional<JsonObject> property,
			Optional<Asset> response) {
		try {
			String code = argument.getString(CarCategoryConstant.CODE);

			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			JsonObject jsonByCode = null;

			// check category need unique code
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				if (jsonTemp != null && jsonTemp.getString(CarCategoryConstant.CODE).toString().equals(code) && 
						jsonTemp.getInt(CarCategoryConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					jsonByCode = jsonTemp;
				}
			}

			// check exist category or not
			if (jsonByCode != null) {
				throw new ContractContextException("Car Category with code " + code + " already exist!");
			}

			// add more category
			JsonArrayBuilder dataListAppliance = Json.createArrayBuilder(jsonArray);
			dataListAppliance.add(this.initCarCategoryData(argument));

			ledger.put(CarCategoryConstant.ASSET_HOLD_ID,
					Json.createObjectBuilder().add(BaseConstant.LIST, dataListAppliance).build());

			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("message", "Car Category " + argument.getString(CarCategoryConstant.ID) + " created").build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}

	/**
	 * Initial car category data from argument
	 * 
	 * @param argument
	 * @return JsonObject
	 */
	private JsonObject initCarCategoryData(JsonObject argument) {
		try {
			String uuid = argument.getString(CarCategoryConstant.ID);
			String code = argument.getString(CarCategoryConstant.CODE);
			String name = argument.getString(CarCategoryConstant.NAME);
			
			int isDeleted = argument.getInt(CarCategoryConstant.IS_DELETED);
			int isActive = argument.getInt(CarCategoryConstant.IS_ACTIVE);
			
			String createAt = argument.getString(CarCategoryConstant.CREATE_AT);
			String createBy = argument.getString(CarCategoryConstant.CREATE_BY);
			String updateAt = argument.getString(CarCategoryConstant.UPDATE_AT);
			String updateBy = argument.getString(CarCategoryConstant.UPDATE_BY);
			
			JsonObject json = Json.createObjectBuilder()
					.add(CarCategoryConstant.ID, uuid)
					.add(CarCategoryConstant.CODE, code)
					.add(CarCategoryConstant.NAME, name)
					
					.add(CarCategoryConstant.IS_DELETED, isDeleted)
					.add(CarCategoryConstant.IS_ACTIVE, isActive)
					
					.add(CarCategoryConstant.CREATE_BY, createBy)
					.add(CarCategoryConstant.UPDATE_BY, updateBy)
					.add(CarCategoryConstant.CREATE_AT, createAt)
					.add(CarCategoryConstant.UPDATE_AT, updateAt).build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
}
