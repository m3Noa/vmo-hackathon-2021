package com.scalar.carapp.contract.car;

import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CarConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.database.Ledger;
import com.scalar.dl.ledger.exception.ContractContextException;

public class CarGetList extends Contract {

	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {
		try {
			// get Asset by [CarConstant.ASSET_HOLD_ID]
			Optional<Asset> response = ledger.get(CarConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			
			String status = argument.getString(BaseConstant.STATUS);
			
			JsonArrayBuilder rs = Json.createArrayBuilder();
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();

				JsonObject car = null;
				if(jsonTemp != null && jsonTemp.getInt(CarConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					car = this.cloneJsonObject(jsonTemp);
					
					// check by status
					switch (status) {
						case CarConstant.ANY_STATUS:
							rs.add(car);
							break;
						case CarConstant.AVAILABLE_STATUS:
							if(car.getInt(CarConstant.RENTAL_STATUS) == CarConstant.NOT_RENTAL) {
								rs.add(car);
							}
							break;
						case CarConstant.RENTED_STATUS:
							if(car.getInt(CarConstant.RENTAL_STATUS) == CarConstant.RENTED) {
								rs.add(car);
							}
						break;
						default:
							rs.add(car);
							break;
					}
				}
			}
			
			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("data", rs)
					.build();
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
					
					.add(CarConstant.CREATE_AT, data.getString(CarConstant.CREATE_AT))
					.add(CarConstant.CREATE_BY, data.getString(CarConstant.CREATE_BY))
					
					.add(CarConstant.UPDATE_AT, data.getString(CarConstant.UPDATE_AT))
					.add(CarConstant.UPDATE_BY, data.getString(CarConstant.UPDATE_BY))
					.build();
			return json;
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
