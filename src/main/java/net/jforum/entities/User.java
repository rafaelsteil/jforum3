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
package net.jforum.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.jforum.repository.UserRepository;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;

/**
 * @author Rafael Steil
 */
@Entity
@Table(name = "jforum_users")
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Component
@PrototypeScoped
public class User implements Serializable {
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "jforum_users_seq")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sequence")
	@Column(name = "user_id")
	private int id;

	@Column(name = "user_posts")
	private int totalPosts;

	@Column(name = "user_attachsig")
	private boolean attachSignature = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rank_id")
	private Ranking ranking;

	@Column(name = "user_allowhtml")
	private boolean htmlEnabled = true;

	@Column(name = "user_allowbbcode")
	private boolean bbCodeEnabled = true;

	@Column(name = "user_allowsmilies")
	private boolean smiliesEnabled = true;

	@Column(name = "user_allowavatar")
	private boolean avatarEnabled = true;

	@Column(name = "user_allow_pm")
	private boolean privateMessagesEnabled = true;

	@Column(name = "user_allow_viewonline")
	private boolean viewOnlineEnabled = true;

	@Column(name = "user_notify_pm")
	private boolean notifyPrivateMessages = true;

	@Column(name = "user_notify")
	private boolean notifyReply = true;

	@Column(name = "user_notify_always")
	private boolean notifyAlways;

	@Column(name = "user_notify_text")
	private boolean notifyText;

	@Column(name = "username")
	@Field(store = Store.NO, index = Index.TOKENIZED)
	private String username;

	@Column(name = "user_password")
	private String password;

	@Column(name = "user_lastvisit")
	private Date lastVisit;

	@Column(name = "user_regdate")
	private Date registrationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avatar_id", referencedColumnName = "id")
	private Avatar avatar;

	@Column(name = "user_email")
	@Field(store = Store.NO, index = Index.TOKENIZED)
	private String email;

	@Column(name = "user_website")
	private String website;

	@Column(name = "user_from")
	private String from;

	@Column(name = "user_sig")
	private String signature;

	@Column(name = "user_aim")
	private String aim;

	@Column(name = "user_yim")
	private String yim;

	@Column(name = "user_msnm")
	private String msn;

	@Column(name = "user_occ")
	private String occupation;

	@Column(name = "user_interests")
	private String interests;

	@Column(name = "user_biography")
	private String biography;

	@Column(name = "gender")
	private String gender;

	@Column(name = "user_timezone")
	private String timezone;

	@Column(name = "user_lang")
	private String lang;

	@Column(name = "user_dateformat")
	private String dateFormat;

	@Column(name = "user_viewemail")
	private boolean viewEmailEnabled = true;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "jforum_user_groups", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "group_id") })
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<Group> groups = new ArrayList<Group>();

	@Column(name = "user_active")
	private boolean active = true;

	@Column(name = "user_actkey")
	private String activationKey;

	@Column(name = "deleted")
	private boolean isDeleted;

	@Transient
	@Field(store = Store.NO, index = Index.TOKENIZED)
	private String firstName;

	@Transient
	@Field(store = Store.NO, index = Index.TOKENIZED)
	private String lastName;

	@Column(name = "security_hash")
	private String securityHash;

	@Transient
	private Map<Object, Object> extra = new HashMap<Object, Object>();

	@ContainedIn
	@OneToMany(mappedBy = "user")
	@SuppressWarnings("unused")
	private List<Post> posts;

	@Transient
	private UserRepository userRepository;

	public User() { }

	@Autowired
	public User(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void addExtra(String name, Object value) {
		this.extra.put(name, value);
	}

	public Object getExtra(String name) {
		return this.extra.get(name);
	}

	public void setFirstName(String name) {
		this.firstName = name;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setLastName(String name) {
		this.lastName = name;
	}

	public String getLastNmame() {
		return this.lastName;
	}

	public String getName() {
		return this.firstName + " " + this.lastName;
	}

	public boolean isDeleted() {
		return this.isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void addGroup(Group group) {
		if (!this.groups.contains(group)) {
			this.groups.add(group);
		}
	}

	/**
	 * Gets the AIM identification
	 *
	 * @return String with the AIM ID
	 */
	public String getAim() {
		return this.aim;
	}

	/**
	 * Gets the avatar of the user
	 *
	 * @return String with the avatar
	 */
	public Avatar getAvatar() {
		return this.avatar;
	}

	/**
	 * Checks if avatar is enabled
	 *
	 * @return boolean value
	 */
	public boolean isAvatarEnabled() {
		return this.avatarEnabled;
	}

	/**
	 * Checks if BB code is enabled
	 *
	 * @return boolean value
	 */
	public boolean isBbCodeEnabled() {
		return this.bbCodeEnabled;
	}

	/**
	 * Gets the format to represent dates and time
	 *
	 * @return String with the format
	 */
	public String getDateFormat() {
		return this.dateFormat;
	}

	/**
	 * Gets the user email
	 *
	 * @return String with the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Gets the user location ( where he lives )
	 *
	 * @return String with the location
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * Gets the user gender
	 *
	 * @return String value. Possible values are <code>M</code> or <code>F</code>
	 */
	public String getGender() {
		return this.gender;
	}

	/**
	 * Checks if HTML code is enabled by default in user messages
	 *
	 * @return boolean value
	 */
	public boolean isHtmlEnabled() {
		return this.htmlEnabled;
	}

	/**
	 * Gets the user id
	 *
	 * @return int value with the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the user interests
	 *
	 * @return String literal
	 */
	public String getinterests() {
		return this.interests;
	}

	/**
	 * Gets the user language
	 *
	 * @return String value with the language chosen
	 */
	public String getLang() {
		return this.lang;
	}

	/**
	 * Gets the last visit time the user was in the forum
	 *
	 * @return long value representing the time
	 */
	public Date getLastVisit() {
		return this.lastVisit;
	}

	/**
	 * Checks if notification of new private messages is enabled
	 *
	 * @return boolean value
	 */
	public boolean getNotifyPrivateMessages() {
		return this.notifyPrivateMessages;
	}

	/**
	 * Gets the OCC
	 *
	 * @return String
	 */
	public String getOccupation() {
		return this.occupation;
	}

	/**
	 * Gets the user password
	 *
	 * @return String with the password ( in plain/text )
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Checks if user permits other people to sent private messages to him
	 *
	 * @return boolean value
	 */
	public boolean isPrivateMessagesEnabled() {
		return this.privateMessagesEnabled;
	}

	/**
	 * Gets the ranking ID of the user
	 *
	 * @return int
	 */
	public Ranking getRanking() {
		return this.ranking;
	}

	/**
	 * Gets the registration date of the user
	 *
	 * @return String value with the registration date
	 */
	public Date getRegistrationDate() {
		return this.registrationDate;
	}

	/**
	 * Gets the user signature
	 *
	 * @return String literal with the signature
	 */
	public String getSignature() {
		return this.signature;
	}

	/**
	 * Checks if smilies are enabled
	 *
	 * @return boolean value
	 */
	public boolean isSmiliesEnabled() {
		return this.smiliesEnabled;
	}

	/**
	 * Gets the timezone
	 *
	 * @return String value with the timezone
	 */
	public String getTimeZone() {
		return this.timezone;
	}

	/**
	 * Gets the total number of messages posted by the user
	 *
	 * @return int value with the total of messages
	 */
	public int getTotalPosts() {
		return this.totalPosts;
	}

	/**
	 * Gets the username
	 *
	 * @return String with the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Checks if the user permits other people to see he online
	 *
	 * @return boolean value
	 */
	public boolean isViewOnlineEnabled() {
		return this.viewOnlineEnabled;
	}

	/**
	 * Gets the user website address
	 *
	 * @return String with the URL
	 */
	public String getWebsite() {
		return this.website;
	}

	/**
	 * Gets the Yahoo messenger ID
	 *
	 * @return String with the ID
	 */
	public String getYim() {
		return this.yim;
	}

	/**
	 * Is the user's email authenticated?
	 *
	 * @return integer 1 if true
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Gets the Yahoo messenger ID
	 *
	 * @return String with the activation key that is created during user registration
	 */
	public String getActivationKey() {
		return this.activationKey;
	}

	/**
	 * Sets the aim.
	 *
	 * @param aim The aim ID to set
	 */
	public void setAim(String aim) {
		this.aim = aim;
	}

	/**
	 * Sets the avatar.
	 *
	 * @param avatar The avatar to set
	 */
	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	/**
	 * Indicates if the avatar is uploaded by user
	 *
	 * @return <code>true</code> if the avatar is current user upload
	 */
	public boolean isCustomizeAvatar(){
		return avatar != null && this.equals(avatar.getUploadedBy());
	}

	/**
	 * Sets avatar status
	 *
	 * @param avatarEnabled <code>true</code> or <code>false</code>
	 */
	public void setAvatarEnabled(boolean avatarEnabled) {
		this.avatarEnabled = avatarEnabled;
	}

	/**
	 * Sets the status for BB codes
	 *
	 * @param bbCodeEnabled <code>true</code> or <code>false</code>
	 */
	public void setBbCodeEnabled(boolean bbCodeEnabled) {
		this.bbCodeEnabled = bbCodeEnabled;
	}

	/**
	 * Sets the date format.
	 *
	 * @param dateFormat The date format to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * Sets the email.
	 *
	 * @param email The email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the user location ( where he lives )
	 *
	 * @param from The location
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Sets the gender.
	 *
	 * @param gender The gender to set. Possible values must be <code>M</code> or <code>F</code>
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Enable or not HTML code into the messages
	 *
	 * @param htmlEnabled <code>true</code> or <code>false</code>
	 */
	public void setHtmlEnabled(boolean htmlEnabled) {
		this.htmlEnabled = htmlEnabled;
	}

	/**
	 * Sets the user id.
	 *
	 * @param id The user id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the interests.
	 *
	 * @param interests The interests to set
	 */
	public void setInterests(String interests) {
		this.interests = interests;
	}

	/**
	 * Sets the language.
	 *
	 * @param lang The lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * Sets the last visit time
	 *
	 * @param lastVisit Last visit time, represented as a long value
	 */
	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	/**
	 * Sets the status for notification of new private messages
	 *
	 * @param notify <code>true</code> or <code>false</code>
	 */
	public void setNotifyPrivateMessages(boolean notify) {
		this.notifyPrivateMessages = notify;
	}

	/**
	 * Sets the occ.
	 *
	 * @param occupation The occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * Sets the password.
	 *
	 * @param password The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Enable or not private messages to the user
	 *
	 * @param privateMessagesEnabled <code>true</code> or <code>false</code>
	 */
	public void setPrivateMessagesEnabled(boolean privateMessagesEnabled) {
		this.privateMessagesEnabled = privateMessagesEnabled;
	}

	/**
	 * Sets the ranking id
	 *
	 * @param rankId The id of the ranking
	 */
	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

	/**
	 * Sets the registration date.
	 *
	 * @param registrationDate The registration date to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * Sets the signature.
	 *
	 * @param signature The signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * Enable or not smilies in messages
	 *
	 * @param smilesEnabled <code>true</code> or <code>false</code>
	 */
	public void setSmiliesEnabled(boolean smilesEnabled) {
		this.smiliesEnabled = smilesEnabled;
	}

	/**
	 * Sets the Timezone.
	 *
	 * @param timeZone The Timezone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timezone = timeZone;
	}

	/**
	 * Sets the total number of posts by the user
	 *
	 * @param totalPosts int value with the total of messages posted by the user
	 */
	public void setTotalPosts(int totalPosts) {
		this.totalPosts = totalPosts;
	}

	/**
	 * Sets the username.
	 *
	 * @param username The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets the viewOnlineEnabled.
	 *
	 * @param viewOnlineEnabled The viewOnlineEnabled to set
	 */
	public void setViewOnlineEnabled(boolean viewOnlineEnabled) {
		this.viewOnlineEnabled = viewOnlineEnabled;
	}

	/**
	 * Sets the webSite.
	 *
	 * @param webSite The webSite to set
	 */
	public void setWebsite(String webSite) {
		this.website = webSite;
	}

	/**
	 * Sets the Yahoo messenger ID
	 *
	 * @param yim The yim to set
	 */
	public void setYim(String yim) {
		this.yim = yim;
	}

	/**
	 * @return
	 */
	public String getMsn() {
		return this.msn;
	}

	/**
	 * @param string
	 */
	public void setMsn(String string) {
		this.msn = string;
	}

	/**
	 * @return
	 */
	public boolean getNotifyReply() {
		return this.notifyReply;
	}

	/**
	 * @param notify
	 */
	public void setNotifyReply(boolean notify) {
		this.notifyReply = notify;
	}

	/**
	 * @return
	 */
	public boolean isViewEmailEnabled() {
		return this.viewEmailEnabled;
	}

	/**
	 * @param b
	 */
	public void setViewEmailEnabled(boolean b) {
		this.viewEmailEnabled = b;
	}

	/**
	 * @return
	 */
	public boolean getAttachSignature() {
		return this.attachSignature;
	}

	/**
	 * @param attach
	 */
	public void setAttachSignature(boolean attach) {
		this.attachSignature = attach;
	}

	/**
	 * @return Returns the privateMessagesCount.
	 */
	public int getTotalUnreadPrivateMessages() {
		this.validateUserRepository();
		return this.userRepository.getTotalUnreadPrivateMessages(this);
	}

	/**
	 * Set when user authenticates his email after user registration
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	/**
	 * Gets the user's biography
	 *
	 * @return the user biography
	 */
	public String getBiography() {
		return biography;
	}

	/**
	 * Sets the user's biography
	 *
	 * @param biography the user's biography
	 */
	public void setBiography(String biography) {
		this.biography = biography;
	}

	/**
	 * @return the notifyAlways
	 */
	public boolean getNotifyAlways() {
		return this.notifyAlways;
	}

	/**
	 * @return the notifyText
	 */
	public boolean getNotifyText() {
		return this.notifyText;
	}

	/**
	 * @param notifyAlways the notifyAlways to set
	 */
	public void setNotifyAlways(boolean notifyAlways) {
		this.notifyAlways = notifyAlways;
	}

	/**
	 * @param notifyText the notifyText to set
	 */
	public void setNotifyText(boolean notifyText) {
		this.notifyText = notifyText;
	}

	public String getSecurityHash() {
		return this.securityHash;
	}

	public void setSecurityHash(String securityHash) {
		this.securityHash = securityHash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof User)) {
			return false;
		}

		User u = (User) obj;
		return this.getId() == u.getId();
	}

	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * Increment by 1 the number of posts of this user
	 */
	public void incrementTotalPosts() {
		this.totalPosts++;
	}

	/**
	 * Decrement by 1 the number of posts of this user
	 */
	public void decrementTotalPosts() {
		this.totalPosts--;
	}

	private void validateUserRepository() {
		if (this.userRepository == null) {
			throw new IllegalStateException("UserRepository was not set");
		}
	}
}
