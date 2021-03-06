package org.seventyeight.web.handlers.template;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.seventyeight.utils.StopWatch;
import org.seventyeight.web.Core;
import org.seventyeight.web.model.Theme;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.SiteDefinition;
import org.seventyeight.web.velocity.DateUtils;
import org.seventyeight.web.velocity.Message;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;


public class TemplateManager {

	private static Logger logger = LogManager.getLogger( TemplateManager.class );
	
	private VelocityEngine engine = new VelocityEngine();
	private Properties velocityProperties = new Properties();
	
	private String templatePathsStr = "";
	
	private List<File> templatePaths = new ArrayList<File>();
	private List<File> staticPaths = new ArrayList<File>();

    private Set<String> libsList = new HashSet<String>();

    private Template _getTemplate( Theme theme, Theme.Platform platform, String template ) throws TemplateException {
		try {
		    return engine.getTemplate( theme.getName() + "/" + platform + "/" + template );
		} catch( ResourceNotFoundException e ) {
			throw new TemplateException( "The template " + template + " for " + theme + "(" + platform + ") does not exist" );
		}
	}

    /**
     * Given a template name and a {@link org.seventyeight.web.model.Theme}, return the {@link Template}.
     * If the {@link org.seventyeight.web.model.Theme} does not have the {@link Template}, try to find it in the default theme.
     * Throws a TemplateException if not found.
     */
    public Template getTemplate( Theme theme, Theme defaultTheme, Theme.Platform platform, String template ) throws TemplateException {
        logger.debug( "[Finding] {} for {}, {}", template, theme.getName(), platform );
        try {
            return _getTemplate( theme, platform, template );
        } catch( TemplateException e ) {
            /* If it goes wrong, try the default theme */
            return _getTemplate( defaultTheme, platform, template );
        }
    }

	
	public void addTemplatePath( File path ) {
		this.templatePathsStr += path.toString() + ", ";
		templatePaths.add( path );
	}
	
	public void addStaticPath( File path ) {
		staticPaths.add( path );
	}

    public void addTemplateLibrary( String lib ) {
        logger.info( "Adding {}", lib );
        libsList.add( lib );
    }
	
	public File getStaticFile( String filename ) throws IOException {
		for( File path : staticPaths ) {
			File file = new File( path, filename );
			if( file.exists() ) {
				return file;
			}
		}
		
		throw new IOException( "File does not exist, " + filename );
	}

    /**
     * Reset template templatePathsStr
     */
	public void resetPaths() {
		this.templatePathsStr = "";
	}

    /**
     * Add a {@link List} of {@link File} templatePathsStr to the template path
     * @param directories
     */
	public void setTemplateDirectories( List<File> directories ) {
		for( File dir : directories ) {
			templatePaths.add( dir );
		}
	}
	
	public File getThemeFile( Theme theme, Theme.Platform platform, String filename ) throws IOException {
		logger.debug( "Scanning {}", templatePaths );
		for( File path : templatePaths ) {
			File file = new File( new File( new File( path, theme.getName() ), platform.getName() ), filename );
			if( file.exists() ) {
				logger.debug( "Returning {}", file );
				return file;
			}
		}

        throw new IOException( "File does not exist, " + filename );
	}

