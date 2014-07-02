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
package net.jforum.util;

/**
 * All configuration keys and system constants.
 * @author Rafael Steil
 */
public class ConfigKeys {

	public static final String SPRING_CONTEXT = "springContext";
	public static final String HTTP_SERVLET_RESPONSE = "jforum.http.servlet.response";
	public static final String CONFIG = "config";
	public static final String SERVLET_CONTEXT = "__servletContext";
	public static final String LOGGED = "logged";
	public static final String APPLICATION_PATH = "application.path";
	public static final String INSTALLATION = "installation";
	public static final String INSTALLED = "installed";

	public static final String INSTALL_DIR = "install.dir";

	public static final String FILECHANGES_DELAY = "file.changes.delay";
	public static final String C3P0_EXTRA_PARAMS = "c3p0.extra.params";

	public static final String MESSAGE_FORMATTERS = "message.formatters";
	public static final String FORUM_TIME_LIMITED_ENABLE = "forum.time.limited.enable";
	public static final String AUTHENTICATION_TYPE = "authentication.type";
	public static final String SSO_IMPLEMENTATION = "sso.implementation";
	public static final String LOGIN_AUTHENTICATOR = "login.authenticator";
	public static final String TYPE_DEFAULT = "default";
	public static final String TYPE_SSO = "sso";
	public static final String AUTO_LOGIN_ENABLED = "auto.login.enabled";

	public static final String SSO_PASSWORD_ATTRIBUTE = "sso.password.attribute";
	public static final String SSO_EMAIL_ATTRIBUTE = "sso.email.attribute";
	public static final String SSO_DEFAULT_PASSWORD = "sso.default.password";
	public static final String SSO_DEFAULT_EMAIL = "sso.default.email";
	public static final String SSO_REDIRECT = "sso.redirect";
	public static final String SSO_LOGOUT = "sso.logout";
	public static final String EXTERNAL_USER_MANAGEMENT = "external.user.management";

	public static final String RESOURCE_DIR = "resource.dir";

	public static final String TEMPLATES_MAPPING = "templates.mapping";
	public static final String TEMPLATE_DIRECTORY = "template.directory";
	public static final String TEMPLATE_NAME = "template.name";
	public static final String ENCODING = "encoding";
	public static final String DEFAULT_CONTAINER_ENCODING = "default.container.encoding";
	public static final String SERVLET_NAME = "servlet.name";
	public static final String DEFAULT_CONFIG = "default.config";
	public static final String CONTEXT_NAME = "context.name";
	public static final String SERVLET_EXTENSION = "servlet.extension";
	public static final String COOKIE_USER_ID = "cookie.user.id";
	public static final String COOKIE_NAME_USER = "cookie.name.user";
	public static final String COOKIE_AUTO_LOGIN = "cookie.name.autologin";
	public static final String COOKIE_USER_HASH = "cookie.name.userHash";

	public static final String ANONYMOUS_USER_ID = "anonymousUserId";
	public static final String DEFAULT_USER_GROUP = "defaultUserGroup";
	public static final String USER_HASH_SEQUENCE = "user.hash.sequence";
	public static final String TOPICS_TRACKING = "topics.tracking";

	public static final String VERSION = "version";

	public static final String FORUM_LINK = "forum.link";
	public static final String HOMEPAGE_LINK = "homepage.link";
	public static final String FORUM_NAME = "forum.name";
	public static final String FORUM_PAGE_TITLE = "forum.page.title";
	public static final String FORUM_PAGE_METATAG_KEYWORDS = "forum.page.metatag.keywords";
	public static final String FORUM_PAGE_METATAG_DESCRIPTION = "forum.page.metatag.description";

	public static final String TMP_DIR = "tmp.dir";
	public static final String CACHE_DIR = "cache.dir";

	public static final String DATE_TIME_FORMAT = "dateTime.format";
	public static final String RSS_DATE_TIME_FORMAT = "rss.datetime.format";
	public static final String RSS_ENABLED = "rss.enabled";
	public static final String HOT_TOPIC_BEGIN = "hot.topic.begin";

