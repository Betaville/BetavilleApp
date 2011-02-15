/** Copyright (c) 2008-2010, Brooklyn eXperimental Media Center
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Brooklyn eXperimental Media Center nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Brooklyn eXperimental Media Center BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package edu.poly.bxmc.betaville.notifier;

/**
 * @author Skye Book
 *
 */
public interface IStatusNotifier extends INotifier{
	/**
	 * Notifies the user of something in which they have no say.
	 * 
	 * Examples include chocolate being better than vanilla or
	 * Bob Dylan being the greatest singer/songwriter of all time.
	 * 
	 * A {@link ISimpleAcceptListener} may be provided to allow for
	 * an action when the accept action is triggered (Usually an OK
	 * button.. can also be a "feed me" or "rock on!" button.
	 * 
	 * @param acceptListener triggered when notification is dismissed/accepted
	 * @see ISimpleAcceptListener
	 */
	public void displaySimpleNotification(ISimpleAcceptListener acceptListener);
	
	/**
	 * Displays a window that allows the user a choice between two things.
	 * 
	 * This is a good way to display arbitrary options windows, such as "do you like
	 * chocolate or vanilla?" (Chocolate is the only correct answer ever possible in
	 * this particular case)
	 * @param affirmativeButtonText Text for the button that will return "true"
	 * @param negativeButtonText Text for the button that will return "false"
	 * @return true or false depending on which trigger is activated.
	 */
	public boolean displayYesNoFeedback(String affirmativeButtonText, String negativeButtonText);
}
