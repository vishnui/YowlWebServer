package com.indukuriapps.yowlwebserver.utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrivacyServlet extends HttpServlet{
	private static final long serialVersionUID = -4025025549862928364L;
	

	String html = "<!-- START PRIVACY POLICY CODE --><div style=\"font-family:arial\"><strong>What information do we collect?</strong> " +
			"<br /><br />We collect information from you when you place an order or fill out a form.  <br /><br />When ordering or registering on our site, as appropriate, " +
			"you may be asked to enter your: e-mail address, phone number or funding intrument details. You may, however, visit our site anonymously.<br /><br " +
			"/><strong>What do we use your information for?</strong> <br /><br />Any of the information we collect from you may be used in one of the following ways: <br /><br />" +
			"; To process transactions<br /><blockquote>Your information, whether public or private, will not be sold, exchanged, transferred, or given to any other company for " +
			"any reason whatsoever, without your consent, other than for the express purpose of delivering the purchased product or service requested.</blockquote><br /><br" +
			" /><strong>Do we use cookies?</strong> <br /><br />We do not use cookies.<br /><br /><strong>Do we disclose any information to outside parties?</strong> <br /><br" +
			" />We do not sell, trade, or otherwise transfer to outside parties your personally identifiable information. This does not include trusted third parties who assist" +
			" us in operating our website, conducting our business, or servicing you, so long as those parties agree to keep this " +
			"information confidential. We may also release your information when we believe release is appropriate to comply with the law, enforce our site policies, or protect" +
			" ours or others rights, property, or safety. However, non-personally identifiable visitor information may be provided to other parties for marketing, advertising," +
			" or other uses.<br /><br /><strong>California Online Privacy Protection Act Compliance</strong><br /><br />Because we value your privacy we have taken the necessary" +
			" precautions to be in compliance with the California Online Privacy Protection Act. We therefore will not distribute your personal information to outside parties" +
			" without your consent.<br /><br /><strong>Childrens Online Privacy Protection Act Compliance</strong> <br /><br />We are in compliance with the requirements of " +
			"COPPA (Childrens Online Privacy Protection Act), we do not collect any information from anyone under 13 years of age. Our website, products and services are all " +
			"directed to people who are at least 13 years old or older.<br /><br /><strong>Your Consent</strong> <br /><br />By using our site, you consent to our <a style='" +
			"text-decoration:none; color:#3C3C3C;' " +
			"href='http://www.freeprivacypolicy.com/' target='_blank'>privacy policy</a>.<br /><br /><strong>Changes to our Privacy Policy</strong> <br /><br />If we decide to " +
			"change our privacy policy, we will post those changes on this page, and/or update the Privacy Policy modification date below. <br /><br />This policy was last modified " +
			"on 7/15/2013<br /><br /><strong>Contacting Us</strong> <br /><br />If there are any questions regarding this privacy policy you may contact us using the " +
			"information below. <br /><br />vishnui@gmail.com<br /><br /><span></span><span></span>This policy is powered by Free Privacy Policy and Rhino Support <a rel='" +
			"nofollow' style='color:#000; text-decoration:none;' href='http://www.rhinosupport.com' target='_blank'>helpdesk software</a>.<span></span><span></span><span></span>" +
			"<!-- END PRIVACY POLICY CODE -->" ;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.setContentType("text/html");
		resp.getWriter().write(html);
	}

}