    /**
     * Initialize velocity
     */
	public void initialize() {
		
		/* Generate templatePathsStr */
		this.templatePathsStr = "";
        logger.debug( "------> {}", templatePaths );
		for( File f : templatePaths ) {
            logger.info( "[Template directory] {}", f.getAbsoluteFile() );
			this.templatePathsStr += f.toString() + ", ";
		}

        logger.debug( "Template path string: {}", templatePathsStr );
		
		this.templatePathsStr = templatePathsStr.substring( 0, ( templatePathsStr.length() - 2 ) );
		
		velocityProperties.setProperty( "resource.loader", "file" );
		velocityProperties.setProperty( "file.resource.loader.path", this.templatePathsStr );
        velocityProperties.setProperty( "file.resource.loader.cache", "true" );
        velocityProperties.setProperty( "velocimacro.library.autoreload", "true" );
        velocityProperties.setProperty( "parser.pool.size", "50" );
				
		velocityProperties.setProperty( "file.resource.loader.modificationCheckInterval", "2" );
        velocityProperties.setProperty( "eventhandler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath" );
		
		/* Custom directives */
		velocityProperties.setProperty( "userdirective", "org.seventyeight.web.velocity.html.TextInputDirective,"
				                                       + "org.seventyeight.web.velocity.html.AdvancedFileInputDirective,"
				                                       //+ "org.seventyeight.web.velocity.html.WidgetDirective,"
				                                       + "org.seventyeight.web.velocity.html.GroupSelectInputDirective,"
				                                       + "org.seventyeight.web.velocity.html.ThemeSelectInputDirective,"
				                                       + "org.seventyeight.web.velocity.html.I18NDirective,"
				                                       //+ "org.seventyeight.web.velocity.html.ViewDirective,"
				                                       + "org.seventyeight.web.velocity.html.PreviewDirective,"
				                                       + "org.seventyeight.web.velocity.html.ResourceSelectorDirective,"
                                                       + "org.seventyeight.web.velocity.html.RenderDescriptorDirective,"
                                                       + "org.seventyeight.web.velocity.html.RenderObject,"
                                                       + "org.seventyeight.web.velocity.html.RenderStatic,"
				                                       + "org.seventyeight.web.velocity.html.FileInputDirective" );

        String l = getListAsCommaString( libsList );
        logger.debug( "L: {}", l );
        velocityProperties.setProperty( "velocimacro.library", l );
        velocityProperties.setProperty( "velocimacro.library.autoreload", "true" );
		
		/* Initialize velocity */
		engine.init( velocityProperties );
	}

    private String getListAsCommaString( Set<String> files ) {
        StringBuilder s = new StringBuilder();
        //for( int i = 0 ; i < files.size() ; i++ ) {
        int i = 0;
        for( String f : files ) {
            //String f = files.get( i );

            if( i < ( files.size() - 1 ) ) {
                s.append( f );
                s.append( "," );
            } else {
                s.append( f );
            }

            i++;
        }

        return s.toString();
    }
	
	public VelocityEngine getEngine() {
		return engine;
	}
	
	public Renderer getRenderer( Core core, Theme theme ) {
		return new Renderer( core, theme );
	}

    public Renderer getRenderer( Request request ) {
        return new Renderer( request );
    }
	
	public class Renderer {
		//private Writer writer = new StringWriter();
		private Theme theme;
		private Locale locale;
        private VelocityContext context;
        private Theme.Platform platform = Theme.Platform.Desktop;

        private StopWatch stopWatch;

        private Message message;

        private Core core;

        public Renderer( Core core, Theme theme ) {
            this.theme = theme;
        }

        public Renderer( Request request ) {
            this.theme = request.getTheme();
            //this.locale = request.getLocale();
            this.context = request.getContext();
            this.locale = request.getLocale();
            this.stopWatch = request.getStopWatch();

            //
            platform = request.getPlatform();

            this.core = request.getCore();
        }

        public Renderer setContext( VelocityContext context ) {
            this.context = context;
            return this;
        }
        
        public Renderer updateContext(String key, Object value) {
        	this.context.put(key, value);
        	return this;
        }

        /*
		public Renderer setWriter( Writer writer ) {
			this.writer = writer;
            return this;
		}
		*/

        public Renderer inject( String identifier, Object object ) {
            logger.debug( "Injecting {} as {}", object, identifier );
            context.put( identifier, object );
            return this;
        }

        public Renderer setTheme( Theme theme ) {
            this.theme = theme;
            return this;
        }

        public Renderer setLocale( Locale locale ) {
            this.locale = locale;
            return this;
        }

        public Renderer setPlatform(Theme.Platform platform) {
            this.platform = platform;
            return this;
        }

