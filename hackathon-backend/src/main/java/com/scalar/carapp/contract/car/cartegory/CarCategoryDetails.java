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
import javax.json.JsonObject;

public class CarCategoryDetails extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {

		try {
			// validate APIs parameters
			if (!argument.containsKey(CarCategoryConstant.ID)) {
				throw new ContractContextException("a required key is missing: [id]");
			}
			String uuid = argument.getString(CarCategoryConstant.ID);

			// get data by CarCategoryConstant.ASSET_HOLD_ID
			Optional<Asset> response = ledger.get(CarCategoryConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			JsonObject jsonResult = null;
			
			// search details category in list data legger [CarCategoryConstant.ASSET_HOLD_ID]
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
				
				if(jsonTemp != null && jsonTemp.getString(CarCategoryConstant.ID).toString().equals(uuid) && 
						jsonTemp.getInt(CarCategoryConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					jsonResult = jsonTemp;
				}
			}

			// check exist car or not
			if (jsonResult == null) {
				throw new ContractContextException("Car Category does not exist!");
			}
			
			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("data", jsonResult)
					.build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
