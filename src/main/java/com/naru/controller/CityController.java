package com.naru.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naru.enums.City;

@RestController
@RequestMapping("/cities")
public class CityController {

	@GetMapping
	public List<String> getAllCities() {
		return Arrays.stream(City.values())
				.map(City::getDisplayName)
				.toList();
	}
}