	public static final String TOPICS_PER_PAGE = "topicsPerPage";
	public static final String POSTS_PER_PAGE = "postsPerPage";
	public static final String USERS_PER_PAGE = "usersPerPage";
	public static final String RECENT_TOPICS = "topic.recent";
	public static final String HOTTEST_TOPICS = "topic.hottest";
	public static final String POSTS_CACHE_SIZE = "posts.cache.size";
	public static final String POSTS_CACHE_ENABLED = "posts.cache.enabled";

	public static final String CAPTCHA_IGNORE_CASE = "captcha.ignore.case";
	public static final String CAPTCHA_REGISTRATION = "captcha.registration";
	public static final String CAPTCHA_POSTS = "captcha.posts";
	public static final String CAPTCHA_WIDTH = "captcha.width";
	public static final String CAPTCHA_HEIGHT = "captcha.height";
	public static final String CAPTCHA_MIN_FONT_SIZE = "captcha.min.font.size";
	public static final String CAPTCHA_MAX_FONT_SIZE = "captcha.max.font.size";
	public static final String CAPTCHA_MIN_WORDS = "captcha.min.words";
	public static final String CAPTCHA_MAX_WORDS = "captcha.max.words";

	public static final String I18N_DEFAULT = "i18n.board.default";
	public static final String I18N_DEFAULT_ADMIN = "i18n.internal";
	public static final String I18N_IMAGES_DIR = "i18n.images.dir";

	public static final String MAIL_BATCH_SIZE = "mail.batch.size";
	public static final String MAIL_LOST_PASSWORD_MESSAGE_FILE = "mail.lostPassword.messageFile";
	public static final String MAIL_LOST_PASSWORD_SUBJECT = "mail.lostPassword.subject";
	public static final String MAIL_NOTIFY_ANSWERS = "mail.notify.answers";
	public static final String MAIL_SENDER = "mail.sender";
	public static final String MAIL_CHARSET = "mail.charset";
	public static final String MAIL_TEMPLATE_ENCODING = "mail.template.encoding";
	public static final String MAIL_NEW_ANSWER_MESSAGE_FILE = "mail.newAnswer.messageFile";
	public static final String MAIL_NEW_ANSWER_SUBJECT = "mail.newAnswer.subject";
	public static final String MAIL_NEW_PM_SUBJECT = "mail.newPm.subject";
	public static final String MAIL_NEW_PM_MESSAGE_FILE = "mail.newPm.messageFile";
	public static final String MAIL_MESSSAGE_FORMAT = "mail.messageFormat";

	public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_PORT = "mail.smtp.port";

	public static final String MAIL_SMTP_SSL_AUTH = "mail.smtps.auth";
	public static final String MAIL_SMTP_SSL_HOST = "mail.smtps.host";
	public static final String MAIL_SMTP_SSL_PORT = "mail.smtps.port";
	public static final String MAIL_SMTP_SSL_LOCALHOST = "mail.smtps.localhost";

	public static final String MAIL_SMTP_SSL = "mail.smtp.ssl";

	public static final String MAIL_SMTP_LOCALHOST = "mail.smtp.localhost";
	public static final String MAIL_SMTP_USERNAME = "mail.smtp.username";
	public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
	public static final String MAIL_SMTP_DELAY = "mail.smtp.delay";

	public static final String MAIL_USER_EMAIL_AUTH = "mail.user.email.auth";
	public static final String MAIL_ACTIVATION_KEY_MESSAGE_FILE = "mail.activationKey.messageFile";
	public static final String MAIL_ACTIVATION_KEY_SUBJECT = "mail.activationKey.subject";

	public static final String MAIL_NEW_TOPIC_MESSAGE_FILE = "mail.newTopic.messageFile";
	public static final String MAIL_NEW_TOPIC_SUBJECT = "mail.newTopic.subject";

	public static final String HTML_TAGS_WELCOME = "html.tags.welcome";
	public static final String HTML_ATTRIBUTES_WELCOME = "html.attributes.welcome";
	public static final String HTML_LINKS_ALLOW_RELATIVE = "html.links.allow.relative";
	public static final String HTML_LINKS_ALLOW_PROTOCOLS = "html.links.allow.protocols";

	public static final String SMILIE_IMAGE_DIR = "smilie.image.dir";

