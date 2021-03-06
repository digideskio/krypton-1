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
package com.thoughtworks.krypton.driver.web.user.cocoa;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSPoint;

import com.thoughtworks.krypton.driver.cocoa.InitializeCocoa;
import com.thoughtworks.krypton.driver.cocoa.NSApplication;
import com.thoughtworks.krypton.driver.cocoa.NSEvent;
import com.thoughtworks.krypton.driver.cocoa.NSWindow;
import com.thoughtworks.krypton.driver.web.user.User;
import com.thoughtworks.krypton.driver.web.user.KeyTranslator.TransaltedKey;

@SuppressWarnings({ "deprecation" })
public class CocoaUser implements User {
	private static final boolean CARBON = "carbon".equals(SWT.getPlatform());
	private static final int LEFT = 1;
	private static final int RIGHT = 2;

	private NSWindow window;
	private CocoaKeyTranslator translator;
	private int modifiers;
	private final Shell shell;

	// Use -NSTraceEvents YES as application arguments to log Cocoa events to
	// System.err.
	private static NSWindow findCocoaWindowForShell() {
		NSApplication application = NSApplication.CLASS.sharedApplication();
		NSArray windows = application.windows();
		for (int i = 0; i < windows.count(); i++) {
			NSWindow window = Rococoa.cast(windows.objectAtIndex(i), NSWindow.class);
			if (window.toString().startsWith("<SWTWindow")) {
				return (NSWindow) window;
			}
		}
		throw new IllegalStateException("Could not find the SWTWindow");
	}

	public CocoaUser(Shell shell) {
		this(findCocoaWindowForShell(), shell);
	}

	public CocoaUser(final NSWindow window, final Shell shell) {
		this.window = window;
		this.shell = shell;
		translator = new CocoaKeyTranslator();
		window.setAutodisplay(false);
	}

	public void click(int x, int y) {
		click(x, y, LEFT, NSEvent.LeftMouseDown, NSEvent.LeftMouseUp);
	}

	public void doubleClick(int x, int y) {
		click(x, y);
		mouseEvent(NSEvent.LeftMouseDown, x, y, LEFT, 2, 1);
		mouseEvent(NSEvent.LeftMouseUp, x, y, LEFT, 2, 0);
	}

	public void rightClick(int x, int y) {
		click(x, y, RIGHT, NSEvent.RightMouseDown, NSEvent.RightMouseUp);
	}

	public void dragAndDrop(int startX, int startY, int endX, int endY) {
		int button = LEFT;
		mouseEvent(NSEvent.MouseMoved, startX, startY, button, 1, 0);
		mouseEvent(NSEvent.LeftMouseDown, startX, startY, button, 1, 1);
		// dragEvents(startX, startY, endX, endY, button, 10);
		mouseEvent(NSEvent.LeftMouseDragged, endX, endY, button, 1, 1);
		mouseEvent(NSEvent.LeftMouseUp, endX, endY, button, 1, 0);
	}

	// private void dragEvents(int startX, int startY, int endX, int endY, int
	// button, int speed) {
	// int x = startX;
	// int y = startY;
	//
	// int xDelta = startX < endX ? speed : -speed;
	// int yDelta = startY < endY ? speed : -speed;
	//
	// while (xDelta != 0 || yDelta != 0) {
	// if (Math.abs(y - endY) < speed) {
	// yDelta = 0;
	// }
	// if (Math.abs(x - endX) < speed) {
	// xDelta = 0;
	// }
	// x += xDelta;
	// y += yDelta;
	// mouseEvent(NSEvent.LeftMouseDragged, x, y, button, 1, 1);
	// }
	// }

	public void type(String string) {
		try {
			window.disableFlushWindow();
			for (int i = 0; i < string.length(); i++) {
				key(string.charAt(i));
			}
			pumpEvents();
		} finally {
			window.enableFlushWindow();
			window.flushWindowIfNeeded();
			// window.display();
		}
	}

	private void pumpEvents() {
		while (!shell.isDisposed() && shell.getDisplay().readAndDispatch())
			;
	}

