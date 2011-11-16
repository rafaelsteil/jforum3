--
-- Table structure for table 'jforum_banlist'
--
CREATE SEQUENCE jforum_banlist_seq;
CREATE TABLE jforum_banlist (
  banlist_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_banlist_seq'),
  user_id INTEGER DEFAULT 0,
  banlist_ip VARCHAR(15),
  banlist_email VARCHAR(255),
  PRIMARY KEY(banlist_id)
);
CREATE INDEX idx_banlist_user ON jforum_banlist(user_id);
CREATE INDEX idx_banlist_ip ON jforum_banlist(banlist_ip);
CREATE INDEX idx_banlist_email ON jforum_banlist(banlist_email);

--
-- Table structure for table 'jforum_categories'
--
CREATE SEQUENCE jforum_categories_seq;
CREATE TABLE jforum_categories (
  category_id INTEGER NOT NULL PRIMARY KEY DEFAULT NEXTVAL('jforum_categories_seq'),
  category_title VARCHAR(100) NOT NULL DEFAULT '',
  category_order INTEGER NOT NULL DEFAULT 0,
  category_moderated boolean default false,
  category_theme_id INTEGER,
);
CREATE INDEX idx_categories_themes_id ON jforum_categories(category_theme_id);

--
-- Table structure for table 'jforum_config'
--
CREATE SEQUENCE jforum_config_seq;
CREATE TABLE jforum_config (
  config_name VARCHAR(255) NOT NULL DEFAULT '',
  config_value VARCHAR(255) NOT NULL DEFAULT '',
  config_id int NOT NULL PRIMARY KEY DEFAULT nextval('jforum_config_seq')
);

--
-- Table structure for table 'jforum_forums'
--
CREATE SEQUENCE jforum_forums_seq;
CREATE TABLE jforum_forums (
  forum_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_forums_seq'),
  category_id INTEGER NOT NULL DEFAULT 1,
  forum_name VARCHAR(150) NOT NULL DEFAULT '',
  forum_description VARCHAR(255) DEFAULT NULL,
  forum_order INTEGER DEFAULT 1,
  forum_last_post_id INTEGER,
  forum_moderated boolean default false,
  forum_allow_anonymous_posts boolean default false,
  PRIMARY KEY(forum_id)
);
CREATE INDEX idx_forums_categories_id ON jforum_forums(category_id);

--
-- Table structure for table 'jforum_forums_watch'
--
CREATE TABLE jforum_forums_watch (
  forum_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  is_read boolean default true
);
CREATE INDEX idx_fw_forum ON jforum_forums_watch(forum_id);
CREATE INDEX idx_fw_user ON jforum_forums_watch(user_id);

--
-- Table structure for table 'jforum_groups'
--
CREATE SEQUENCE jforum_groups_seq;
CREATE TABLE jforum_groups (
  group_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_groups_seq'),
  group_name VARCHAR(40) NOT NULL DEFAULT '',
  group_description VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY(group_id)
);


CREATE TABLE jforum_user_groups (
	group_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL
);
CREATE INDEX idx_ug_group ON jforum_user_groups(group_id);
CREATE INDEX idx_ug_user ON jforum_user_groups(user_id);

--
-- Table structure for table 'jforum_roles'
--
CREATE SEQUENCE jforum_roles_seq;
CREATE TABLE jforum_roles (
  role_id INTEGER NOT NULL PRIMARY KEY DEFAULT NEXTVAL('jforum_roles_seq'),
  group_id INTEGER DEFAULT 0,
  name VARCHAR(255) NOT NULL
);
CREATE INDEX idx_roles_group ON jforum_roles(group_id);
CREATE INDEX idx_roles_name ON jforum_roles(name);

--
-- Table structure for table 'jforum_role_values'
--
CREATE TABLE jforum_role_values (
  role_id INTEGER NOT NULL,
  role_value INTEGER NOT NULL
);
CREATE INDEX idx_rv_role ON jforum_role_values(role_id);

