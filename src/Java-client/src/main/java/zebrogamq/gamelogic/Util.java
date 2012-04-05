/**
 TCM: TOTEM Communication Middleware
 Copyright: Copyright (C) 2009-2012
 Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 USA

 Developer(s): Denis Conan, Gabriel Adgeg
 */

package zebrogamq.gamelogic;

import java.util.Properties;

public class Util {

	private static Properties rabbitMQProperties = null;
	private static Properties xmlrpcProperties = null;
	private static Log logger = null;

	public static void setRabbitMQProperties(final Properties prop) {
		if (rabbitMQProperties == null) {
			rabbitMQProperties = prop;
		}
	}

	public static Properties getRabbitMQProperties() {
		return rabbitMQProperties;
	}

	public static void setXMLRPCProperties(final Properties prop) {
		if (xmlrpcProperties == null) {
			xmlrpcProperties = prop;
		}
	}

	public static Properties getXMLRPCProperties() {
		return xmlrpcProperties;
	}

	public static String getContentKeyAt(final String key, final String regex,
			final int at) {
		String content = null;
		if (key == null) {
			throw new IllegalArgumentException(
					"getContentKeyAt: search in null key");
		}
		if (regex == null) {
			throw new IllegalArgumentException(
					"getContentKeyAt: search will null regew");
		}
		String[] seq = key.split(regex);
		if (at < seq.length) {
			content = seq[at];
		}
		return content;
	}

	public static void setLogger(final Log log) {
		if (logger == null) {
			logger = log;
		} else {
			throw new IllegalStateException("There is already a logger (" + log
					+ ")");
		}
	}
	
	public static void removeLogger() {
		if (logger != null) {
			logger = null;
		} else {
			throw new IllegalStateException("There isn't any logger to remove.");
		}
	}

	public static void println(final String message) {
		if (logger == null) {
			// Ignore this case that can appear when ending the application
			//throw new IllegalStateException("Cannot print with a null logger");
		}else{
			logger.println(message);
		}
	}
	
	

}
