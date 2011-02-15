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
package edu.poly.bxmc.betaville.logging;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import com.jme.scene.Node;
import com.jme.util.geom.GeometryTool;
import com.jmex.font3d.math.TriangulationVertex;
import com.jmex.font3d.math.Triangulator;
import com.jmex.model.collada.ThreadSafeColladaImporter;

import edu.poly.bxmc.betaville.ResourceLoader;
import edu.poly.bxmc.betaville.jme.loaders.util.DriveFinder;

/**
 * @author Skye Book
 *
 */
public class LogManager{
	
	public static void setupJMELoggers(){
		// reduce level of jME loggers
		java.util.logging.Logger.getLogger(Node.class.getName()).setLevel(java.util.logging.Level.OFF);
		java.util.logging.Logger.getLogger(GeometryTool.class.getName()).setLevel(java.util.logging.Level.OFF);
		JMELogSettings.reduceToWarning(ThreadSafeColladaImporter.class);
		JMELogSettings.reduceToWarning(Triangulator.class);
		JMELogSettings.reduceToWarning(TriangulationVertex.class);
		
		//System.setProperty("jme.debug", "false");
		//System.setProperty("jme.info", "false");
	}

	public static void setupLoggers(){
		

		PropertyConfigurator.configure(ResourceLoader.loadResource("/data/logging/config.properties"));
		try {
			Logger.getRootLogger().addAppender(new FileAppender(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"), DriveFinder.getHomeDir().getAbsolutePath()+"/.betaville/testlog.log"));
			Logger.getRootLogger().setLevel(Level.INFO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.getLogger(LogManager.class).info("Logger Set Up");
	}

	public static void flushFileHandler(){
	}

	public static void enterLog(Level level, String msg, String classname){
		int numLevel = level.toInt();
		switch (numLevel){
		case Level.DEBUG_INT: Logger.getLogger(classname).debug(msg);
		break;
		case Level.ERROR_INT: Logger.getLogger(classname).error(msg);
		break;
		case Level.FATAL_INT: Logger.getLogger(classname).fatal(msg);
		break;
		case Level.INFO_INT: Logger.getLogger(classname).info(msg);
		break;
		case Level.WARN_INT: Logger.getLogger(classname).warn(msg);
		break;
		}
	}
}
