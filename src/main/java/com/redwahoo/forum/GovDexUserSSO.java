package com.redwahoo.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.jforum.entities.Group;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.repository.GroupRepository;
import net.jforum.repository.UserRepository;
import net.jforum.sso.SSO;
import net.jforum.util.ConfigKeys;
import net.jforum.util.JForumConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

import com.redwahoo.authz.RemoteUserDetails;

import edu.yale.its.tp.cas.client.filter.CASFilter;

public class GovDexUserSSO implements SSO {
	private static final Logger logger = Logger.getLogger(GovDexUserSSO.class);
	private UserRepository userRepository = null;
	private GroupRepository groupRepository = null;

	private JForumConfig config;

	/**
	 * @see net.jforum.sso.SSO#authenticateUser(javax.servlet.http.HttpServletRequest)
	 */
	public String authenticateUser(HttpServletRequest request) {
		HttpSession session = request.getSession();

		ApplicationContext springContext = (ApplicationContext)session.getServletContext().getAttribute(ConfigKeys.SPRING_CONTEXT);
		userRepository = (UserRepository) springContext.getBean(UserRepository.class.getName());
		groupRepository = (GroupRepository) springContext.getBean(GroupRepository.class.getName());

		String username = (String)session.getAttribute(CASFilter.CAS_FILTER_USER);

		if (!StringUtils.isEmpty(username)) {
			username = username.trim().toLowerCase();
			User user = userRepository.getByUsername(username);

			RemoteUserDetails rud = new RemoteUserDetails();
			rud.getDetails(username);

			if (user == null) {
				// New user
				user = new User();

				user.setUsername(rud.getUsername().trim().toLowerCase());
				user.setEmail(rud.getEmail());
				user.setLastName(rud.getFullname());
				user.setRegistrationDate(new Date());

				Random r = new Random();
				String pass = Long.toString(Math.abs(r.nextLong()), 32);
				user.setPassword(pass);

				SessionFactory sessionFactory = (SessionFactory)springContext.getBean(SessionFactory.class.getName());
				userRepository.add(user);
				sessionFactory.getCurrentSession().getTransaction().commit();
				sessionFactory.getCurrentSession().clear();
				sessionFactory.getCurrentSession().beginTransaction();

				user = userRepository.get(user.getId());

				logger.info(String.format("Added new user. Username is %s, id is %d. Session ID: %s", user.getUsername(), user.getId(), request.getSession().getId()));
			}
			else {
				username = username.toLowerCase();

				 // Synchronize user membership
				user.setUsername(rud.getUsername().trim().toLowerCase());
				user.setEmail(rud.getEmail());
				user.setLastName(rud.getFullname());

				userRepository.update(user);

				logger.info(String.format("Updated user. username %s, session ID %s", user.getUsername(), request.getSession().getId()));
			}

			this.syncGroup(rud, user);
		}
		else {
			logger.warn("CAS_FILTER_USER returned null, which means the user is null as well. Session ID: " + request.getSession().getId());
		}

		return username;
	}

	/**
	 * @see net.jforum.sso.SSO#isSessionValid(net.jforum.entities.UserSession, javax.servlet.http.HttpServletRequest)
	 */
	public boolean isSessionValid(UserSession userSession, HttpServletRequest request) {
		String remoteUser = (String) request.getSession().getAttribute(CASFilter.CAS_FILTER_USER);

		if (!StringUtils.isEmpty(remoteUser)) {
			remoteUser = remoteUser.toLowerCase();
		}

		if (config == null){
			ServletContext context = request.getSession().getServletContext();
			ApplicationContext springContext = (ApplicationContext)context.getAttribute(ConfigKeys.SPRING_CONTEXT);
			config = (JForumConfig) springContext.getBean(JForumConfig.class.getName());
		}

		int anonymousUserId = config.getInt(ConfigKeys.ANONYMOUS_USER_ID);
		int userID = anonymousUserId;
		String sessionUsername = "";

		User user = userSession.getUser();

		if (user == null) {
			logger.warn("userSession.getUser() returned null. Session ID: " + request.getSession().getId());
			return false;
		}
		else {
			if (!StringUtils.isEmpty(user.getUsername())) {
				sessionUsername = user.getUsername().toLowerCase();
			}
			else {
				logger.warn(String.format("Username for user.id %d is null. Session ID: %s", user.getId(), request.getSession().getId()));
			}

			userID = user.getId();
		}

		// user has since logged out
		if (remoteUser == null && userID != anonymousUserId) {
			logger.info(String.format("remoteUser is null (logged out?). Session is no longer valid. logged flag is %s. Is http session new? %s. Session ID: %s",
				userSession.isLogged(), request.getSession().isNew(), request.getSession().getId()));
			return false;
		}
		// user has since logged in
		else if (remoteUser != null && userID == anonymousUserId) {
			logger.info("Remote user is not null (" + remoteUser + "), but userId is equal to anonymous. Session invalid. ID: " + request.getSession().getId());
			return false;
		}
		// user has changed user
		else if (remoteUser != null && !remoteUser.equals(sessionUsername)) {
			logger.info(String.format("Remote user is %s, user.username is %s. Session invalid. ID: %s", remoteUser, sessionUsername, request.getSession().getId()));
			return false;
		}

		return true;
	}

	/**
	 * @see net.jforum.sso.SSO#setConfig(net.jforum.util.JForumConfig)
	 */
	public void setConfig(JForumConfig config) {
		this.config = config;
	}

	private void syncGroup(RemoteUserDetails rud, User user) {
        // group mapping from LDAP to JForum

        List<String> groupNameInLdap = rud.getGroupNames(); // get user group name list from LDAP
        List<Group> groupInJforum = groupRepository.getAllGroups(); // get all groups in JForum
        List<String> groupNameInBoth = new ArrayList<String>(); // list of group name in both LDAP & JForum DB
        List<String> groupNameOnlyJforum = new ArrayList<String>(); // list of group name in JForum Only

        for (Group group : groupInJforum){
             groupNameInBoth.add(group.getName());
             groupNameOnlyJforum.add(group.getName());
        }

        groupNameInBoth.retainAll(groupNameInLdap); // retain the same groups in ldap and jforum
        groupNameOnlyJforum.removeAll(groupNameInLdap);// the group in JForum DB only

        //add the group that both in LDAP & JForum DB to the user
        for (String groupName : groupNameInBoth) {
             Group group = groupRepository.getByName(groupName);
             user.addGroup(group);
        }

        groupNameInLdap.removeAll(groupNameInBoth); // Group Name in LDAP Only

       //create the group which is in LDAP Only, in JForum DB
        for (String groupName : groupNameInLdap){
            Group group = new Group();
            group.setName(groupName);
            group.setDescription("LDAP Group");
            groupRepository.add(group);

            user.addGroup(group);
        }

        //delete user from the group they're not belong too,
        //these groups are in not in LDAP, but Jforum DB only.
        List<Group> userGroups = user.getGroups();
		for (String gropName : groupNameOnlyJforum){
			Group group = groupRepository.getByName(gropName);

			//if this user has such group, remove from this group
			if  (userGroups.contains(group)){
				userGroups.remove(group);
			}
		}

		if (user.getGroups().size() == 0) {
			logger.warn(String.format("Group sync ended with an empty list of groups for user %s.", user.getUsername()));
		}

		//save the group changes
		userRepository.update(user);
	}
}
