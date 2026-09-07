package com.naru.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

	@Size(max = 160)
	private String organizationName;

	@Size(max = 2000)
	private String bio;

	@Size(max = 500)
	private String logoUrl;

	@Size(max = 300)
	private String website;
}
