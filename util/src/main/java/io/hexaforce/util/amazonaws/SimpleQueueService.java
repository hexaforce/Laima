package io.hexaforce.util.amazonaws;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import io.hexaforce.util.amazonaws.dto.QueueObject;

/**
 * 
 * @author T.Tantaka
 * 
 */
public class SimpleQueueService {

	/**
	 * @return
	 */
	private static AmazonSQS buildClient() {
		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at (~/.aws/credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider("SQS");
		try {
			credentialsProvider.getCredentials();
			AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(credentialsProvider)
					.withRegion(Regions.AP_NORTHEAST_1).build();
			return sqs;
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct location (~/.aws/credentials), and is in valid format.",
					e);
		}
	}

	public static void createQueue() {

	}

	public static void createQueues() {
		AmazonSQS sqs = buildClient();

	}

	public static void deleteQueue() {

	}

	public static void deleteQueues() {
		AmazonSQS sqs = buildClient();

	}

	public static List<String> listQueueUrls() {
		AmazonSQS sqs = buildClient();
		return sqs.listQueues().getQueueUrls();
	}

	public static void sendMessage() {

	}

	public static List<QueueObject> sendMessage(List<QueueObject> values) {
		AmazonSQS sqs = buildClient();
		for (QueueObject v : values) {
			SendMessageResult result = sqs.sendMessage(v.getQueueUrl(), v.getMessageBody());
			v.setMessageId(result.getMessageId());
			v.setSequenceNumber(result.getSequenceNumber());
			v.setHttpStatusCode(result.getSdkHttpMetadata().getHttpStatusCode());
		}
		return values;
	}

	public static List<QueueObject> receiveMessage(String queueUrl) {
		AmazonSQS sqs = buildClient();
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		List<QueueObject> queueObjectList = new ArrayList<>();
		for (Message m : messages) {
			QueueObject queueObject = new QueueObject();
			queueObject.setQueueUrl(queueUrl);
			queueObject.setMessageId(m.getMessageId());
			queueObject.setMessageBody(m.getBody());
			queueObject.setReceiptHandle(m.getReceiptHandle());
			queueObjectList.add(queueObject);
		}
		return queueObjectList;
	}

	public static void deleteMessage() {

	}

	public static List<QueueObject> deleteMessage(List<QueueObject> values) {
		AmazonSQS sqs = buildClient();
		for (QueueObject v : values) {
			DeleteMessageResult result = sqs.deleteMessage(v.getQueueUrl(), v.getReceiptHandle());
			v.setRequestId(result.getSdkResponseMetadata().getRequestId());
			v.setHttpStatusCode(result.getSdkHttpMetadata().getHttpStatusCode());
		}
		return values;
	}

	public static void main(String[] args) throws Exception {

		AmazonSQS sqs = buildClient();

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		try {
			// Create a queue
			System.out.println("Creating a new SQS queue called MyQueue.\n");
			CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
			String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();

			// Send a message
			System.out.println("Sending a message to MyQueue.\n");
			sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));

			// Receive messages
			System.out.println("Receiving messages from MyQueue.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				System.out.println("  Message");
				System.out.println("    MessageId:     " + message.getMessageId());
				System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
				System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
				System.out.println("    Body:          " + message.getBody());
				for (Entry<String, String> entry : message.getAttributes().entrySet()) {
					System.out.println("  Attribute");
					System.out.println("    Name:  " + entry.getKey());
					System.out.println("    Value: " + entry.getValue());
				}
			}
			System.out.println();

			// Delete a message
			System.out.println("Deleting a message.\n");
			String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));

			// Delete a queue
			System.out.println("Deleting the test queue.\n");
			sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	private static void displayException(Exception e) {

		if (e instanceof AmazonServiceException) {
			AmazonServiceException ase = (AmazonServiceException) e;
			out.println("Caught an AmazonServiceException, which means your request made it to Amazon SQS, "
					+ "but was rejected with an error response for some reason.");
			out.println("Error Message:    " + ase.getMessage());
			out.println("HTTP Status Code: " + ase.getStatusCode());
			out.println("AWS Error Code:   " + ase.getErrorCode());
			out.println("Error Type:       " + ase.getErrorType());
			out.println("Request ID:       " + ase.getRequestId());

		} else if (e instanceof AmazonClientException) {
			AmazonClientException ace = (AmazonClientException) e;
			out.println("Caught an AmazonClientException, "
					+ "which means the client encountered a serious internal problem while trying to communicate with SQS, "
					+ "such as not being able to access the network.");
			out.println("Error Message: " + ace.getMessage());
		} else {
			e.printStackTrace();
		}

	}

}
