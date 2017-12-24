package io.hexaforce.util.amazonaws;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;

public class CloudWatchLogs {
	
	void aaa() {
		final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();

		boolean done = false;

		while (!done) {
			ListMetricsRequest request = new ListMetricsRequest().withMetricName("name").withNamespace("namespace");

			ListMetricsResult response = cw.listMetrics(request);

			for (Metric metric : response.getMetrics()) {
				System.out.printf("Retrieved metric %s", metric.getMetricName());
			}

			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
}
