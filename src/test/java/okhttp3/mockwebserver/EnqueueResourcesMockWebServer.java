/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.mockwebserver;

import java.io.IOException;
import java.io.InputStream;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Allows externalizing queued responses using classpath resources. This is
 * useful when dealing with larger responses from the server that may be
 * difficult to read inline within a test. An example test can be seen below:
 *
 * <pre>
 * package okhttp3.mockwebserver;
 *
 * public class EnqueueResourcesMockWebServerITest {
 * 	&#64;Rule
 * 	public EnqueueResourcesMockWebServer server = new EnqueueResourcesMockWebServer();
 *
 * 	&#64;Test
 * 	public void multipleRequests() throws IOException {
 * 		OkHttpClient client = new OkHttpClient.Builder().build();
 * 		HttpUrl url = server.getServer().url("/");
 *
 * 		Request request = new Request.Builder().get().url(url).build();
 *
 * 		Response response = client.newCall(request).execute();
 *
 * 		assertEquals(200, response.code());
 * 		assertEquals("Hi", response.body().string());
 *
 * 		response = client.newCall(request).execute();
 *
 * 		assertEquals(500, response.code());
 * 		assertEquals("Fail", response.body().string());
 * 	}
 * }
 * </pre>
 *
 * <p>
 * For this to work the following classpath resources are present:
 * </p>
 *
 * okhttp3.mockwebserver.EnqueueResourcesMockWebServerITest_okhttp3.
 * multipleRequests/1
 *
 * <pre>
 * HTTP/1.1 200 OK
 *
 * Hi
 * </pre>
 *
 * okhttp3.mockwebserver.EnqueueResourcesMockWebServerITest_okhttp3.
 * multipleRequests/2
 *
 * <pre>
 * HTTP/1.1 500 Internal Server Error
 *
 * Fail
 * </pre>
 *
 * In addition the following special headers can be used:
 *
 * <ul>
 * <li>OkHttp-ThrottleBody - Sets the MockResponse's throttleBody. Takes the
 * format of &lt;long bytesPerPeriod&gt;;&lt;long period&gt;;&lt;TimeUnit
 * unit&gt;</li>
 * <li>OkHttp-ChunkedBody - Sets the MockResponse's chunked body value. Takes
 * the format of &lt;int maxChunkSize&gt;</li>
 * <li>OkHttp-BodyDelay - Sets the MockResponse's bodyDelay. Takes the format of
 * &lt;long delay&gt;TimeUnit unit&gt;</li>
 * </ul>
 *
 * If additional customizations are necessary, the MockResponse can be accessed
 * using {@link #peek()}.
 *
 * @author Rob Winch
 */
public final class EnqueueResourcesMockWebServer implements TestRule {
	private final QueueDispatcher dispatcher;

	private final MockWebServer server;

	public EnqueueResourcesMockWebServer() {
		dispatcher = new QueueDispatcher();
		server = new MockWebServer();
		server.setDispatcher(dispatcher);
	}

	private final MockResponseParser parser = new MockResponseParser();

	public MockResponse peek() {
		return dispatcher.peek();
	}

	public MockWebServer getServer() {
		return server;
	}

	public String getServerUrl() {
		return "http://" + server.getHostName()+ ":" + server.getPort();
	}

	@Override
	public Statement apply(Statement base, final Description description) {
		final Statement serverStatement = server.apply(base, description);
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				enqueue(description);
				serverStatement.evaluate();
			}
		};
	}

	private void enqueue(Description description) {
		String resourceBaseName = getResourceBaseName(description);

		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			String resourceName = resourceBaseName + i;

			MockResponse response = getResponse(resourceName);
			if (response == null) {
				server.enqueue(tooManyRequests(resourceName));
				break;
			}
			server.enqueue(response);
		}
	}

	private MockResponse tooManyRequests(String resourceName) {
		MockResponse result = new MockResponse();
		result.setStatus("HTTP/1.1 400 Bad Request");
		result.setBody("You have made too many requests. Reduce your requests or provide a resource at "+resourceName);
		return result;
	}

	private MockResponse getResponse(String resourceName) {
		InputStream in = input(resourceName);
		if (in == null) {
			return null;
		}
		try {
			return parser.createResponse(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream input(String name) {
		return getClass().getResourceAsStream(name);
	}

	private String getResourceBaseName(Description description) {
		return "/"+(description.getTestClass().getName() + "_okhttp3/" + description.getMethodName() + "/")
				.replaceAll("\\.", "/");
	}
}