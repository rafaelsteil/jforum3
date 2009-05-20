package org.hinjector;

import org.hibernate.SessionFactory;

/**
 * @Author Fabio Kung
 */
public interface HInjector {
	public SessionFactory support(SessionFactory factory);
}
