package com.scalar.carapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scalar.base.model.StatusSyncData;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/api/data-status")
public class StatusSyncController {

	@GetMapping
	public ResponseEntity<String> list() {
		return ResponseEntity.status(HttpStatus.OK).body(StatusSyncData.statusId);
	}
}