	public void key(int c) {
		if (translator.shouldTranslateKey(c)) {
			TransaltedKey transaltedKey = translator.translate(c);
			key(transaltedKey.charCode, transaltedKey.keyCode, true);
		} else {
			key(isShiftDown() ? Character.toUpperCase(c) : c, 0, true);
		}
	}

	public void key(int c, int modifiers) {
		boolean shift = (modifiers & SWT.SHIFT) > 0;
		boolean control = (modifiers & SWT.CONTROL) > 0;
		boolean alt = (modifiers & SWT.ALT) > 0;
		boolean command = (modifiers & SWT.COMMAND) > 0;

		if (shift) {
			shiftDown();
		}
		if (control) {
			controlDown();
		}
		if (alt) {
			altDown();
		}
		if (command) {
			commandDown();
		}

		key(c);

		if (command) {
			commandUp();
		}
		if (alt) {
			altUp();
		}
		if (control) {
			controlUp();
		}
		if (shift) {
			shiftUp();
		}
	}

	public void shiftDown() {
		modifiers |= NSEvent.ShiftKeyMask;
	}

	public void shiftUp() {
		modifiers ^= NSEvent.ShiftKeyMask;
	}

	public boolean isShiftDown() {
		return (modifiers & NSEvent.ShiftKeyMask) > 0;
	}

	public void commandDown() {
		modifiers |= NSEvent.CommandKeyMask;
	}

	public void commandUp() {
		modifiers ^= NSEvent.CommandKeyMask;
	}

	public void controlDown() {
		modifiers |= NSEvent.ControlKeyMask;
	}

	public void controlUp() {
		modifiers ^= NSEvent.ControlKeyMask;
	}

	public void altDown() {
		modifiers |= NSEvent.AlternateKeyMask;
	}

	public void altUp() {
		modifiers ^= NSEvent.AlternateKeyMask;
	}

	private void key(int c, int keyCode, boolean wasTranslated) {
		keyEvent(NSEvent.KeyDown, (char) c, (short) keyCode, wasTranslated);
		keyEvent(NSEvent.KeyUp, (char) c, (short) keyCode, wasTranslated);
	}

	private void click(int x, int y, int button, int mouseDownEventType, int mouseUpEventType) {
		mouseEvent(NSEvent.MouseMoved, x, y, button, 1, 0);
		mouseEvent(mouseDownEventType, x, y, button, 1, 1);
		mouseEvent(mouseUpEventType, x, y, button, 1, 0);
	}

	private void mouseEvent(int type, final int x, final int y, int button, int clickCount, float pressure) {
		NSEvent mouseEvent = NSEvent.CLASS.mouseEventWithType_location_modifierFlags_timestamp_windowNumber_context_eventNumber_clickCount_pressure(type, new NSPoint(x, window.contentView().frame().size.height.intValue() - y),
		NSEvent.MouseEnteredMask, 0.0, window.windowNumber(), NSApplication.CLASS.sharedApplication().context(), 0, clickCount, pressure);

		ensureWindowCanAcceptEvents();
		window.postEvent_atStart(mouseEvent, false);
	}

	private void keyEvent(int type, final char c, final short keyCode, boolean wasTranslated) {
		String string = new String(new char[] { c });
		int mask = modifiers;
		// int mask = NSEvent.MouseEnteredMask | modifiers;
		// if (c == 0 || wasTranslated) {
		// mask = mask | NSEvent.OtherMouseDownMask | NSEvent.
		// OtherMouseDraggedMask;
		// }

		NSEvent keyEvent = NSEvent.CLASS.keyEventWithType_location_modifierFlags_timestamp_windowNumber_context_characters_charactersIgnoringModifiers_isARepeat_keyCode(
		type, new NSPoint(), mask, 0.0, 0, NSApplication.CLASS.sharedApplication()
						.context(), string, string, false, keyCode);

		ensureWindowCanAcceptEvents();
		window.postEvent_atStart(keyEvent, false);
	}

	private void ensureWindowCanAcceptEvents() {
		if (CARBON) {
			shell.setFocus();
		}
		if (window != null) {
			window.becomeKeyWindow();
		}
	}

	static {
		InitializeCocoa.init();
	}
}
