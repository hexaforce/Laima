package io.hexaforce.util.amazonaws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import io.hexaforce.util.amazonaws.dto.S3ObjectValue;
import static java.lang.System.out;

/**
 * @author T.Tantaka
 *
 */
public class SimpleStorageService {

	/**
	 * @return
	 */
	private static AmazonS3 buildClient() {
		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at (~/.aws/credentials).
		 */
		try {
			AWSCredentials credentials = new ProfileCredentialsProvider("S3").getCredentials();
			AmazonS3 s3 = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_WEST_2)
					.build();
			return s3;
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct location (~/.aws/credentials), and is in valid format.",
					e);
		}

	}

	/**
	 * @param bucketName
	 * @return Bucket
	 */
	public static Bucket createBucket(String bucketName) {
		return createBuckets(Arrays.asList(bucketName)).get(0);
	}

	public static List<Bucket> createBuckets(List<String> bucketNames) {
		AmazonS3 s3 = buildClient();
		List<Bucket> bucketList = new ArrayList<>();
		for (String bucketName : bucketNames) {
			try {
				bucketList.add(s3.createBucket(bucketName));
			} catch (Exception e) {
				displayException(e);
			}
		}
		return bucketList;
	}

	/**
	 * @param bucketName
	 */
	public static void deleteBucket(String bucketName) {
		deleteBuckets(Arrays.asList(bucketName));
	}

	public static void deleteBuckets(List<String> bucketNames) {
		AmazonS3 s3 = buildClient();
		for (String bucketName : bucketNames) {
			try {
				s3.deleteBucket(bucketName);
			} catch (Exception e) {
				displayException(e);
			}
		}
	}

	/**
	 * @return
	 */
	public static List<Bucket> listBuckets() {
		AmazonS3 s3 = buildClient();
		return s3.listBuckets();
	}

	/**
	 * @param bucketName
	 * @param prefix
	 */
	public static List<S3ObjectValue> listObjects(String bucketName, String prefix) {
		AmazonS3 s3 = buildClient();
		ListObjectsRequest serchRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
		List<S3ObjectValue> result = new ArrayList<>();
		for (S3ObjectSummary summary : s3.listObjects(serchRequest).getObjectSummaries()) {
			S3ObjectValue o = new S3ObjectValue();
			o.setBucketName(summary.getBucketName());
			o.setKey(summary.getKey());
			result.add(o);
		}
		return result;
	}

	/**
	 * @param value
	 */
	public static S3ObjectValue putObject(S3ObjectValue value) {
		return putObjects(Arrays.asList(value)).get(0);
	}

	public static List<S3ObjectValue> putObjects(List<S3ObjectValue> values) {
		AmazonS3 s3 = buildClient();
		for (S3ObjectValue v : values) {
			try {
				PutObjectResult s = null;
				if (v.getRequestByStream() != null) {
					s = s3.putObject(v.getBucketName(), v.getKey(), v.getRequestByFile());
				} else if (v.getRequestByStream() != null) {
					s = s3.putObject(v.getBucketName(), v.getKey(), v.getRequestByStream(), new ObjectMetadata());
				} else if (v.getRequestByBuffer() != null) {
					s = s3.putObject(v.getBucketName(), v.getKey(), v.getRequestByBuffer().toString());
				}
				if (s == null) {
					values.remove(v);
				}
			} catch (Exception e) {
				displayException(e);
			}
		}
		return values;
	}

	/**
	 * @param value
	 * @return
	 */
	public static S3ObjectValue getObject(S3ObjectValue value) {
		return getObjects(Arrays.asList(value)).get(0);
	}

	public static List<S3ObjectValue> getObjects(List<S3ObjectValue> values) {
		AmazonS3 s3 = buildClient();
		for (S3ObjectValue v : values) {
			try {
				v.setResponseContents(s3.getObject(v.getBucketName(), v.getKey()).getObjectContent());
			} catch (Exception e) {
				displayException(e);
			}
		}
		return values;
	}

	/**
	 * @param value
	 */
	public static void deleteObject(S3ObjectValue value) {
		deleteObjects(Arrays.asList(value));
	}

	public static void deleteObjects(List<S3ObjectValue> values) {
		AmazonS3 s3 = buildClient();
		for (S3ObjectValue v : values) {
			try {
				s3.deleteObject(v.getBucketName(), v.getKey());
			} catch (Exception e) {
				displayException(e);
			}
		}

	}

	/**
	 * @param e
	 * @throws IOException
	 */
	private static void displayException(Exception e) {

		if (e instanceof AmazonServiceException) {
			AmazonServiceException ase = (AmazonServiceException) e;
			out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, "
					+ "but was rejected with an error response for some reason.");
			out.println("Error Message:    " + ase.getMessage());
			out.println("HTTP Status Code: " + ase.getStatusCode());
			out.println("AWS Error Code:   " + ase.getErrorCode());
			out.println("Error Type:       " + ase.getErrorType());
			out.println("Request ID:       " + ase.getRequestId());
		} else if (e instanceof AmazonClientException) {
			AmazonClientException ace = (AmazonClientException) e;
			out.println("Caught an AmazonClientException, "
					+ "which means the client encountered a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			out.println("Error Message: " + ace.getMessage());
		} else {
			e.printStackTrace();
		}

	}

	/**
	 * Displays the contents of the specified input stream as text.
	 *
	 * @param input
	 *            The input stream to display as text.
	 *
	 * @throws IOException
	 */
	private static void displayTextInputStream(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			out.println("    " + line);
		}
		out.println();
	}

}
