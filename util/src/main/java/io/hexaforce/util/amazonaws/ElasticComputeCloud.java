package io.hexaforce.util.amazonaws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class ElasticComputeCloud {
	void ggg() {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		RunInstancesRequest run_request = new RunInstancesRequest().withImageId(null)
				.withInstanceType(InstanceType.T1Micro).withMaxCount(1).withMinCount(1);

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String instance_id = run_response.getReservation().getReservationId();
	}
}
