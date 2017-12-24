package io.hexaforce.util.amazonaws.dto;

import lombok.Data;

@Data
public class QueueObject {
	
	private String queueUrl;
	private String messageBody;
	
	private String messageId;
	private String requestId;
	private String sequenceNumber;
	private int httpStatusCode;

	private String receiptHandle;
	
}
