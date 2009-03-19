/****************************************************************************
 * Copyright 2008-2011 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Initial Contributors:
 *   Håkan Råberg
 *   Manish Chakravarty
 *   Pavan K S
 ***************************************************************************/
package com.thoughtworks.twist.driver.web.browser.wait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

import com.thoughtworks.twist.driver.web.browser.BrowserFamily;
import com.thoughtworks.twist.driver.web.browser.BrowserSession;

public class DocumentReadyWaitStrategy implements WaitStrategy, LocationListener {
 Logger log = LoggerFactory.getLogger(getClass());

	private BrowserSession session;
	private boolean isDomReady = true;
	private BrowserFunction documentIsReady;

	public void init(final BrowserSession session) {
		this.session = session;
		session.getBrowser().addLocationListener(this);
	}

	public void changed(final LocationEvent event) {
		if (event.top) {
			isDomReady = false;

			if (documentIsReady != null) {
				documentIsReady.dispose();
			}
			documentIsReady = new BrowserFunction(session.getBrowser(), "documentIsReady") {
				public Object function(Object[] arguments) {
					isDomReady = true;
					log.debug("DOM is ready: {}", event.location);
					return null;
				}
			};
			session.inject("twist-domready.js", getClass());
			session.execute("Twist.DomReady.ready(documentIsReady)");
		}
	}

	public void changing(LocationEvent event) {
	}

	public boolean isBusy() {
		return !isDomReady;
	}
}
