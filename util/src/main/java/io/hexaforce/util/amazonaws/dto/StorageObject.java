package io.hexaforce.util.amazonaws.dto;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import lombok.Data;

@Data
public class StorageObject {
	
	private String bucketName;
	private String key;

	private File requestByFile;
	private StringBuffer requestByBuffer;
	private InputStream requestByStream;
	
	private InputStream responseContents;
	
	private Date lastModified;
	private String versionId;
	
}
