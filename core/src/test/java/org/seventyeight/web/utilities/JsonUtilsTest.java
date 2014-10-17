package org.seventyeight.web.utilities;

import com.google.gson.*;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonUtilsTest {

	@Test
	public void test() {
		JsonArray a = new JsonArray();
		
		JsonObject extension = new JsonObject();
				
		JsonObject config = new JsonObject();
		config.addProperty("class", "org.seventyeight");
		
		extension.add("config", config);
		extension.addProperty("extension", "ACL");
		
		a.add(extension);
		
		String o = JsonUtils.toHtml("extensions", a);
		
		System.out.println(o);
		assertThat(o, is("<div name=\"extensions\"><div name=\"config\" class=\"targetObject\"><input name=\"class\" value=\"org.seventyeight\"></div><input name=\"extension\" value=\"ACL\"></div>"));
	}

}