	public static final String AVATAR_GALLERY_DIR = "avatar.gallery.dir";
	public static final String AVATAR_UPLOAD_DIR = "avatar.upload.dir";
	public static final String AVATAR_MAX_SIZE = "avatar.maxSize";
	public static final String AVATAR_MIN_WIDTH = "avatar.minWidth";
	public static final String AVATAR_MIN_HEIGHT = "avatar.minHeight";
	public static final String AVATAR_MAX_WIDTH = "avatar.maxWidth";
	public static final String AVATAR_MAX_HEIGHT = "avatar.maxHeight";
	public static final String AVATAR_ALLOW_UPLOAD = "avatar.allow.upload";
	public static final String AVATAR_ALLOW_GALLERY = "avatar.allow.gallery";
	public static final String AVATAR_STORE_PATH = "avatar.store.path";

	public static final String MOST_USERS_EVER_ONLINE = "most.users.ever.online";

	public static final String ATTACHMENTS_MAX_POST = "attachments.max.post";
	public static final String ATTACHMENTS_IMAGES_CREATE_THUMB = "attachments.images.createthumb";
	public static final String ATTACHMENTS_IMAGES_MAX_THUMB_W = "attachments.images.thumb.maxsize.w";
	public static final String ATTACHMENTS_IMAGES_MAX_THUMB_H = "attachments.images.thumb.maxsize.h";
	public static final String ATTACHMENTS_IMAGES_THUMB_BOX_SHOW = "attachments.images.thumb.box.show";
	public static final String ATTACHMENTS_ICON = "attachments.icon";
	public static final String ATTACHMENTS_STORE_DIR = "attachments.store.dir";
	public static final String ATTACHMENTS_UPLOAD_DIR = "attachments.upload.dir";
	public static final String ATTACHMENTS_ANONYMOUS = "attachments.anonymous";

	public static final String AGREEMENT_SHOW = "agreement.show";
	public static final String AGREEMENT_ACCEPTED = "agreement.accepted";
	public static final String AGREEMENT_DEFAULT_FILE = "agreement.default.file";
	public static final String AGREEMENT_FILES_PATH = "agreement.files.path";
	public static final String REGISTRATION_ENABLED = "registration.enabled";
	public static final String USERNAME_MAX_LENGTH = "username.max.length";

	public static final String CLICKSTREAM_CONFIG = "clickstream.config";
	public static final String IS_BOT = "clickstream.is.bot";

	public static final String POSTS_NEW_DELAY = "posts.new.delay";
	public static final String LAST_POST_TIME = "last.post.time";

    public static final String LOGIN_IGNORE_XFORWARDEDHOST = "login.ignore.xforwardedhost";
	public static final String LOGIN_IGNORE_REFERER = "login.ignore.referer";

    public static final String JFORUM_VERSION_URL = "jforum.version.url";
	public static final String REQUEST_IGNORE_CAPTCHA = "request.ignore.captcha";

	public static final String MODERATION_LOGGING_ENABLED = "moderation.logging.enabled";
	public static final String BANLIST_SEND_403FORBIDDEN = "banlist.send.403forbidden";
	public static final String RENDER_CUSTOM_LOGIC = "render.custom.logic";
	public static final String RENDER_CUSTOM_COMPONENT = "render.custom.component";
	public static final String VRAPTOR_VIEW_PATTERN = "vraptor.view.pattern";
	public static final String IGNORE_VIEW_MANAGER_REDIRECT = "ignore.viewmanager.redirect";
	public static final String USER_SESSION = "userSession";
	public static final String ROLE_MANAGER = "roleManager";

	public static final String SEARCH_INDEXING_ENABLED = "search.indexing.enabled";
	public static final String LUCENE_ANALYZER = "lucene.analyzer";
	public static final String LUCENE_INDEX_WRITE_PATH = "lucene.index.write.path";
	public static final String LUCENE_SETTINGS = "lucene.settings";
	public static final String LUCENE_CURRENTLY_INDEXING = "lucene.currently.indexing";
	public static final String LUCENE_INDEXER_RAM_NUMDOCS = "lucene.indexer.ram.numdocs";
	public static final String LUCENE_BATCH_SIZE = "lucene.batch.size";
	public static final String QUERY_IGNORE_TOPIC_MOVED = "query.ignore.topic.moved";

	public static final String BLOCK_IP = "jforum.block_ip";


	private ConfigKeys() { }
}
