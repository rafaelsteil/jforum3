package net.jforum.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rafael Steil, Jonatan Cloutier
 */
@RunWith(MockitoJUnitRunner.class)
public class SafeHtmlTestCase {
	@Mock private JForumConfig config;
	private SafeHtml safeHtml;

	@Before
	public void setUp() throws Exception {
		when(config.containsKey(ConfigKeys.HTML_TAGS_WELCOME)).thenReturn(true);
		when(config.containsKey(ConfigKeys.HTML_ATTRIBUTES_WELCOME)).thenReturn(true);
		when(config.containsKey(ConfigKeys.HTML_LINKS_ALLOW_PROTOCOLS)).thenReturn(true);
		when(config.getValue(ConfigKeys.HTML_TAGS_WELCOME)).thenReturn("u, a, img, i, u, li, ul, font, br, p, b, hr");
		when(config.getValue(ConfigKeys.HTML_ATTRIBUTES_WELCOME)).thenReturn("src, href, size, face, color, target, rel");
		when(config.getValue(ConfigKeys.HTML_LINKS_ALLOW_PROTOCOLS)).thenReturn("http://, https://, mailto:, ftp://");
		when(config.getBoolean(ConfigKeys.HTML_LINKS_ALLOW_RELATIVE)).thenReturn(true);
		
		safeHtml = new SafeHtml(config);
	}
	
	@Test
	public void javascriptInsideURLTagExpectItToBeRemoved() {
		String input = "<a class=\"snap_shots\" rel=\"nofollow\" target=\"_new\" onmouseover=\"javascript:alert('test2');\" href=\"before\">test</a>";
		String expected = "<a class=\"snap_shots\" rel=\"nofollow\" target=\"_new\"  >test</a>";

		String result = safeHtml.ensureAllAttributesAreSafe(input);

		assertEquals(expected, result);
	}

	@Test
	public void javascriptInsideImageTagExpectItToBeRemoved() {
		String input = "<img border=\"0\" onmouseover=\"javascript:alert('buuuh!!!');\"\"\" src=\"javascript:alert('hi from an alert!');\"/>";
		String expected = "<img border=\"0\" \"\" />";

		String result = safeHtml.ensureAllAttributesAreSafe(input);

		assertEquals(expected, result);
	}

	@Test
	public void iframe() {
		String input = "<iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'></iframe>";
		String expected = "&lt;iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'&gt;&lt;/iframe&gt;";

		String result = safeHtml.makeSafe(input);
				
		assertEquals(expected, result);
	}

	@Test
	public void makeSafe() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='http://somelink'>Some Link</a>");
		sb.append("bla <b>bla</b> <pre>code code</pre>");
		sb.append("<script>document.location = 'xxx';</script>");
		sb.append("<img src='http://imgPath' onLoad='window.close();'>");
		sb.append("<a href='javascript:alert(bleh)'>xxxx</a>");
		sb.append("<img src='javascript:alert(bloh)'>");
		sb.append("<img src=\"&#106ava&#115cript&#58aler&#116&#40&#39Oops&#39&#41&#59\">");
		sb.append("\"> TTTTT <");
		sb.append("<img src='http://some.image' onLoad=\"javascript:alert('boo')\">");
		sb.append("<b>heeelooo, nurse</b>");
		sb.append("<b style='some style'>1, 2, 3</b>");
		String input = sb.toString();

		sb = new StringBuilder();
		sb.append("<a href='http://somelink'>Some Link</a>");
		sb.append("bla <b>bla</b> &lt;pre&gt;code code&lt;/pre&gt;");
		sb.append("&lt;script&gt;document.location = 'xxx';&lt;/script&gt;");
		sb.append("<img src='http://imgPath' >");
		sb.append("<a >xxxx</a>");
		sb.append("<img >");
		sb.append("<img >");
		sb.append("&quot;&gt; TTTTT &lt;");
		sb.append("<img src='http://some.image' >");
		sb.append("<b>heeelooo, nurse</b>");
		sb.append("<b >1, 2, 3</b>");
		String expected = sb.toString();

		String result = safeHtml.makeSafe(input);
				
		assertEquals(expected, result);
	}
}
