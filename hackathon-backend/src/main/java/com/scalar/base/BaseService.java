package com.scalar.base;

import com.scalar.dl.ledger.model.ContractExecutionResult;
import com.scalar.dl.client.exception.ClientException;

import javax.json.Json;
import javax.json.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BaseService {

	protected ResponseEntity<String> serve(ThrowableFunction f, JsonObject json) {
		try {
			ContractExecutionResult result = f.apply(json);

			return ResponseEntity.ok(result.getResult().isPresent() ? result.getResult().get().toString() : "{}");
		} catch (ClientException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Json.createObjectBuilder()
					.add("status", e.getStatusCode().toString()).add("message", e.getMessage()).build().toString());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@FunctionalInterface
	protected interface ThrowableFunction {
		ContractExecutionResult apply(JsonObject json) throws Exception;
	}
}
