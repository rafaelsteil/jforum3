/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.mail;

import groovy.text.SimpleTemplateEngine;
import groovy.text.TemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.jforum.core.exceptions.MailException;
import net.jforum.entities.User;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Dispatch emails to the world.
 *
 * @author Rafael Steil
 */
public abstract class Spammer {
	private static final Logger logger = Logger.getLogger(Spammer.class);

	private static final int MESSAGE_HTML = 0;
	private static final int MESSAGE_TEXT = 1;

	private Session session;
	private String username;
	private String password;
	private String messageId;
	private String inReplyTo;
	private int messageFormat;
	private boolean needCustomization;
	private MimeMessage message;
	private JForumConfig config;
	private File templateFile;
	private List<User> users = new ArrayList<User>();
	private Properties mailProperties = new Properties();
	private Map<String, Object> templateParams = new HashMap<String, Object>();
	private TemplateEngine templateEngine = new SimpleTemplateEngine();

	public Spammer(JForumConfig config) throws MailException {
		this.config = config;

		boolean ssl = config.getBoolean(ConfigKeys.MAIL_SMTP_SSL);

		String hostProperty = this.hostProperty(ssl);
		String portProperty = this.portProperty(ssl);
		String authProperty = this.authProperty(ssl);
		String localhostProperty = this.localhostProperty(ssl);

		mailProperties.put(hostProperty, config.getValue(ConfigKeys.MAIL_SMTP_HOST));
		mailProperties.put(portProperty, config.getValue(ConfigKeys.MAIL_SMTP_PORT));

		String localhost = this.config.getValue(ConfigKeys.MAIL_SMTP_LOCALHOST);

		if (!StringUtils.isEmpty(localhost)) {
			mailProperties.put(localhostProperty, localhost);
		}

		mailProperties.put("mail.mime.address.strict", "false");
		mailProperties.put("mail.mime.charset", this.config.getValue(ConfigKeys.MAIL_CHARSET));
		mailProperties.put(authProperty, this.config.getValue(ConfigKeys.MAIL_SMTP_AUTH));

		username = this.config.getValue(ConfigKeys.MAIL_SMTP_USERNAME);
		password = this.config.getValue(ConfigKeys.MAIL_SMTP_PASSWORD);

		messageFormat = this.config.getValue(ConfigKeys.MAIL_MESSSAGE_FORMAT).equals("html")
			? MESSAGE_HTML
			: MESSAGE_TEXT;

		session = Session.getInstance(mailProperties);
	}

	public boolean dispatchMessages() {
		try {
			if (config.getBoolean(ConfigKeys.MAIL_SMTP_AUTH)) {
				this.dispatchAuthenticatedMessage();
			}
			else {
				this.dispatchAnonymousMessage();
			}
		}
		catch (MessagingException e) {
			logger.error("Error while dispatching the message." + e, e);
		}

		return true;
	}

	protected JForumConfig getConfig() {
		return config;
	}

	private void dispatchAnonymousMessage() throws AddressException, MessagingException {
		int sendDelay = config.getInt(ConfigKeys.MAIL_SMTP_DELAY);

		for (User user : users) {
			if (StringUtils.isEmpty(user.getEmail())) {
				continue;
			}

			if (needCustomization) {
				this.defineUserMessage(user);
			}

			Address address = new InternetAddress(user.getEmail());

			if (logger.isTraceEnabled()) {
				logger.trace("Sending mail to: " + user.getEmail());
			}

			message.setRecipient(Message.RecipientType.TO, address);
			Transport.send(message, new Address[] { address });

			if (sendDelay > 0) {
				this.waitUntilNextMessage(sendDelay);
			}
		}
	}

