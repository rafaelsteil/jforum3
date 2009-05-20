package net.jforum.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Rafael Steil
 */
public class SafeHtmlTestCase {
	private String input;
	private String expected;
	private JForumConfig config;

	@Test
	public void javascriptInsideURLTagExpectItToBeRemoved() {
		String input = "<a class=\"snap_shots\" rel=\"nofollow\" target=\"_new\" onmouseover=\"javascript:alert('test2');\" href=\"before\">test</a>";
		String expected = "<a class=\"snap_shots\" rel=\"nofollow\" target=\"_new\"  >test</a>";

		String result = this.newSafeHtml().ensureAllAttributesAreSafe(input);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void javascriptInsideImageTagExpectItToBeRemoved() {
		String input = "<img border=\"0\" onmouseover=\"javascript:alert('buuuh!!!');\"\"\" src=\"javascript:alert('hi from an alert!');\"/>";
		String expected = "<img border=\"0\" \"\" />";

		String result = this.newSafeHtml().ensureAllAttributesAreSafe(input);

		Assert.assertEquals(expected, result);
	}

	@Test
	public void iframe() {
		String input = "<iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'></iframe>";
		String output = "&lt;iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'&gt;&lt;/iframe&gt;";

		Assert.assertEquals(output, this.newSafeHtml().makeSafe(input));
	}

	@Test
	public void makeSafe() throws Exception {
		Assert.assertEquals(expected, this.newSafeHtml().makeSafe(input));
	}

	@Before
	public void setUp() throws Exception {
		config = new JForumConfig(null, null);

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
		input = sb.toString();

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
		expected = sb.toString();
	}

	private SafeHtml newSafeHtml() {
		return new SafeHtml(config);
	}
}
