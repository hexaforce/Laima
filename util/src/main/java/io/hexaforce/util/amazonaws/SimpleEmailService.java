package io.hexaforce.util.amazonaws;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

import io.hexaforce.util.amazonaws.dto.EmailObject;

/**
 * @author T.Tantaka
 *
 */
public class SimpleEmailService {

	/**
	 * @return
	 */
	private static AmazonSimpleEmailService buildClient() {
		/*
		 * Before running the code: Fill in your AWS access credentials in the provided
		 * credentials file template, and be sure to move the file to the default
		 * location (~/.aws/credentials) where the sample code will load the credentials
		 * from. https://console.aws.amazon.com/iam/home?#security_credential
		 *
		 * WARNING: To avoid accidental leakage of your credentials, DO NOT keep the
		 * credentials file in your source directory.
		 */
		try {
			ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider("SES");
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withCredentials(credentialsProvider).withRegion(Regions.AP_NORTHEAST_1).build();
			return client;
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct location (~/.aws/credentials), "
					+ "and is in valid format.", e);
		}

	}

	/**
	 * @param value
	 */
	public static EmailObject sendEmail(EmailObject value) {
		return sendEmail(Arrays.asList(value)).get(0);
	}

	public static List<EmailObject> sendEmail(List<EmailObject> values) {

		AmazonSimpleEmailService client = buildClient();

		for (EmailObject v : values) {

			try {

				// Construct an object to contain the recipient address.
				Destination destination = new Destination().withToAddresses(new String[] { v.getTo() });

				// Create the subject and body of the message.
				Content subject = new Content().withData(v.getSubject());
				Content textBody = new Content().withData(v.getBody());
				Body body = new Body().withText(textBody);

				// Create a message with the specified subject and body.
				Message message = new Message().withSubject(subject).withBody(body);

				// Assemble the email.
				SendEmailRequest request = new SendEmailRequest()
						.withSource(v.getFrom())
						.withDestination(destination)
						.withMessage(message);

				// Send the email.
				SendEmailResult result = client.sendEmail(request);

				v.setResultHttpStatusCode(result.getSdkHttpMetadata().getHttpStatusCode());
				v.setResultMessageId(result.getMessageId());
				v.setResultRequestId(result.getSdkResponseMetadata().getRequestId());
				out.println("Email sent!");

			} catch (Exception ex) {

				out.println("The email was not sent.");
				out.println("Error message: " + ex.getMessage());

			}

		}

		return values;

	}

}
