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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Rob Winch
 *
 */
public class MockResponseParserTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

	MockResponseParser parser;

	@Before
	public void setup() {
		parser = new MockResponseParser();
	}

	@Test
	public void statusOk() throws IOException {
		MockResponse response = parser.createResponse(input("statusOk"));

		assertEquals("HTTP/1.1 200 OK", response.getStatus());
	}

	@Test
	public void statusInternalServerError() throws IOException {
		MockResponse response = parser.createResponse(input("statusInternalServerError"));

		assertEquals("HTTP/1.1 500 Internal Server Error", response.getStatus());
	}

	@Test
	public void headerContentType() throws IOException {
		MockResponse response = parser.createResponse(input("headerContentType"));

		assertEquals(2, response.getHeaders().size());
		assertEquals("0", response.getHeaders().get("Content-Length"));
		assertEquals("application/json", response.getHeaders().get("Content-Type"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void nullInputStream() throws IOException {
		parser.createResponse(null);
	}

	@Test(expected=EOFException.class)
	public void empty() throws IOException {
		parser.createResponse(input("empty"));
	}

	@Test(expected=EOFException.class)
	public void onlyStatus() throws IOException {
		parser.createResponse(input("onlyStatus"));
	}

	@Test(expected=EOFException.class)
	public void noEmptyLine() throws IOException {
		parser.createResponse(input("noEmptyLine"));
	}

	@Test
	public void body() throws IOException {
		MockResponse response = parser.createResponse(input("body"));

		assertEquals(1, response.getHeaders().size());
		assertEquals("Hi", response.getBody().readByteString().utf8());
	}

	@Test
	public void bodyEndsNewLine() throws IOException {
		MockResponse response = parser.createResponse(input("bodyEndsNewLine"));

		assertEquals(1, response.getHeaders().size());
		assertEquals("Hi\n", response.getBody().readByteString().utf8());
	}

	@Test
	public void noBody() throws IOException {
		MockResponse response = parser.createResponse(input("noBody"));

		assertEquals(1, response.getHeaders().size());
		assertEquals("", response.getBody().readByteString().utf8());
	}

	@Test
	public void bodyMultiLine() throws IOException {
		MockResponse response = parser.createResponse(input("bodyMultiLine"));

		assertEquals(1, response.getHeaders().size());
		assertEquals("Hi\nThere", response.getBody().readByteString().utf8());
	}

	@Test
	public void throttleBody() throws IOException {
		MockResponse response = parser.createResponse(input("throttleBody"));

		assertNull(response.getHeaders().get(MockResponseParser.THROTTLE_BODY_HEADER_NAME));
		assertEquals(12345L, response.getThrottleBytesPerPeriod());
		assertEquals(67890L, response.getThrottlePeriod(TimeUnit.SECONDS));
	}

	@Test
	public void throttleBodyWithWhiteSpace() throws IOException {
		MockResponse response = parser.createResponse(input("throttleBodyWithWhiteSpace"));

		assertEquals(12345L, response.getThrottleBytesPerPeriod());
		assertEquals(67890L, response.getThrottlePeriod(TimeUnit.SECONDS));
	}

	@Test
	public void throttleBodyInvalidFormatMissingPart() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-ThrottleBody expected to be in format <long bytesPerPeriod>;<long period>;<TimeUnit unit> but got '12345;67890'");

		parser.createResponse(input("throttleBodyInvalidFormatMissingPart"));
	}

	@Test
	public void throttleBodyInvalidFormatBytesPerPeriodNotLong() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-ThrottleBody expected to be in format <long bytesPerPeriod>;<long period>;<TimeUnit unit> but got 'invalid;67890;SECONDS'");

		parser.createResponse(input("throttleBodyInvalidFormatBytesPerPeriodNotLong"));
	}

	@Test
	public void throttleBodyInvalidFormatPeriodNotLong() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-ThrottleBody expected to be in format <long bytesPerPeriod>;<long period>;<TimeUnit unit> but got '12345;invalid;SECONDS'");

		parser.createResponse(input("throttleBodyInvalidFormatPeriodNotLong"));
	}

	@Test
	public void throttleBodyInvalidFormatInvalidUnit() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-ThrottleBody expected to be in format <long bytesPerPeriod>;<long period>;<TimeUnit unit> but got '12345;67890;invalid'");

		parser.createResponse(input("throttleBodyInvalidFormatInvalidUnit"));
	}

	@Test
	public void chunkedBody() throws IOException {
		MockResponse expected = new MockResponse();
		expected.setChunkedBody("This is a test for chunking the body.", 5);
		MockResponse response = parser.createResponse(input("chunkedBody"));

		assertNull(response.getHeaders().get(MockResponseParser.CHUNKED_BODY_HEADER_NAME));
		assertEquals(expected.getBody().readUtf8(), response.getBody().readUtf8());
	}

	@Test
	public void chunkedBodyInvalidFormatNotInt() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-ChunkedBody expected to be in format <int maxChunkSize> but got 'invalid'");

		parser.createResponse(input("chunkedBodyInvalidFormatNotInt"));
	}

	@Test
	public void bodyDelay() throws IOException {
		MockResponse response = parser.createResponse(input("bodyDelay"));

		assertNull(response.getHeaders().get(MockResponseParser.BODY_DELAY_HEADER_NAME));
		assertEquals(123, response.getBodyDelay(TimeUnit.SECONDS));
	}

	@Test
	public void bodyDelayWithWhiteSpace() throws IOException {
		MockResponse response = parser.createResponse(input("bodyDelayWithWhiteSpace"));

		assertNull(response.getHeaders().get(MockResponseParser.BODY_DELAY_HEADER_NAME));
		assertEquals(123, response.getBodyDelay(TimeUnit.SECONDS));
	}

	@Test
	public void bodyDelayInvalidFormatInvalidUnit() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-BodyDelay expected to be in format <long delay>;<TimeUnit unit> but got '123;invalid'");

		parser.createResponse(input("bodyDelayInvalidFormatInvalidUnit"));
	}

	@Test
	public void bodyDelayInvalidFormatInvalidDelay() throws IOException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("OkHttp-BodyDelay expected to be in format <long delay>;<TimeUnit unit> but got 'invalid;SECONDS'");

		parser.createResponse(input("bodyDelayInvalidFormatInvalidDelay"));
	}

	private InputStream input(String name) {
		String resourceName = getClass().getSimpleName() + "_okhttp3/" + name;
		return getClass().getResourceAsStream(resourceName);
	}
}
