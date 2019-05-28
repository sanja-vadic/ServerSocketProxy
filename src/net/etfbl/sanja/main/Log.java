package net.etfbl.sanja.main;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Log {
	private String clientAddress;
	private String requestMethod;
	private String attackType;
}
