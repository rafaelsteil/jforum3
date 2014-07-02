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
package net.jforum.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;

import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class PollServiceTestCase {
	@Test
	public void processChanges() {
		List<PollOption> allOptions = new ArrayList<PollOption>();
		allOptions.add(this.createOption(1, "A changed"));
		allOptions.add(this.createOption(4, "D"));
		allOptions.add(this.createOption(0, "E"));

		Poll poll = this.createPoll();

		PollService service = new PollService();
		service.processChanges(poll, allOptions);

		assertEquals(3, poll.getOptions().size());
		assertFalse(poll.getOptions().contains(this.createOption(2, "B")));
		assertFalse(poll.getOptions().contains(this.createOption(3, "c")));
		assertTrue(poll.getOptions().contains(this.createOption(0, "E")));
		assertEquals("A changed", poll.getOptions().get(0).getText());
	}

	private Poll createPoll() {
		Poll p = new  Poll();

		p.getOptions().add(this.createOption(1, "A"));
		p.getOptions().add(this.createOption(2, "B"));
		p.getOptions().add(this.createOption(3, "C"));
		p.getOptions().add(this.createOption(4, "D"));

		return p;
	}

	private PollOption createOption(int id, String text) {
		PollOption o = new PollOption();

		o.setId(id);
		o.setText(text);

		return o;
	}
}
