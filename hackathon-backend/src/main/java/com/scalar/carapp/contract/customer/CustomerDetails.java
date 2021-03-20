package com.scalar.carapp.contract.customer;

import com.scalar.base.BaseConstant;
import com.scalar.carapp.constant.CustomerConstant;
import com.scalar.dl.ledger.asset.Asset;
import com.scalar.dl.ledger.contract.Contract;
import com.scalar.dl.ledger.exception.ContractContextException;
import com.scalar.dl.ledger.database.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class CustomerDetails extends Contract {
	
	@Override
	public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> property) {

		try {
			// validate APIs parameters
			if (!argument.containsKey(CustomerConstant.ID)) {
				throw new ContractContextException("a required key is missing: [id]");
			}
			String uuid = argument.getString(CustomerConstant.ID);

			// get data by APPLIANCE_HOLD_ID
			Optional<Asset> response = ledger.get(CustomerConstant.ASSET_HOLD_ID);
			if (!response.isPresent()) {
				throw new ContractContextException("Asset does not exist!");
			}
			
			JsonArray jsonArray = response.get().data().getJsonArray(BaseConstant.LIST).asJsonArray();
			JsonObject jsonResult = null;
			
			// search details category in list data legger [CustomerConstant.ASSET_HOLD_ID]
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonTemp = jsonArray.getJsonObject(i).asJsonObject();
				
				if(jsonTemp != null && jsonTemp.getString(CustomerConstant.ID).toString().equals(uuid) && 
						jsonTemp.getInt(CustomerConstant.IS_DELETED) != BaseConstant.IS_DELETED) {
					jsonResult = jsonTemp;
				}
			}

			// check exist car or not
			if (jsonResult == null) {
				throw new ContractContextException("Customer does not exist!");
			}
			
			return Json.createObjectBuilder().add("status", "Succeeded")
					.add("data", jsonResult)
					.build();
		} catch (Exception e) {
			throw new ContractContextException(e.getMessage());
		}
	}
	
}
