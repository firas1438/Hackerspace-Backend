package com.naru.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventFilterOptionsResponse {

	private List<String> cities;
	private List<String> tags;
}