--
-- Table structure for table 'jforum_post_report'
--
CREATE SEQUENCE jforum_post_report_seq;
CREATE TABLE jforum_post_report (
	report_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_post_report_seq'),
	report_date TIMESTAMP,
	report_description VARCHAR(255),
	report_status VARCHAR(10) DEFAULT 'UNRESOLVED',
	post_id INTEGER NOT NULL,
	user_id INTEGER NOT NULL,
	PRIMARY KEY(report_id)
);
CREATE INDEX idx_report_post ON jforum_post_report(post_id);
CREATE INDEX idx_report_user ON jforum_post_report(user_id);

--
-- Table structure for table 'jforum_posts'
--
CREATE SEQUENCE jforum_posts_seq;
CREATE TABLE jforum_posts (
  post_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_posts_seq'),
  topic_id INTEGER NOT NULL DEFAULT 0,
  user_id INTEGER NOT NULL DEFAULT 0,
  forum_id INTEGER NOT NULL DEFAULT 0,
  post_date timestamp DEFAULT NULL,
  poster_ip VARCHAR(50) DEFAULT NULL,
  enable_bbcode boolean default true,
  enable_html boolean default true,
  enable_smilies boolean default true,
  enable_sig boolean default true,
  post_edit_time timestamp DEFAULT NULL,
  post_edit_count INTEGER DEFAULT 0,
  status INTEGER DEFAULT 1,
  attach boolean default false,
  need_moderate boolean default false,
  post_subject VARCHAR(110) NOT NULL,
  post_text TEXT NOT NULL,
  PRIMARY KEY(post_id)
);
CREATE INDEX idx_posts_user ON jforum_posts(user_id);
CREATE INDEX idx_posts_topic ON jforum_posts(topic_id);
CREATE INDEX idx_posts_forum ON jforum_posts(forum_id);
CREATE INDEX idx_posts_time ON jforum_posts(post_date);
CREATE INDEX idx_posts_moderate ON jforum_posts(need_moderate);

--
-- Table structure for table 'jforum_privmsgs'
--
CREATE SEQUENCE jforum_privmsgs_seq;
CREATE TABLE jforum_privmsgs (
  privmsgs_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_privmsgs_seq'),
  privmsgs_type INTEGER NOT NULL DEFAULT 0,
  privmsgs_subject VARCHAR(255) NOT NULL DEFAULT '',
  privmsgs_from_userid INTEGER NOT NULL DEFAULT 0,
  privmsgs_to_userid INTEGER NOT NULL DEFAULT 0,
  privmsgs_date timestamp DEFAULT CURRENT_TIMESTAMP,
  privmsgs_ip VARCHAR(50) DEFAULT '',
  privmsgs_enable_bbcode boolean default true,
  privmsgs_enable_html boolean default false,
  privmsgs_enable_smilies boolean default true,
  privmsgs_attach_sig boolean default true,
  privmsgs_text TEXT,
  PRIMARY KEY(privmsgs_id)
);

--
-- Table structure for table 'jforum_ranks'
--
CREATE SEQUENCE jforum_ranks_seq;
CREATE TABLE jforum_ranks (
  rank_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_ranks_seq'),
  rank_title VARCHAR(50) NOT NULL DEFAULT '',
  rank_min INTEGER NOT NULL DEFAULT 0,
  rank_special BOOLEAN DEFAULT FALSE,
  rank_image VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY(rank_id)
);

--
-- Table structure for table 'jforum_sessions'
--
CREATE TABLE jforum_sessions (
  user_id INTEGER NOT NULL,
  session_start TIMESTAMP,
  session_last_accessed TIMESTAMP,
  session_last_visit TIMESTAMP,
  session_ip varchar(50) default '',
  PRIMARY KEY(user_id)
);

--
-- Table structure for table 'jforum_smilies'
--
CREATE SEQUENCE jforum_smilies_seq;
CREATE TABLE jforum_smilies (
  smilie_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_smilies_seq'),
  code VARCHAR(50) NOT NULL DEFAULT '',
  disk_name VARCHAR(255),
  PRIMARY KEY(smilie_id)
);

--
-- Table structure for table 'jforum_themes'
--
CREATE SEQUENCE jforum_themes_seq;
CREATE TABLE jforum_themes (
  theme_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_themes_seq'),
  template_name VARCHAR(30),
  style_name VARCHAR(30),
  PRIMARY KEY(theme_id)
);

