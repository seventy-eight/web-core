package org.seventyeight.web.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.web.UserAgent;

/**
 * 
 * @author wolfgang
 *
 */
public abstract class Theme {

    public enum Platform {
        Desktop("desktop"),
        Mobile("mobile");

        private String name;

        private Platform(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

	private Logger logger = LogManager.getLogger( Theme.class );

	public Theme() {
		logger.debug( "Creating theme" );
	}
	
	public abstract String getName();

    @Override
    public String toString() {
        return getName();
    }
}
