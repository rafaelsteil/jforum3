--
-- Table structure for table 'jforum_banlist'
--
DROP TABLE IF EXISTS jforum_banlist;
CREATE TABLE jforum_banlist (
  banlist_id INT NOT NULL auto_increment,
  user_id INT,
  banlist_ip varchar(15),
  banlist_email varchar(255),
  PRIMARY KEY (banlist_id),
  INDEX idx_user (user_id),
  INDEX (banlist_ip),
  INDEX (banlist_email)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_categories'
--
DROP TABLE IF EXISTS jforum_categories;
CREATE TABLE jforum_categories (
  category_id INT NOT NULL auto_increment,
  category_title varchar(100) NOT NULL default '',
  category_order INT NOT NULL default '0',
  category_moderated TINYINT(1) DEFAULT '0',
  category_theme_id INT,
  PRIMARY KEY  (category_id),
  KEY (category_theme_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_config'
--
DROP TABLE IF EXISTS jforum_config;
CREATE TABLE jforum_config (
  config_name varchar(255) NOT NULL default '',
  config_value varchar(255) NOT NULL default '',
  config_id int not null auto_increment,
  PRIMARY KEY(config_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_forums'
--
DROP TABLE IF EXISTS jforum_forums;
CREATE TABLE jforum_forums (
  forum_id INT NOT NULL auto_increment,
  category_id INT NOT NULL default '1',
  forum_name varchar(150) NOT NULL default '',
  forum_description varchar(255) default NULL,
  forum_order INT default '1',
  forum_last_post_id INT default '0',
  forum_moderated TINYINT(1) DEFAULT '0',
  forum_allow_anonymous_posts tinyint(1) default 0,
  PRIMARY KEY  (forum_id),
  KEY (category_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_forums_watch'
--
DROP TABLE IF EXISTS jforum_forums_watch;
CREATE TABLE jforum_forums_watch (
  forum_id INT NOT NULL,
  user_id INT NOT NULL,
  INDEX idx_fw_forum (forum_id),
  INDEX idx_fw_user (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_groups'
--
DROP TABLE IF EXISTS jforum_groups;
CREATE TABLE jforum_groups (
  group_id INT NOT NULL auto_increment,
  group_name varchar(40) NOT NULL default '',
  group_description varchar(255) default NULL,
  PRIMARY KEY  (group_id)
) ENGINE=InnoDB;


DROP TABLE IF EXISTS jforum_user_groups;
CREATE TABLE jforum_user_groups (
	group_id INT NOT NULL,
	user_id INT NOT NULL,
	INDEX idx_group (group_id),
	INDEX idx_user (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_roles'
--
DROP TABLE IF EXISTS jforum_roles;
CREATE TABLE jforum_roles (
  role_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_id INT default '0',
  name varchar(255) NOT NULL,
  INDEX idx_group (group_id),
  INDEX idx_name (name)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_role_values'
--
DROP TABLE IF EXISTS jforum_role_values;
CREATE TABLE jforum_role_values (
  role_id INT NOT NULL,
  role_value INT NOT NULL,
  INDEX idx_role(role_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_post_report'
--
DROP TABLE IF EXISTS jforum_post_report;
CREATE TABLE jforum_post_report (
	report_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	report_date DATETIME,
	report_description VARCHAR(255),
	report_status VARCHAR(10) default 'UNRESOLVED',
	post_id INT NOT NULL,
	user_id INT NOT NULL,
	INDEX idx_report_user(user_id),
	INDEX idx_report_post(post_id)
);

--
-- Table structure for table 'jforum_posts'
--
DROP TABLE IF EXISTS jforum_posts;
CREATE TABLE jforum_posts (
  post_id INT NOT NULL auto_increment,
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  forum_id INT NOT NULL,
  post_date datetime default NULL,
  poster_ip varchar(50) default NULL,
  enable_bbcode tinyint(1) DEFAULT 1,
  enable_html tinyint(1) DEFAULT 1,
  enable_smilies tinyint(1) DEFAULT 1,
  enable_sig tinyint(1) DEFAULT 1,
  post_edit_time datetime,
  post_edit_count INT DEFAULT 0,
  status tinyint(1) default 1,
  attach TINYINT(1) DEFAULT 0,
  need_moderate TINYINT(1) DEFAULT 0,
  post_subject varchar(110) NOT NULL,
  post_text TEXT NOT NULL,
  PRIMARY KEY  (post_id),
  KEY (user_id),
  KEY (topic_id),
  KEY (post_date),
  KEY (forum_id),
  INDEX (need_moderate)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_privmsgs'
--
DROP TABLE IF EXISTS jforum_privmsgs;
CREATE TABLE jforum_privmsgs (
  privmsgs_id INT NOT NULL auto_increment,
  privmsgs_type tinyint(4) NOT NULL default '0',
  privmsgs_subject varchar(255) NOT NULL default '',
  privmsgs_from_userid INT NOT NULL default '0',
  privmsgs_to_userid INT NOT NULL default '0',
  privmsgs_date datetime default null,
  privmsgs_ip varchar(50) default '',
  privmsgs_enable_bbcode tinyint(1) NOT NULL default '1',
  privmsgs_enable_html tinyint(1) NOT NULL default '0',
  privmsgs_enable_smilies tinyint(1) NOT NULL default '1',
  privmsgs_attach_sig tinyint(1) NOT NULL default '1',
  privmsgs_text TEXT,
  PRIMARY KEY  (privmsgs_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_ranks'
--
DROP TABLE IF EXISTS jforum_ranks;
CREATE TABLE jforum_ranks (
  rank_id INT NOT NULL auto_increment,
  rank_title varchar(50) NOT NULL default '',
  rank_min INT NOT NULL default '0',
  rank_special tinyint(1) default NULL,
  rank_image varchar(255) default NULL,
  PRIMARY KEY  (rank_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_sessions'
--
DROP TABLE IF EXISTS jforum_sessions;
CREATE TABLE jforum_sessions (
  user_id INT NOT NULL PRIMARY KEY,
  session_start datetime,
  session_last_accessed datetime,
  session_last_visit datetime,
  session_ip varchar(50) default ''
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_smilies'
--
DROP TABLE IF EXISTS jforum_smilies;
CREATE TABLE jforum_smilies (
  smilie_id INT NOT NULL auto_increment,
  code varchar(50) NOT NULL default '',
  disk_name varchar(255),
  PRIMARY KEY  (smilie_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_themes'
--
DROP TABLE IF EXISTS jforum_themes;
CREATE TABLE jforum_themes (
  theme_id INT NOT NULL auto_increment,
  template_name varchar(30),
  style_name varchar(30),
  PRIMARY KEY  (theme_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_topics'
--
DROP TABLE IF EXISTS jforum_topics;
CREATE TABLE jforum_topics (
  topic_id INT NOT NULL auto_increment,
  forum_id INT NOT NULL default 0,
  topic_subject varchar(100) NOT NULL,
  user_id INT NOT NULL,
  topic_date datetime default null,
  topic_views INT default 1,
  topic_replies INT default 0,
  topic_status tinyint(3) default 0,
  topic_vote_id INT default null,
  topic_type tinyint(3) default 0,
  topic_first_post_id INT default 0,
  topic_last_post_id INT default 0,
  topic_moved_id INT DEFAULT 0,
  need_moderate TINYINT(1) DEFAULT 0,
  has_attachment TINYINT(1) DEFAULT 0,
  PRIMARY KEY  (topic_id),
  KEY (forum_id),
  KEY(user_id),
  KEY(topic_first_post_id),
  KEY(topic_last_post_id),
  KEY(topic_moved_id),
  KEY(need_moderate)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_topics_watch'
--
DROP TABLE IF EXISTS jforum_topics_watch;
CREATE TABLE jforum_topics_watch (
  topics_watch_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  is_read tinyint(1) DEFAULT 1,
  INDEX idx_topic (topic_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_users'
--
DROP TABLE IF EXISTS jforum_users;
CREATE TABLE jforum_users (
  user_id INT NOT NULL auto_increment,
  user_active tinyint(1) default 1,
  username varchar(50) NOT NULL default '',
  user_password varchar(32) NOT NULL default '',
  user_session_time bigint default 0,
  user_session_page INT NOT NULL default '0',
  user_lastvisit datetime default null,
  user_regdate datetime default null,
  user_posts INT NOT NULL default '0',
  user_timezone varchar(5),
  user_style tinyint(4) default NULL,
  user_lang varchar(10),
  user_dateformat varchar(20) default '%d/%M/%Y %H:%i',
  user_new_privmsg int default '0',
  user_unread_privmsg int default '0',
  user_last_privmsg datetime NULL,
  user_emailtime datetime default NULL,
  user_viewemail tinyint(1) default '0',
  user_attachsig tinyint(1) default '1',
  user_allowhtml tinyint(1) default '0',
  user_allowbbcode tinyint(1) default '1',
  user_allowsmilies tinyint(1) default '1',
  user_allowavatar tinyint(1) default '1',
  user_allow_pm tinyint(1) default '1',
  user_allow_viewonline tinyint(1) default '1',
  user_notify tinyint(1) default '1',
  user_notify_always tinyint(1) default '0',
  user_notify_text tinyint(1) default '0',
  user_notify_pm tinyint(1) default '1',
  user_popup_pm tinyint(1) default '1',
  rank_id INT,
  avatar_id int,
  user_email varchar(255) default '',
  user_website varchar(255) default NULL,
  user_from varchar(100) default NULL,
  user_sig text,
  user_aim varchar(255) default NULL,
  user_yim varchar(255) default NULL,
  user_msnm varchar(255) default NULL,
  user_occ varchar(100) default NULL,
  user_interests varchar(255) default NULL,
  user_biography text DEFAULT NULL,
  user_actkey varchar(32) default NULL,
  gender char(1) default NULL,
  themes_id INT default 0,
  deleted tinyint(1) default 0,
  user_viewonline tinyint(1) default '1',
  security_hash varchar(32),
  user_authhash VARCHAR(32),
  PRIMARY KEY  (user_id),
  INDEX idx_user_avatar(avatar_id),
  INDEX idx_user_ranking(rank_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_vote_desc'
--
DROP TABLE IF EXISTS jforum_vote_desc;
CREATE TABLE jforum_vote_desc (
  vote_id INT NOT NULL auto_increment,
  vote_text varchar(255) NOT NULL,
  vote_start datetime,
  vote_length int(11) default '0',
  PRIMARY KEY  (vote_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_vote_results'
--
DROP TABLE IF EXISTS jforum_vote_results;
CREATE TABLE jforum_vote_results (
  vote_option_id tinyint(4) NOT NULL PRIMARY KEY AUTO_INCREMENT,  
  vote_id INT NOT NULL default 0,
  vote_option_text varchar(255) NOT NULL,
  vote_result int(11) default 0,
  INDEX(vote_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_vote_voters'
--
DROP TABLE IF EXISTS jforum_vote_voters;
CREATE TABLE jforum_vote_voters (
  voter_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  vote_id INT NOT NULL,
  vote_user_id INT NOT NULL,
  vote_user_ip varchar(50) default '',
  INDEX(vote_id),
  INDEX(vote_user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_words'
--
DROP TABLE IF EXISTS jforum_words;
CREATE TABLE jforum_words (
  word_id INT NOT NULL auto_increment,
  word varchar(100) NOT NULL default '',
  replacement varchar(100) NOT NULL default '',
  PRIMARY KEY  (word_id)
) ENGINE=InnoDB;

-- 
-- Table structure for table 'jforum_quota_limit'
--
DROP TABLE IF EXISTS jforum_quota_limit;
CREATE TABLE jforum_quota_limit (
	quota_limit_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	quota_desc VARCHAR(50) NOT NULL,
	quota_limit INT NOT NULL,
	quota_type TINYINT(1) DEFAULT '1'
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_extension_groups'
--
DROP TABLE IF EXISTS jforum_extension_groups;
CREATE TABLE jforum_extension_groups (
	extension_group_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	allow TINYINT(1) DEFAULT '1', 
	upload_icon VARCHAR(100),
	download_mode TINYINT(1) DEFAULT '1'
) ENGINE=InnoDB;

-- 
-- Table structure for table 'jforum_extensions'
--
DROP TABLE IF EXISTS jforum_extensions;
CREATE TABLE jforum_extensions (
	extension_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	extension_group_id INT NOT NULL,
	description VARCHAR(100),
	upload_icon VARCHAR(100),
	extension VARCHAR(10),
	allow TINYINT(1) DEFAULT '1',
	KEY(extension_group_id),
	INDEX(extension)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_attach'
--
DROP TABLE IF EXISTS jforum_attach;
CREATE TABLE jforum_attach (
	attach_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	post_id INT,
	download_count INT,
	physical_filename VARCHAR(255) NOT NULL,
	real_filename VARCHAR(255) NOT NULL,
	description VARCHAR(255),
	mimetype VARCHAR(255),
	filesize INT,
	upload_date DATETIME,
	file_extension varchar(5),
	thumb TINYINT(1) DEFAULT 0,
	INDEX idx_att_post(post_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_attach_quota'
--
DROP TABLE IF EXISTS jforum_attach_quota;
CREATE TABLE jforum_attach_quota (
	attach_quota_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	group_id INT NOT NULL,
	quota_limit_id INT NOT NULL,
	KEY(group_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_moderation_log'
-- 
DROP TABLE IF EXISTS jforum_moderation_log;
CREATE TABLE jforum_moderation_log (
	log_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	log_description TEXT NOT NULL,
	log_original_message TEXT,
	log_date DATETIME NOT NULL,
	log_type TINYINT DEFAULT 0,
	post_id INT DEFAULT 0,
	topic_id INT DEFAULT 0,
	post_user_id INT DEFAULT 0,
	KEY(user_id),
	KEY(post_user_id)
) ENGINE=InnoDB;

--
-- Table structure for table 'jforum_topic_tag'
-- 
DROP TABLE IF EXISTS jforum_topics_tag;
CREATE TABLE jforum_topics_tag (
  tag_id        INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  topic_id      INT NOT NULL,
  tag_name      varchar(50) NOT NULL,
  INDEX idx_att_tag_name (tag_name),
  INDEX idx_att_tag_topic(topic_id)
)ENGINE=InnoDB;

--
-- Table structure for table 'jforum_avatar'
-- 
DROP TABLE IF EXISTS jforum_avatar;
CREATE TABLE jforum_avatar (
  id      INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  file_name    varchar(255) NOT NULL,
  avatar_type  varchar(255),
  width   smallint(4) unsigned default 0,
  height  smallint(4) unsigned default 0
)ENGINE=InnoDB;

--
-- Table structure for table 'jforum_shout_box'
-- 
DROP TABLE IF EXISTS jforum_shoutbox;
CREATE TABLE jforum_shoutbox (
  id              INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  category_id     INT NOT NULL default '1',
  shout_length    INT NOT NULL default '0',
  allow_anonymous tinyint(1) NOT NULL DEFAULT '1',
  disabled        tinyint(1) NOT NULL DEFAULT '0',
  INDEX idx_shoutbox_category(category_id)
);

--
-- Table structure for table 'jforum_shouts'
-- 
DROP TABLE IF EXISTS jforum_shouts;
CREATE TABLE jforum_shouts (
  shout_id     INT UNSIGNED NOT NULL AUTO_INCREMENT,
  shout_box_id INT NOT NULL default '1',
  user_id      INT NOT NULL,
  shouter_name VARCHAR(30),
  shout_text   VARCHAR(255) NOT NULL,
  shouter_ip   VARCHAR(50),
  shout_time   datetime NOT NULL,
  PRIMARY KEY ( shout_id ),
  INDEX idx_shouts_user(user_id)
);

-- Table structure for table 'jforum_forums_limited_time';
--
DROP TABLE IF EXISTS jforum_forums_limited_time;
CREATE TABLE jforum_forums_limited_time (
  id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  forum_id INT NOT NULL,
  limited_time INT DEFAULT 0,
  INDEX idx_limited_forum(forum_id)
);
