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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.jforum.entities.Poll;
import net.jforum.entities.PollOption;
import net.jforum.entities.Topic;

import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.ioc.Component;

/**
 * @author Rafael Steil
 */
@Component
public class PollService {
	public void processChanges(Poll originalPoll, List<PollOption> options) {
		PollChanges changes = new PollChanges(originalPoll);
		changes.processChanges(options);
	}

	public void associatePoll(Topic topic, List<PollOption> pollOptions) {
		if (topic.getPoll() == null) {
			return;
		}

		if (StringUtils.isEmpty(topic.getPoll().getLabel()) || pollOptions == null) {
			topic.setPoll(null);
			return;
		}

		topic.getPoll().setStartDate(new Date());

		for (Iterator<PollOption> iterator = pollOptions.iterator(); iterator.hasNext();) {
			PollOption option = iterator.next();

			if (StringUtils.isEmpty(option.getText())) {
				iterator.remove();
			}
			else {
				option.setPoll(topic.getPoll());
			}
		}

		if (pollOptions.size() == 0) {
			topic.setPoll(null);
		}
		else {
			topic.getPoll().setOptions(pollOptions);
		}
	}

	private class PollChanges {
		private Poll originalPoll;
		private List<PollOption> newOptions = new ArrayList<PollOption>();

		public PollChanges(Poll originalPoll) {
			this.originalPoll = originalPoll;
		}

		public void processChanges(List<PollOption> allOptions) {
			this.processNewAndChangedOptions(allOptions);
			this.processDeletedOptions(allOptions);
		}

		private void processDeletedOptions(List<PollOption> allOptions) {
			for (Iterator<PollOption> iterator = this.originalPoll.getOptions().iterator(); iterator.hasNext(); ) {
				PollOption currentOption = iterator.next();

				if (this.findOption(currentOption.getId(), allOptions) == null) {
					iterator.remove();
				}
			}
		}

		private void processNewAndChangedOptions(List<PollOption> allOptions) {
			for (PollOption option : allOptions) {
				if (option.getId() == 0) {
					this.newOptions.add(option);
					option.setPoll(this.originalPoll);
				}
				else {
					PollOption originalOption = this.findOption(option.getId(), this.originalPoll.getOptions());

					if (originalOption != null && !StringUtils.isEmpty(option.getText())
							&& !originalOption.getText().equals(option.getText())) {
						originalOption.setText(option.getText());
					}
				}
			}

			this.originalPoll.getOptions().addAll(this.newOptions);
		}

		private PollOption findOption(int optionId, List<PollOption> options) {
			for (PollOption option : options) {
				if (option.getId() == optionId) {
					return option;
				}
			}

			return null;
		}
	}
}
