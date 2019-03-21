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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Rob Winch
 *
 */
public class EnqueueResourcesMockWebServerITest {
	@Rule
	public EnqueueResourcesMockWebServer server = new EnqueueResourcesMockWebServer();

	@Test
	public void singleRequest() throws IOException {
		OkHttpClient client = new OkHttpClient.Builder().build();
		HttpUrl url = server.getServer().url("/");
		Request request = new Request.Builder().get().url(url).build();

		Response response = client.newCall(request).execute();

		assertEquals(200, response.code());
		assertEquals("Hi", response.body().string());
	}

	@Test
	public void contentType() throws IOException {
		OkHttpClient client = new OkHttpClient.Builder().build();
		HttpUrl url = server.getServer().url("/");
		Request request = new Request.Builder().get().url(url).build();

		Response response = client.newCall(request).execute();

		assertEquals("application/json", response.headers().get("Content-Type"));
		assertEquals("{'message':'hi'}", response.body().string());
	}

	@Test
	public void multipleRequests() throws IOException {
		OkHttpClient client = new OkHttpClient.Builder().build();
		HttpUrl url = server.getServer().url("/");

		Request request = new Request.Builder().get().url(url).build();

		Response response = client.newCall(request).execute();

		assertEquals(200, response.code());
		assertEquals("Hi", response.body().string());

		response = client.newCall(request).execute();

		assertEquals(500, response.code());
		assertEquals("Fail", response.body().string());
	}

	@Test
	@EnqueueRequests("body-relative")
	public void enqueueRequestsRelative() throws Exception {
		OkHttpClient client = new OkHttpClient.Builder().build();
		HttpUrl url = server.getServer().url("/");

		Request request = new Request.Builder().get().url(url).build();

		Response response = client.newCall(request).execute();

		assertEquals(200, response.code());
		assertEquals("body-relative", response.body().string());
	}

	@Test
	@EnqueueRequests("/okhttp3/mockwebserver/EnqueueResourcesMockWebServerITest_resource/body-absolute")
	public void enqueueRequestsAbsolute() throws Exception {
		OkHttpClient client = new OkHttpClient.Builder().build();
		HttpUrl url = server.getServer().url("/");

		Request request = new Request.Builder().get().url(url).build();

		Response response = client.newCall(request).execute();

		assertEquals(200, response.code());
		assertEquals("body-absolute", response.body().string());
	}
}