	private void dispatchAuthenticatedMessage() throws NoSuchProviderException {
		if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
			int batchSize = config.getInt(ConfigKeys.MAIL_BATCH_SIZE);
			int total = (int)Math.ceil((double)users.size() / (double)batchSize);

			Iterator<User> iterator = users.iterator();

			for (int i = 0; i < total; i++) {
				this.dispatchNoMoreThanBatchSize(iterator, batchSize);
			}
		}
	}

	private void dispatchNoMoreThanBatchSize(Iterator<User> iterator, int batchSize) throws NoSuchProviderException {
		boolean ssl = config.getBoolean(ConfigKeys.MAIL_SMTP_SSL);
		Transport transport = session.getTransport(ssl ? "smtps" : "smtp");

		try {
			String host = config.getValue(ConfigKeys.MAIL_SMTP_HOST);
			int sendDelay = config.getInt(ConfigKeys.MAIL_SMTP_DELAY);

			transport.connect(host, username, password);

			if (transport.isConnected()) {
				for (int counter = 0; counter < batchSize && iterator.hasNext(); counter++) {
					User user = iterator.next();

					if (StringUtils.isEmpty(user.getEmail())) {
						continue;
					}

					if (needCustomization) {
						this.defineUserMessage(user);
					}

					Address address = new InternetAddress(user.getEmail());

					if (logger.isDebugEnabled()) {
						logger.debug("Sending mail to: " + user.getEmail());
					}

					message.setRecipient(Message.RecipientType.TO, address);
					transport.sendMessage(message, new Address[] { address });

					if (sendDelay > 0) {
						this.waitUntilNextMessage(sendDelay);
					}
				}
			}
		}
		catch (Exception e) {
			logger.error("Errow while sending emails: " + e, e);
			throw new MailException(e);
		}
		finally {
			try {
				transport.close();
			}
			catch (Exception e) { }
		}
	}

	private void defineUserMessage(User user) {
		try {
			templateParams.put("user", user);

			String text = this.processTemplate();

			this.defineMessageText(text);
		}
		catch (Exception e) {
			throw new MailException(e);
		}
	}

	private void waitUntilNextMessage(int sendDelay) {
		try {
			Thread.sleep(sendDelay);
		}
		catch (InterruptedException ie) {
			logger.error("Error while Thread.sleep." + ie, ie);
		}
	}

	/**
	 * Prepares the mail message for sending.
	 *
	 * @param subject the subject of the email
	 * @param messageFile the path to the mail message template
	 * @throws MailException
	 */
	protected void prepareMessage(String subject, String messageFile) throws MailException {
		if (messageId == null) {
			message = new MimeMessage(session);
		}
		else {
			message = new IdentifiableMimeMessage(session);
			((IdentifiableMimeMessage) message).setMessageId(messageId);
		}

		templateParams.put("forumName", config.getValue(ConfigKeys.FORUM_NAME));

		try {
			message.setSentDate(new Date());
			message.setFrom(new InternetAddress(config.getValue(ConfigKeys.MAIL_SENDER)));
			message.setSubject(subject, config.getValue(ConfigKeys.MAIL_CHARSET));

			if (inReplyTo != null) {
				message.addHeader("In-Reply-To", inReplyTo);
			}

			this.createTemplate(messageFile);
			needCustomization = this.isCustomizationNeeded();

			// If we don't need to customize any part of the message,
			// then build the generic text right now
			if (!needCustomization) {
				String text = this.processTemplate();
				this.defineMessageText(text);
			}
		}
		catch (Exception e) {
			throw new MailException(e);
		}
	}

	/**
	 * Set the text contents of the email we're sending
	 *
	 * @param text the text to set
	 * @throws MessagingException
	 */
	private void defineMessageText(String text) throws MessagingException {

		if (messageFormat == MESSAGE_TEXT) {
			message.setText(text);
		}
		else {
			String charset = config.getValue(ConfigKeys.MAIL_CHARSET);
			message.setContent(text.replaceAll("\n", "<br />"), "text/html; charset=" + charset);
		}
	}

	/**
	 * Gets the message text to send in the email.
	 *
	 * @param templateName The file with the email template, relative to the application root
	 * @return The email message text
	 * @throws Exception
	 */
	protected void createTemplate(String templateName) throws Exception {
		templateFile = new File(config.getValue(ConfigKeys.APPLICATION_PATH) + templateName);
	}

	/**
	 * Merge the template data, creating the final content. T
	 * his method should only be called after {@link #createTemplate(String)} and
	 * {@link #setTemplateParams(SimpleHash)}
	 *
	 * @return the generated content
	 */
	protected String processTemplate() throws Exception {
		return templateEngine.createTemplate(templateFile)
			.make(templateParams).toString();
	}

	/**
	 * Set the parameters for the template being processed
	 *
	 * @param params the parameters to the template
	 */
	protected void setTemplateParams(Map<String, Object> params) {
		templateParams = params;
	}

	/**
	 * Check if we have to send customized emails
	 *
	 * @return true if there is a need for customized emails
	 */
	private boolean isCustomizationNeeded() {
		for (User user : users) {
			if (user.getNotifyText()) {
				return true;
			}
		}

		return false;
	}

	protected String buildForumLink() {
		String forumLink = this.getConfig().getValue(ConfigKeys.FORUM_LINK);

		if (forumLink.charAt(forumLink.length() - 1) != '/') {
			forumLink += "/";
		}
		return forumLink;
	}

	protected void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	protected void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	protected void setUsers(List<User> users) {
		this.users = users;
	}

	private String localhostProperty(boolean ssl) {
		return ssl ? ConfigKeys.MAIL_SMTP_SSL_LOCALHOST : ConfigKeys.MAIL_SMTP_LOCALHOST;
	}

	private String authProperty(boolean ssl) {
		return ssl ? ConfigKeys.MAIL_SMTP_SSL_AUTH : ConfigKeys.MAIL_SMTP_AUTH;
	}

	private String portProperty(boolean ssl) {
		return ssl ? ConfigKeys.MAIL_SMTP_SSL_PORT : ConfigKeys.MAIL_SMTP_PORT;
	}

	private String hostProperty(boolean ssl) {
		return ssl ? ConfigKeys.MAIL_SMTP_SSL_HOST : ConfigKeys.MAIL_SMTP_HOST;
	}
}
