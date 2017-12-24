package io.hexaforce.util.amazonaws;

import static java.lang.System.out;

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

import io.hexaforce.util.amazonaws.dto.StorageObject;

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
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_NORTHEAST_1)
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
		return createBucket(Arrays.asList(bucketName)).get(0);
	}

	public static List<Bucket> createBucket(List<String> bucketNames) {
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
		deleteBucket(Arrays.asList(bucketName));
	}

	public static void deleteBucket(List<String> bucketNames) {
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
	public static List<Bucket> listBucket() {
		AmazonS3 s3 = buildClient();
		return s3.listBuckets();
	}

	/**
	 * @param bucketName
	 * @param prefix
	 */
	public static List<StorageObject> listObject(String bucketName, String prefix) {
		AmazonS3 s3 = buildClient();
		ListObjectsRequest serchRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
		List<StorageObject> result = new ArrayList<>();
		for (S3ObjectSummary summary : s3.listObjects(serchRequest).getObjectSummaries()) {
			StorageObject o = new StorageObject();
			o.setBucketName(summary.getBucketName());
			o.setKey(summary.getKey());
			result.add(o);
		}
		return result;
	}

	/**
	 * @param value
	 */
	public static StorageObject putObject(StorageObject value) {
		return putObject(Arrays.asList(value)).get(0);
	}

	public static List<StorageObject> putObject(List<StorageObject> values) {
		AmazonS3 s3 = buildClient();

		for (StorageObject v : values) {
			try {
				PutObjectResult s = null;
				if (v.getRequestByStream() != null) {
					s = s3.putObject(v.getBucketName(), v.getKey(), v.getRequestByFile());
				} else if (v.getRequestByStream() != null) {
					s = s3.putObject(v.getBucketName(), v.getKey(), v.getRequestByStream(), new ObjectMetadata());
				} else if (v.getRequestByBuffer() != null) {
					s = s3.putObject(v.getBucketName(), v.getKey(), v.getRequestByBuffer().toString());
				}

				if (s != null) {
					v.setLastModified(s.getMetadata().getLastModified());
					v.setVersionId(s.getMetadata().getVersionId());
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
	public static StorageObject getObject(StorageObject value) {
		return getObject(Arrays.asList(value)).get(0);
	}

	public static List<StorageObject> getObject(List<StorageObject> values) {
		AmazonS3 s3 = buildClient();
		for (StorageObject v : values) {
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
	public static void deleteObject(StorageObject value) {
		deleteObject(Arrays.asList(value));
	}

	public static void deleteObject(List<StorageObject> values) {
		AmazonS3 s3 = buildClient();
		for (StorageObject v : values) {
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
