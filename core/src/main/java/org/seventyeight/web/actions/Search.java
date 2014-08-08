package org.seventyeight.web.actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.utils.GetMethod;
import org.seventyeight.web.Core;
import org.seventyeight.web.handlers.template.TemplateException;
import org.seventyeight.web.model.FeatureSearch;
import org.seventyeight.web.model.ItemInstantiationException;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.NotFoundException;
import org.seventyeight.web.servlet.Request;
import org.seventyeight.web.servlet.Response;
import org.seventyeight.web.servlet.SearchHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author cwolfgang
 */
public class Search implements Node {

    private static Logger logger = LogManager.getLogger( Search.class );

    private Core core;

    public Search( Core core ) {
        this.core = core;
    }

    @Override
    public Node getParent() {
        return core.getRoot();
    }

    @Override
    public String getDisplayName() {
        return "Search";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    @GetMethod
    public void doSearch( Request request, Response response ) throws IOException, NotFoundException, ItemInstantiationException, TemplateException {
        response.setRenderType( Response.RenderType.NONE );

        SearchHelper sh = new SearchHelper( this, request, response );
        sh.search();
        sh.render();
    }

    @GetMethod
    public void doShow( Request request, Response response ) {
        logger.debug( "SHOW????!!!!" );
    }



}
