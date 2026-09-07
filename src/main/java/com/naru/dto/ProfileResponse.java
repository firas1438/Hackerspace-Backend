package com.naru.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

	private Long id;
	private String email;
	private String organizationName;
	private String bio;
	private String logoUrl;
	private String website;
}