--
-- Table structure for table 'jforum_topics'
--
CREATE SEQUENCE jforum_topics_seq;
CREATE TABLE jforum_topics (
  topic_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_topics_seq'),
  forum_id INTEGER NOT NULL DEFAULT 0,
  topic_subject VARCHAR(100) NOT NULL DEFAULT '',
  user_id INTEGER NOT NULL DEFAULT 0,
  topic_date timestamp DEFAULT CURRENT_TIMESTAMP,
  topic_views INTEGER DEFAULT 1,
  topic_replies INTEGER DEFAULT 0,
  topic_status INTEGER DEFAULT 0,
  topic_vote_id INTEGER DEFAULT NULL,
  topic_type INTEGER DEFAULT 0,
  topic_first_post_id INTEGER DEFAULT 0,
  topic_last_post_id INTEGER DEFAULT 0,
  topic_moved_id INTEGER DEFAULT 0,
  need_moderate boolean default false,
  has_attachment boolean default false,
  PRIMARY KEY(topic_id)
);

CREATE INDEX idx_topics_forum ON jforum_topics(forum_id);
CREATE INDEX idx_topics_user ON jforum_topics(user_id);
CREATE INDEX idx_topics_fp ON jforum_topics(topic_first_post_id);
CREATE INDEX idx_topics_lp ON jforum_topics(topic_last_post_id);
CREATE INDEX idx_topics_time ON jforum_topics(topic_date);
CREATE INDEX idx_topics_type ON jforum_topics(topic_type);
CREATE INDEX idx_topics_moved ON jforum_topics(topic_moved_id);
		
--
-- Table structure for table 'jforum_topics_watch'
--
CREATE SEQUENCE jforum_topics_watch_seq;
CREATE TABLE jforum_topics_watch (
  topics_watch_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_topics_watch_seq'),
  topic_id INTEGER NOT NULL DEFAULT 0,
  user_id INTEGER NOT NULL DEFAULT 0,
  is_read boolean default false
);
CREATE INDEX idx_tw_topic ON jforum_topics_watch(topic_id);
CREATE INDEX idx_tw_user ON jforum_topics_watch(user_id);

--
-- Table structure for table 'jforum_users'
--
CREATE SEQUENCE jforum_users_seq;
CREATE TABLE jforum_users (
  user_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_users_seq'),
  user_active boolean default true,
  username VARCHAR(50) NOT NULL DEFAULT '',
  user_password VARCHAR(32) NOT NULL,
  user_session_time INTEGER default 0,
  user_session_page INTEGER default 0,
  user_lastvisit timestamp DEFAULT CURRENT_TIMESTAMP,
  user_regdate timestamp DEFAULT CURRENT_TIMESTAMP,
  user_posts INTEGER DEFAULT 0,
  user_timezone VARCHAR(5) DEFAULT '',
  user_style INTEGER,
  user_lang VARCHAR(255),
  user_dateformat VARCHAR(20) DEFAULT '%d/%M/%Y %H:%i',
  user_new_privmsg INTEGER DEFAULT 0,
  user_unread_privmsg INTEGER DEFAULT 0,
  user_last_privmsg timestamp NULL,
  user_emailtime timestamp NULL,
  user_viewemail boolean default false,
  user_attachsig boolean default true,
  user_allowhtml boolean default false,
  user_allowbbcode boolean default true,
  user_allowsmilies boolean default true,
  user_allowavatar boolean default true,
  user_allow_pm boolean default true,
  user_allow_viewonline boolean default true,
  user_notify boolean default true,
  user_notify_always boolean default false,
  user_notify_text boolean default false,
  user_notify_pm boolean default true,
  user_popup_pm boolean default true,
  rank_id INTEGER,
  avatar_id integer,
  user_email VARCHAR(255),
  user_website VARCHAR(255),
  user_from VARCHAR(100),
  user_sig TEXT,
  user_aim VARCHAR(255),
  user_yim VARCHAR(255),
  user_msnm VARCHAR(255),
  user_occ VARCHAR(100),
  user_interests VARCHAR(255),
  user_biography TEXT DEFAULT NULL,
  user_actkey VARCHAR(32) DEFAULT NULL,
  gender CHAR(1) DEFAULT NULL,
  themes_id INTEGER DEFAULT NULL,
  deleted boolean default false,
  user_viewonline boolean default true,
  security_hash VARCHAR(32),
  user_authhash VARCHAR(32),
  PRIMARY KEY(user_id)
);

