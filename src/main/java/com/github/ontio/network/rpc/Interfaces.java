/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.network.rpc;

import com.github.ontio.io.json.JNumber;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;
import com.github.ontio.io.json.JArray;

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
		if(new Double(response.get("error").asNumber()).intValue() == 0){
			return response.get("result");
		} else {
			throw new RpcException(new Double(response.get("error").asNumber()).intValue(),""+response);
		}
	}

	private static JObject makeRequest(String method, JObject[] params) {
		JObject request = new JObject();
		request.set("jsonrpc", new JString("2.0"));
		request.set("method", new JString(method));
		request.set("params", new JArray(params));
		request.set("id", new JNumber(getNextId()));
		System.out.println(request);
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
