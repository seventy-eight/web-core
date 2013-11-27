package org.seventyeight.web.project.model;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.*;

/**
 * @author cwolfgang
 */
public class Project extends Resource<Project> {

    public Project( Node parent, MongoDocument document ) {
        super( parent, document );
    }

    @Override
    public Saver getSaver( CoreRequest request ) {
        return new ProjectSaver( this, request );
    }

    public static class ProjectSaver extends Saver {

        public ProjectSaver( AbstractNode modelObject, CoreRequest request ) {
            super( modelObject, request );
        }

        @Override
        public void save() throws SavingException {
            super.save();

            String description = request.getValue( "description", "" );
            modelObject.setText( "description", description, request.getLocale().getIdentifier() );
        }
    }

    public String getDescription() {
        return getText( "description", "" );
    }

    public static class ProjectDescriptor extends ResourceDescriptor<Project> {

        @Override
        public String getType() {
            return "project";
        }

        @Override
        public String getDisplayName() {
            return "Project";
        }
    }
}
