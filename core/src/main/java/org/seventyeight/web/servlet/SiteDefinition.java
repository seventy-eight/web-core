package org.seventyeight.web.servlet;

import java.util.ArrayList;
import java.util.List;

public class SiteDefinition {

	private List<String> javaScriptSources = new ArrayList<String>();
	
	private List<String> cssSources = new ArrayList<String>();
	
	private String theme = "default";
	
	private String title;
	
	public SiteDefinition(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public SiteDefinition addJavaScriptSource(String javaScript) {
		javaScriptSources.add(javaScript);
		return this;
	}
	
	public List<String> getJavaScriptSources() {
		return javaScriptSources;
	}
	
	public SiteDefinition addCssSource(String css) {
		cssSources.add(css);
		return this;
	}
	
	public List<String> getCssSources() {
		return cssSources;
	}
}
