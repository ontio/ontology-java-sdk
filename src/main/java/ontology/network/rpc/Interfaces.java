package ontology.network.rpc;

import ontology.io.json.JArray;
import ontology.io.json.JNumber;
import ontology.io.json.JObject;
import ontology.io.json.JString;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zx on 2018/2/1.
 */
class Interfaces {
	private final URL url;

	public String getHost() {
		return url.getHost() + " " + url.getPort();
	}

	public Interfaces(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public JObject call(String method, JObject ...params) throws RpcException, IOException {
		JObject response = send(makeRequest(method, params));
		if (response.containsProperty("result")) {
			return response.get("result");
		}
		else if (response.containsProperty("Result")) {
			return response.get("Result");
		}
		else if (response.containsProperty("error")) {
			throw new RpcException((int) response.get("error").get("code").asNumber(), response.get("error").get("message").asString());
		}
		else {
			throw new IOException();
		}
	}

	private static JObject makeRequest(String method, JObject[] params) {
		JObject request = new JObject();
		request.set("jsonrpc", new JString("2.0"));
		request.set("method", new JString(method));
		request.set("params", new JArray(params));
		request.set("id", new JNumber(getNextId()));
		return request;
	}
	public static String toSs(byte[] bb) {
		StringBuilder sb = new StringBuilder();
		for(byte b: bb) {
			sb.append(",").append(Byte.toUnsignedInt(b));
		}
		return sb.substring(1);
	}
	private static double getNextId() {
		double d = 0.0;
		do{
			d = Math.random();
		} while((""+d).indexOf("E") != -1);
		return d;
	}

	private JObject send(JObject request) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream())) {
			w.write(request.toString());
		}
		try (InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
			return JObject.parse(r);
		}
	}
}
