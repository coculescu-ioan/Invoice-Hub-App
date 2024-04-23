package com.example.backend.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@Getter
@ConfigurationProperties("storage")
public class StorageProperties {

	private String location = "src/main/resources/files/";

}
