/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.mockwebserver;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author Rob Winch
 */
class MockResponseParser {
	static final String HEADER_PREFIX = "OkHttp-";

	static final String THROTTLE_BODY_HEADER_NAME = HEADER_PREFIX + "ThrottleBody";

	static final String CHUNKED_BODY_HEADER_NAME = HEADER_PREFIX + "ChunkedBody";

	static final String BODY_DELAY_HEADER_NAME = HEADER_PREFIX + "BodyDelay";

	MockResponse createResponse(InputStream in) throws IOException {
		if(in == null) {
			throw new IllegalArgumentException("InputStream cannot be null");
		}
		MockResponse response = new MockResponse();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String status = reader.readLine();
		if(status == null) {
			throw new EOFException("Unexpected EOF. No HTTP Status present");
		}
		response.setStatus(status);

		for(String header = reader.readLine(); !"".equals(header); header = reader.readLine()) {
			if(header == null) {
				throw new EOFException("Unexpected EOF. Expecting an empty line after the HTTP headers");
			}
			response.addHeader(header);
		}

		updateThrottleBody(response);
		updateBodyDelay(response);

		String body = toString(reader);

		Integer maxChunkSize = getMaChunkSize(response);
		if(maxChunkSize == null) {
			response.setBody(body);
		} else {
			response.setChunkedBody(body, maxChunkSize);
		}

		reader.close();
		return response;
	}

	private void updateBodyDelay(MockResponse response) {
		String bodyDelayHeaderValue = response.getHeaders().get(BODY_DELAY_HEADER_NAME);
		if(bodyDelayHeaderValue != null) {
			response.removeHeader(BODY_DELAY_HEADER_NAME);
			try {
				setBodyDelay(response, bodyDelayHeaderValue);
			} catch(RuntimeException e) {
				throw new IllegalArgumentException(BODY_DELAY_HEADER_NAME + " expected to be in format <long delay>;<TimeUnit unit> but got '" + bodyDelayHeaderValue+"'", e);
			}
		}
	}

	private void updateThrottleBody(MockResponse response) {
		String throttleBodyHeaderValue = response.getHeaders().get(THROTTLE_BODY_HEADER_NAME);
		if(throttleBodyHeaderValue != null) {
			response.removeHeader(THROTTLE_BODY_HEADER_NAME);
			try {
				setThrottleBody(response, throttleBodyHeaderValue);
			} catch(RuntimeException e) {
				throw new IllegalArgumentException(THROTTLE_BODY_HEADER_NAME + " expected to be in format <long bytesPerPeriod>;<long period>;<TimeUnit unit> but got '" + throttleBodyHeaderValue+"'", e);
			}
		}
	}

	private Integer getMaChunkSize(MockResponse response) {
		Integer maxChunkSize =  null;
		String chunkedHeadersHeaderValue = response.getHeaders().get(CHUNKED_BODY_HEADER_NAME);
		if(chunkedHeadersHeaderValue != null) {
			response.removeHeader(CHUNKED_BODY_HEADER_NAME);
			try {
				maxChunkSize = Integer.parseInt(chunkedHeadersHeaderValue);
			} catch(RuntimeException e) {
				throw new IllegalArgumentException(CHUNKED_BODY_HEADER_NAME + " expected to be in format <int maxChunkSize> but got '" + chunkedHeadersHeaderValue+"'", e);
			}
		}
		return maxChunkSize;
	}

	private void setThrottleBody(MockResponse response, String throttleBodyHeaderValue) {
		String parts[] = throttleBodyHeaderValue.split(";");
		for(int i = 0;i < parts.length;i++) {
			parts[i] = parts[i].trim();
		}

		long bytesPerPeriod = Long.parseLong(parts[0]);
		long period = Long.parseLong(parts[1]);
		TimeUnit unit = TimeUnit.valueOf(parts[2]);

		response.throttleBody(bytesPerPeriod, period, unit);
	}

	private void setBodyDelay(MockResponse response, String bodyDelayHeaderValue) {
		String parts[] = bodyDelayHeaderValue.split(";");
		for(int i = 0;i < parts.length;i++) {
			parts[i] = parts[i].trim();
		}

		long delay = Long.parseLong(parts[0]);
		TimeUnit unit = TimeUnit.valueOf(parts[1]);

		response.setBodyDelay(delay, unit);
	}

	private String toString(Reader input) throws IOException {
		char[] buffer = new char[1024 * 4];
		StringWriter output = new StringWriter();
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toString();
	}
}