CREATE INDEX idx_user_avatar ON jforum_users(avatar_id);
CREATE INDEX idx_user_ranking ON jforum_users(rank_id);

--
-- Table structure for table 'jforum_vote_desc'
--
CREATE SEQUENCE jforum_vote_desc_seq;
CREATE TABLE jforum_vote_desc (
  vote_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_vote_desc_seq'),
  vote_text VARCHAR(255) NOT NULL,
  vote_start TIMESTAMP NOT NULL,
  vote_length INTEGER DEFAULT 0,
  PRIMARY KEY(vote_id)
);

--
-- Table structure for table 'jforum_vote_results'
--
CREATE SEQUENCE jforum_vote_results_seq;
CREATE TABLE jforum_vote_results (
  vote_option_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_vote_results_seq'),
  vote_id INTEGER NOT NULL,
  vote_option_text VARCHAR(255) NOT NULL,
  vote_result INTEGER NOT NULL
);

CREATE INDEX idx_vr_id ON jforum_vote_results(vote_id);

--
-- Table structure for table 'jforum_vote_voters'
--
CREATE SEQUENCE jforum_vote_voters_seq;
CREATE TABLE jforum_vote_voters (
  voter_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_vote_voters_seq'),
  vote_id INTEGER NOT NULL,
  vote_user_id INTEGER NOT NULL,
  vote_user_ip VARCHAR(50) DEFAULT ''
);

CREATE INDEX idx_vv_id ON jforum_vote_voters(vote_id);
CREATE INDEX idx_vv_user ON jforum_vote_voters(vote_user_id);

--
-- Table structure for table 'jforum_words'
--
CREATE SEQUENCE jforum_words_seq;
CREATE TABLE jforum_words (
  word_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_words_seq'),
  word VARCHAR(100) NOT NULL DEFAULT '',
  replacement VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY(word_id)
);

-- 
-- Table structure for table 'jforum_quota_limit'
--

CREATE SEQUENCE jforum_quota_limit_seq;
CREATE TABLE jforum_quota_limit (
	quota_limit_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_quota_limit_seq'),
	quota_desc VARCHAR(50) NOT NULL,
	quota_limit INTEGER NOT NULL,
	quota_type INTEGER DEFAULT 1,
	PRIMARY KEY(quota_limit_id)
);

--
-- Table structure for table 'jforum_extension_groups'
--

CREATE SEQUENCE jforum_extension_groups_seq;
CREATE TABLE jforum_extension_groups (
	extension_group_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_extension_groups_seq'),
	name VARCHAR(100) NOT NULL,
	allow INTEGER DEFAULT 1, 
	upload_icon VARCHAR(100),
	download_mode INTEGER DEFAULT 1,
	PRIMARY KEY(extension_group_id)
);

-- 
-- Table structure for table 'jforum_extensions'
--

CREATE SEQUENCE jforum_extensions_seq;
CREATE TABLE jforum_extensions (
	extension_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_extensions_seq'),
	extension_group_id INTEGER NOT NULL,
	description VARCHAR(100),
	upload_icon VARCHAR(100),
	extension VARCHAR(10),
	allow boolean default true,
	PRIMARY KEY(extension_id)
);

CREATE INDEX idx_ext_group ON jforum_extensions(extension_group_id);
CREATE INDEX idx_ext_ext ON jforum_extensions(extension);

--
-- Table structure for table 'jforum_attach'
--

CREATE SEQUENCE jforum_attach_seq;
CREATE TABLE jforum_attach (
	attach_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_attach_seq'),
	post_id INTEGER,
	download_count INTEGER DEFAULT 0,
	physical_filename VARCHAR(255) not null,
	real_filename VARCHAR(255) not null, 
	description varchar(255),
	mimetype varchar(255),
	filesize INTEGER,
	upload_date date, 
	file_extension varchar(5),
	thumb boolean default false,
	PRIMARY KEY(attach_id)
);

CREATE INDEX idx_att_post ON jforum_attach(post_id);

