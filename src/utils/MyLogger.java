package utils;

import org.apache.log4j.Logger;

import org.apache.log4j.xml.DOMConfigurator;

/**
 * This class wraps the log4j logger, in order to simplify the usage of the loggers.
 * You don't have to write 
 * <pre>
 * Logger l = Logger.getLogger(SomeClas.class);
 * l.debug("message");
 * </pre>
 * instead of that you can instantly write
 * <pre>
 * MyLogger.debug("message");
 * </pre>
 * and the name of the class that called to this function would be obtained dynamically.
 */
public class MyLogger {
	private static Logger logger = null;
	// refresh the configuration of the logger on startup
	static {
		refrshConfiguration();
	}

	/**
	 * Refreshes the configuration of the Log4j according to the configuration
	 * path that is specified in the {@link Settings#Log4jConfigurationFile}
	 * This function could be'called during the runtime.
	 * 
	 */
	public static void refrshConfiguration() {
		DOMConfigurator.configureAndWatch(Settings.Log4jConfigurationFile,5000);
	}

	/**
	 * Wraps the original {@link Logger#debug(Object)} function.
	 * @param message
	 *            the message to log.
	 */
	public static void debug(Object message) {
		logger = Logger.getLogger(getCallerName());
		if(logger.isDebugEnabled())
			logger.debug(message);
	}

	/**
	 * Wraps the original {@link Logger#info(Object)} function.
	 * @param message
	 *            the message to log.
	 */
	public static void info(Object message) {
		logger = Logger.getLogger(getCallerName());
		logger.info(message);
	}

	/**
	 * Wraps the original {@link Logger#warn(Object)} function.
	 * @param message
	 *            the message to log.
	 */
	public static void warn(Object message) {
		logger = Logger.getLogger(getCallerName());
		logger.warn(message);
	}

	/**
	 * Wraps the original {@link Logger#error(Object)} function.
	 * 
	 * @param message
	 *            the message to log.
	 */
	public static void error(Object message) {
		logger = Logger.getLogger(getCallerName());
		logger.error(message);
	}

	/**
	 * Wraps the original {@link Logger#error(Object, Throwable)} function.
	 * @param message
	 *            the message to log.
	 */
	public static void error(Object message, Throwable t) {
		logger = Logger.getLogger(getCallerName());
		logger.error(message, t);
	}

	/**
	 * Wraps the original {@link Logger#fatal(Object)} function.
	 * @param message
	 *            the message to log.
	 */
	public static void fatal(Object message) {
		logger = Logger.getLogger(getCallerName());
		logger.fatal(message);
	}

	/**
	 * Wraps the original {@link Logger#fatal(Object, Throwable)} function.
	 * 
	 * @param message
	 *            the message to log.
	 */
	public static void fatal(Object message, Throwable t) {
		logger = Logger.getLogger(getCallerName());
		logger.fatal(message, t);
	}

	/**
	 * This function retrieves the name of the class that has called to one of
	 * the log functions.
	 * @return The name of the class that called one of the logging functions
	 */
	private static String getCallerName() {
		StackTraceElement[] framesArray = Thread.currentThread().getStackTrace();
		StackTraceElement traceElem = framesArray[3]; 
		
		return traceElem.getClassName();
		// to get more detailed information on why initialed the log use this line
		//return traceElem.getClassName()+"."+traceElem.getMethodName()+"() line:"+traceElem.getLineNumber() ;
	}
}