		public String render( String template ) throws TemplateException {
            /* Resolve template */
            Template t = null;
            try {
                t = getTemplate( theme, core.getDefaultTheme(), platform, template );
            } catch( TemplateException e ) {
                /* If it goes wrong, try the default theme */
                t = getTemplate( core.getDefaultTheme(), core.getDefaultTheme(), platform, template );
            } catch( Exception e ) {
                logger.error( e );
            }

            return render( t );
        }

        public String render( Template template ) {
            stopWatch.start( "Render " + template.getName() );
            StringWriter writer = new StringWriter();

			logger.debug( "[Rendering] {}, {}, {}", template.getName(), template.isSourceModified(), template.requiresChecking() );

            context.put( "core", core );
            context.put( "theme", theme );
            //context.put( "url" );
            
            SiteDefinition def = new SiteDefinition("My title");

            context.put("definition", def);

			/* I18N */
            //logger.debug( "LANG: {}", locale );
			context.put( "locale", locale );
            //logger.debug( "MESSAGE: {}", "templates." + templatePathToClass( template.getName() ) );
            message = new Message( core, "templates." + templatePathToClass( template.getName() ), locale );
            context.put( "message", message );
            //context.put( "message2", new Message( "stuff", locale ) );
            //context.put( "dateTool", new DateTool() );
            context.put( "dateUtils", new DateUtils() );
			
			template.merge( context, writer );

            stopWatch.stop( "Render " + template.getName() );
			return writer.toString();
		}

        /**
         * Render a specific object, given as "modelObject" in the context. Given a concrete template
         * @param object
         * @param template An exact template
         * @return
         * @throws TemplateException
         */
		public String render( Object object, String template ) throws TemplateException {
            context.put( "rb", getBundle( object.getClass() ) );
			context.put( "item", object );
			return render( template );
		}

        /**
         * Render a specific object, given as "modelObject" in the context. <br/>
         * This will render each existing view for this class.
         * @param object
         * @param method
         * @return
         * @throws TemplateException
         */
        public String renderObject( Object object, String method ) throws TemplateException {
            return renderObject( object, method, true );
        }

        public String renderObject( Object object, String method, boolean trySuper ) throws TemplateException {
            Template template = getTemplate( theme, core.getDefaultTheme(), platform, object, method, trySuper );
            context.put( "item", object );
            context.put( "rb", getBundle( object.getClass() ) );
            return render( template );
        }


        public String renderClass( Class clazz, String method ) throws NotFoundException {
            return renderClass( clazz, method, true );
        }

        public String renderClass( Class clazz, String method, boolean trySuper ) throws NotFoundException {
            Template template = getTemplateFromClass( theme, core.getDefaultTheme(), platform, clazz, method, trySuper );
            context.put( "rb", getBundle( clazz ) );
            return render( template );
        }


        public String renderClass( Object object, Class clazz, String method ) throws NotFoundException {
            Template template = getTemplateFromClass( theme, core.getDefaultTheme(), platform, clazz, method, true );
            context.put( "item", object );
            context.put( "rb", getBundle( clazz ) );
            return render( template );
        }

        public String renderClass( Object object, Class clazz, String method, boolean trySuper ) throws NotFoundException {
        	logger.debug("Rendering {} with {}, trySuper: {}", object, clazz, trySuper);
            Template template = getTemplateFromClass( theme, core.getDefaultTheme(), platform, clazz, method, trySuper );
            context.put( "item", object );
            context.put( "rb", getBundle( clazz ) );
            return render( template );
        }

	}

    public static ResourceBundle getBundle( Class<?> clazz ) {
        try {
            return ResourceBundle.getBundle( "templates.default." + clazz.getName() + ".messages" );
        } catch( MissingResourceException e ) {
            // Can I live without it?
            return null;
        }

        //return ResourceBundle.getBundle( "templates/default/" + getUrlFromClass( clazz ) + "/messages" );
    }