--
-- Table structure for table 'jforum_attach_quota'
--
CREATE SEQUENCE jforum_attach_quota_seq;
CREATE TABLE jforum_attach_quota (
	attach_quota_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_attach_quota_seq'),
	group_id INTEGER NOT NULL,
	quota_limit_id INTEGER NOT NULL,
	PRIMARY KEY(attach_quota_id)
);

CREATE INDEX idx_aq_group ON jforum_attach_quota(group_id);
CREATE INDEX idx_aq_ql ON jforum_attach_quota(quota_limit_id);

--
-- Table structure for table 'jforum_moderation_log'
-- 
CREATE SEQUENCE jforum_moderation_log_seq;
CREATE TABLE jforum_moderation_log (
	log_id INTEGER NOT NULL DEFAULT NEXTVAL('jforum_moderation_log_seq'),
	user_id INTEGER NOT NULL,
	log_description TEXT NOT NULL,
	log_original_message TEXT,
	log_date TIMESTAMP NOT NULL,
	log_type INTEGER DEFAULT 0,
	post_id INTEGER,
	topic_id INTEGER,
	post_user_id INTEGER,
	PRIMARY KEY(log_id)
);

CREATE INDEX idx_ml_user ON jforum_moderation_log(user_id);
CREATE INDEX idx_ml_post_user ON jforum_moderation_log(post_user_id);

--
-- Table structure for table 'jforum_topic_tag'
-- 
CREATE SEQUENCE jforum_topics_tag_seq;
CREATE TABLE jforum_topics_tag (
  tag_id        INTEGER NOT NULL DEFAULT NEXTVAL('jforum_topics_tag_seq'),
  topic_id      INTEGER NOT NULL,
  tag_name      varchar(50) NOT NULL,
  PRIMARY KEY(tag_id)
);

CREATE INDEX idx_tag_name ON jforum_topics_tag(tag_name);
CREATE INDEX idx_tag_topic ON jforum_topics_tag(topic_id);

--
-- Table structure for table 'jforum_avatar'
-- 
CREATE SEQUENCE jforum_avatar_seq;
CREATE TABLE jforum_avatar (
  id      INTEGER NOT NULL DEFAULT NEXTVAL('jforum_avatar_seq'),
  file_name    varchar(255) NOT NULL,
  avatar_type  varchar(255),
  width   INTEGER DEFAULT 0,
  height  INTEGER DEFAULT 0,
  PRIMARY KEY(id)
);

--
-- Table structure for table 'jforum_shout_box'
-- 
CREATE SEQUENCE jforum_shoutbox_seq;
CREATE TABLE jforum_shoutbox (
  id              INTEGER NOT NULL DEFAULT NEXTVAL('jforum_shoutbox_seq'),
  category_id     INTEGER NOT NULL default 1,
  shout_length    INTEGER NOT NULL default 0,
  allow_anonymous boolean default false,
  disabled        boolean default false,
  PRIMARY KEY(id)
);

CREATE INDEX idx_shoutbox_category ON jforum_shoutbox(category_id);

--
-- Table structure for table 'jforum_shouts'
-- 
CREATE SEQUENCE jforum_shouts_seq;
CREATE TABLE jforum_shouts (
  shout_id     INTEGER NOT NULL DEFAULT NEXTVAL('jforum_shouts_seq'),
  shout_box_id INTEGER NOT NULL default 1,
  user_id      INTEGER NOT NULL,
  shouter_name VARCHAR(30),
  shout_text   VARCHAR(255) NOT NULL,
  shouter_ip   VARCHAR(50),
  shout_time   TIMESTAMP NOT NULL,
  PRIMARY KEY ( shout_id )
);

CREATE INDEX idx_shouts_user ON jforum_shouts(user_id);

--
-- Table structure for table 'jforum_limited_time_seq'
-- 
CREATE SEQUENCE jforum_limited_time_seq;
CREATE TABLE jforum_forums_limited_time (
  id     INTEGER NOT NULL DEFAULT NEXTVAL('jforum_limited_time_seq'),
  forum_id INTEGER NOT NULL,
  limited_time      INTEGER default 0,
  PRIMARY KEY ( id )
);

CREATE INDEX idx_forum_ltd_time ON jforum_forums_limited_time(forum_id);