package com.naru.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum City {

	TUNIS("Tunis"),
	ARIANA("Ariana"),
	BEN_AROUS("Ben Arous"),
	MANOUBA("Manouba"),
	NABEUL("Nabeul"),
	ZAGHOUAN("Zaghouan"),
	BIZERTE("Bizerte"),
	BEJA("Béja"),
	JENDOUBA("Jendouba"),
	KEF("Kef"),
	SILIANA("Siliana"),
	SOUSSE("Sousse"),
	MONASTIR("Monastir"),
	MAHDIA("Mahdia"),
	SFAX("Sfax"),
	KAIROUAN("Kairouan"),
	KASSERINE("Kasserine"),
	SIDI_BOUZID("Sidi Bouzid"),
	GABES("Gabès"),
	MEDENINE("Medenine"),
	TATAOUINE("Tataouine"),
	GAFSA("Gafsa"),
	TOZEUR("Tozeur"),
	KEBILI("Kebili");

	private final String displayName;

	private static final Map<String, City> BY_DISPLAY_NAME = Arrays.stream(values())
			.collect(Collectors.toMap(
					city -> city.displayName.toLowerCase(),
					city -> city));

	public static City fromDisplayName(String displayName) {
		City city = BY_DISPLAY_NAME.get(displayName.toLowerCase());
		if (city == null) {
			throw new IllegalArgumentException("Unknown city: " + displayName);
		}
		return city;
	}

	public static boolean isValid(String displayName) {
		if (displayName == null || displayName.isBlank()) {
			return false;
		}
		return BY_DISPLAY_NAME.containsKey(displayName.toLowerCase());
	}
}
