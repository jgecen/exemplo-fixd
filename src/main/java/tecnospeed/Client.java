package tecnospeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;



public class Client {

	public String getContent(String url, Map<String, String> params) throws Exception{
		Request request = Request.Get(url);
		return doRequest(params, request);
	}
	public String getContent(String url) throws Exception{
		return getContent(url, new HashMap<String, String>());
	}
	
	public String postContent(String url, Map<String, String> params) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		
		Set<Entry<String,String>> entrySet = params.entrySet();
		for (Entry<String, String> entry : entrySet) {
			nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));	
		}
		
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse response = httpclient.execute(httpPost);
	    HttpEntity entity = response.getEntity();
	    
		return EntityUtils.toString(entity);
	}

	private String doRequest(Map<String, String> params, Request request)
			throws ClientProtocolException, IOException {
		adicionaParametrosNaRequisicao(params, request);		
		return obtemRetorno(request);
	}

	private String obtemRetorno(Request request)
			throws ClientProtocolException, IOException {
		return request.execute().returnContent().asString();
	}

	private void adicionaParametrosNaRequisicao(Map<String, String> params,
			Request request) {
		Set<Entry<String,String>> entrySet = params.entrySet();		
		for (Entry<String, String> entry : entrySet) {
			request.addHeader(entry.getKey(), entry.getValue());
		}
	}

	public static void main(String a[]) throws Exception{
		Client c = new Client();
		System.out.println(c.getContent("http://www.google.com"));
	}
}
