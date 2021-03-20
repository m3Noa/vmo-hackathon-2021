package com.scalar.carapp.repository;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.carapp.constant.CarCategoryConstant;
import com.scalar.dl.client.config.ClientConfig;
import com.scalar.dl.client.service.ClientModule;
import com.scalar.dl.client.service.ClientService;
import com.scalar.dl.ledger.model.ContractExecutionResult;
import java.io.File;
import java.io.IOException;
import javax.inject.Singleton;
import javax.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@Singleton
public class CarCategoryRepository {

	private static final Logger logger = LogManager.getLogger(CarCategoryRepository.class);
	
	private final ClientService clientService;

	public CarCategoryRepository(@Value("${client.properties.path}") String properties) throws IOException {
		Injector injector = Guice.createInjector(new ClientModule(new ClientConfig(new File(properties))));
		this.clientService = injector.getInstance(ClientService.class);
	}

	public ContractExecutionResult create(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarCategoryConstant.CONTRACT_CREATE_NEW, argument);
		logResponse("create-car-category", result);
		return result;
	}
	
	public ContractExecutionResult update(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarCategoryConstant.CONTRACT_UPDATE_CATEGORY, argument);
		logResponse("update-car-category", result);
		return result;
	}
	
	public ContractExecutionResult list(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarCategoryConstant.CONTRACT_LIST_CATEGORY, argument);
		logResponse("list-car-category", result);
		return result;
	}

	public ContractExecutionResult details(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarCategoryConstant.CONTRACT_DETAILS_CATEGORY, argument);
		logResponse("car-category-details", result);
		return result;
	}
	
	public ContractExecutionResult delete(JsonObject argument) {
		ContractExecutionResult result = clientService.executeContract(CarCategoryConstant.CONTRACT_DELETE_CATEGORY, argument);
		logResponse("car-category-delete", result);
		return result;
	}

	private void logResponse(String header, ContractExecutionResult result) {
		logger.info(header + ": (" + (result.getResult().isPresent() ? result.getResult().get() : "{}") + ")");
	}
}
