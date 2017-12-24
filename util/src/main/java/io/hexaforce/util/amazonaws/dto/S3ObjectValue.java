package io.hexaforce.util.amazonaws.dto;

import java.io.File;
import java.io.InputStream;

import lombok.Data;

@Data
public class S3ObjectValue {
	private String bucketName;
	private String key;
	private InputStream responseContents;
	// 以下はPUT時にどれか必須
	private File requestByFile;
	private StringBuffer requestByBuffer;
	private InputStream requestByStream;
}
