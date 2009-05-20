package com.redwahoo.authz;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * <p>Title: RemoteUserDetails</p>
 *
 * <p>Description: Class that will process remote authorization details</p>
 * <p>To get authorisation details of a user:
 * <code>
 *    RemoteUserDetails auth = new RemoteUserDetails();
 *    auth.getDetails("http://govdex.jawasoft.com:8085/user/remoteauth.do", uid);
 * </code>
 * After that you could get user information (including joined groups).
 * </p>
 *
 * Update: now organisations is supported (v2)
 *
 * Update: now group description is supported for groups and ownedgroup (v3)
 *
 * TODO : XML Encryption Security
 *
 * @author sverdianto
 * @version 1.0
 *
 */
@SuppressWarnings("unchecked")
public class RemoteUserDetails {
    private String username;
    private String email;
    private String fullname;
    private List<String> groupNames = new ArrayList<String>();
    private List<String> ownedGroupNames = new ArrayList<String>();
	private List<String> organisationNames = new ArrayList<String>();
    private String remoteUrl;
	HashMap groupMap = new HashMap();
	HashMap ownedGroupMap = new HashMap();

    public RemoteUserDetails() {
        try {
            Properties p = new Properties();
            p.load(this.getClass().getResourceAsStream("/redwahoo/authz.properties"));
            remoteUrl = p.getProperty("authz.url");
        } catch (IOException ex) {
        	throw new RuntimeException("Unable to load authz.properties", ex);
        }
    }

    /**
     * Resolve details
     *
     * @param remoteUrl String
     * @param uid String
     */
    public void getDetails(String uid) {
        String url = remoteUrl + "?uid=" + uid;

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode == HttpStatus.SC_OK) {
                // Read the response body.
                byte[] responseBody = method.getResponseBody();

                // Deal with the response.
                parse(new String(responseBody));
            }
        }
        catch (Exception e) {
            // catch all exception, ignore exception
            e.printStackTrace();
        }
        finally {
            // Release the connection.
            method.releaseConnection();
        }
    }

    private void parse(String xml) throws Exception {
        // using SAX parser to build the Document
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xml));

        // get root Element
        Element root = doc.getRootElement();

        if ("user".equals(root.getName()) && "success".equals(root.getAttributeValue("result"))) {
            username = root.getChildTextTrim("username");
            fullname = root.getChildTextTrim("fullname");
            email = root.getChildTextTrim("email");

            List<?> groupList = root.getChild("groups").getChildren("group");
            for (Iterator<?> i = groupList.listIterator(); i.hasNext();){
            	Element group = (Element)i.next();
            	//groupNames.add(group.getTextTrim());
            	groupNames.add(group.getChildTextTrim("name"));
            	groupMap.put(group.getChildTextTrim("name"), group.getChildTextTrim("description"));

            }

            List<?> ownedGroupList = root.getChild("owned-groups").getChildren("group");
            for (Iterator<?> i = ownedGroupList.listIterator(); i.hasNext(); ) {
                Element group = (Element) i.next();
                //ownedGroupNames.add(group.getTextTrim());
                ownedGroupNames.add(group.getChildTextTrim("name"));
                ownedGroupMap.put(group.getChildTextTrim("name"), group.getChildTextTrim("description"));


            }

            List<?> organisationList = root.getChild("organisations").getChildren("organisation");
            for (Iterator<?> i = organisationList.listIterator(); i.hasNext(); ) {
                Element group = (Element) i.next();
                organisationNames.add(group.getTextTrim());
            }
        }
    }

    /**
     * get authenticated username/uid
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * get user email
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * get user fullname
     *
     * @return String
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * get list of groups joined by authenticated user
     *
     * @return List
     */
    public List<String> getGroupNames() {
        return groupNames;
    }

    /**
     * get list of organisations joined by authenticated user
     *
     * @return
     */
    public List<String> getOrganisationNames() {
    	return organisationNames;
    }

    /**
     *
     * @return
     */
    public List<String> getOwnedGroupNames() {
		return ownedGroupNames;
	}

    /**
     *
     * @param ownedGroupNames
     */
	public void setOwnedGroupNames(List<String> ownedGroupNames) {
		this.ownedGroupNames = ownedGroupNames;
	}

	public String getGroupDesc(String groupName){
		String groupDesc = "";

		groupDesc = (String)groupMap.get(groupName);

		return groupDesc;
	}

	public Map getGroupMap(){
		return groupMap;
	}

	public String getOwnedGroupDescription(String groupName){
		String groupDesc = "";

		groupDesc = (String)ownedGroupMap.get(groupName);

		return groupDesc;
	}

	public Map getOwnedGroupMap(){
		return ownedGroupMap;
	}

    /**
     * For testing purpose
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        RemoteUserDetails auth = new RemoteUserDetails();
        auth.getDetails("administrator");
        System.out.println(auth.getUsername());
        System.out.println(auth.getEmail());
        System.out.println(auth.getOwnedGroupNames());
        System.out.println(auth.getGroupNames());
        System.out.println(auth.getOrganisationNames());
        System.out.println(auth.getGroupMap());
    }
}
