package tecnospeed;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bigtesting.fixd.ServerFixture;
import org.bigtesting.fixd.core.Method;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class ClientTest {
	
	
	private String somenteParaTeste;

	private ServerFixture server;
	private Client client;

	@Before
	public void init() throws Exception {
		server = new ServerFixture(8080);
		server.start();
		client = new Client();
	}

	@Test
	public void testGetContent() throws Exception {
		String esperada = "ALO_TESTE";
		server.handle(Method.GET, "/alo").with(200, "text/plan", esperada);
		String returnContent = client.getContent("http://localhost:8080/alo");
		assertEquals(esperada, returnContent.trim());
	}
	
	@Test
	public void testPostContent() throws Exception {
		server.handle(Method.POST, "/login",
				"application/x-www-form-urlencoded")
				.with(200, "text/plain", "usuario:[request?username] senha:[request?password]");
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "gecen");
		params.put("password", "123mudar");
		assertEquals("usuario:gecen senha:123mudar", client.postContent("http://localhost:8080/login", params).trim());
		
	}

	@Test
	public void testPostUtilizandoAsyncHttpClient() throws Exception {
		server.handle(Method.POST, "/greeting",
				"application/x-www-form-urlencoded")
				.with(200, "text/plain", "Hello [request?name]");

		@SuppressWarnings("resource")
		Response resp = new AsyncHttpClient()
				.preparePost("http://localhost:8080/greeting")
				.addParameter("name", "Tim").execute().get();

		assertEquals("Hello Tim", resp.getResponseBody().trim());
	}
	@Test
	public void testPostContentUtilizandoApiHttpComponents() throws Exception {
		server.handle(Method.POST, "/greeting",
				"application/x-www-form-urlencoded")
				.with(200, "text/plain", "Hello [request?name]");


		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://localhost:8080/greeting");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("name", "Tim"));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {
		    HttpEntity entity = response.getEntity();
		    String string = EntityUtils.toString(entity);
		    assertEquals("Hello Tim", string.trim());
		} finally {
		    response.close();
		}		
		
	}

	@After
	public void stop() throws Exception {
		server.stop();
	}

}