    /**
     * Given a class, get the corresponding template
     *
     * @param defaultTheme
     * @param object
     * @param method
     * @param trySuper If true, try objects super classes
     * @return
     */
	public Template getTemplate( Theme theme, Theme defaultTheme, Theme.Platform platform, Object object, String method, boolean trySuper ) throws TemplateException {
		/* Resolve template */
		Class<?> clazz = object.getClass();
		while( clazz != null && clazz != Object.class ) {
            try {
                return getTemplate( theme, defaultTheme, platform, getUrlFromClass( clazz.getCanonicalName(), method ) );
            } catch( TemplateException e ) {
                if( trySuper ) {
                    clazz = clazz.getSuperclass();
                } else {
                    break;
                }
            }
		}
		
		throw new TemplateException( method + " for " + object.getClass().getName() + " not found" );
	}

    public boolean templateExists( Theme theme, Theme defaultTheme, Theme.Platform platform, Object object, String method ) {
        try {
            getTemplate( theme, defaultTheme, platform, getUrlFromClass( object.getClass().getCanonicalName(), method ) );
            return true;
        } catch( TemplateException e ) {
            return false;
        }
    }

    /**
     * Determine whether a template for the given {@link Class} exists or not
     */
    public boolean templateForClassExists( Theme theme, Theme defaultTheme, Theme.Platform platform, Class<?> clazz, String method ) {
        try {
            getTemplate( theme, defaultTheme, platform, getUrlFromClass( clazz, method ) );
            return true;
        } catch( TemplateException e ) {
            return false;
        }
    }



    /**
     * Given a class, get the corresponding template
     *
     * @param defaultTheme
     * @param clazz
     * @param method
     * @param trySuper If true, try objects super classes
     * @return
     */
	public Template getTemplateFromClass( Theme theme, Theme defaultTheme, Theme.Platform platform, Class<?> clazz, String method, boolean trySuper ) throws NotFoundException {
        Class<?> org = clazz;
		/* Resolve template */
		int cnt = 0;
		while( clazz != null && clazz != Object.class ) {
            try {
                return getTemplate( theme, defaultTheme, platform, getUrlFromClass( clazz.getCanonicalName(), method ) );
            } catch( TemplateException e ) {
                if( trySuper ) {
                    clazz = clazz.getSuperclass();
                } else {
                    break;
                }
            }
		}

        //throw new TemplateException( method + " for " + org.getName() + " not found" );
        throw new NotFoundException( "Template " + method + " for " + org.getSimpleName() + " not found", "Template not found" );
	}

    public static String getUrlFromClass( Class<?> clazz ) {
        return getUrlFromClass( clazz.getName() );
    }

	/**
	 * Given an object and the velocity method, get the url of the file.
	 * @param object - Some object
	 * @param method - A velocity method, view.vm or configure.vm
	 * @return A relative path to the velocity file
	 */
	public static String getUrlFromObject( Object object, String method ) {
		return getUrlFromClass( object.getClass().getCanonicalName(), method );
	}
	
	/**
	 * Given a string representation of a class and the velocity method, get the url of the file.
	 * @param clazz - A string representing a class
	 * @param method - A velocity method, view.vm or configure.vm
	 * @return A relative path to the velocity file
	 */
	public static String getUrlFromClass( String clazz, String method ) {
		return clazz.replace( '.', '/' ).replace( '$', '/' ) + "/" + method;
	}

    public static String getUrlFromClass( Class clazz, String method ) {
        return clazz.getCanonicalName().replace( '.', '/' ).replace( '$', '/' ) + "/" + method;
    }

    public static String getUrlFromClass( String clazz ) {
        return clazz.replace( '.', '/' ).replace( '$', '/' );
    }

    public static String templatePathToClass(String templatePath) {
        int idx = templatePath.lastIndexOf( "/" );
        if(idx > -1) {
            return templatePath.substring( 0, idx ).replace( "/", "." );
        } else {
            throw new IllegalArgumentException( templatePath + " is not a path?" );
        }
    }
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "Paths: " + this.templatePathsStr + "\n" );
		
		return sb.toString();
	}
}
