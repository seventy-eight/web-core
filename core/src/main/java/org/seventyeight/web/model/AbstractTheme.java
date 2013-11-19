package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author wolfgang
 *
 */
public abstract class AbstractTheme {

	private Logger logger = LogManager.getLogger( AbstractTheme.class );

	public AbstractTheme() {
		logger.debug( "Creating theme" );
	}
	
	public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
