package org.seventyeight.web.project.model.temp;

import org.seventyeight.database.mongodb.MongoDBCollection;
import org.seventyeight.database.mongodb.MongoDBQuery;
import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.data.DataStrategy;
import org.seventyeight.web.project.model.Certificate;
import org.seventyeight.web.project.model.temp.ProfileCertificateDataNode;

/**
 * @author cwolfgang
 */
public class ProfileCertificateStrategy extends DataStrategy<Certificate, ProfileCertificateDataNode> {

    @Override
    public String getCollectionName() {
        return "profile-certificates";
    }

    @Override
    public ProfileCertificateDataNode getDataNode( MongoDocument document ) {
        return new ProfileCertificateDataNode( document );
    }

    @Override
    public void addDataNode( String identifier, Certificate node ) {

        /* Find the latest data node */
        MongoDBQuery query = new MongoDBQuery().is( "identifier", identifier );
        long count = MongoDBCollection.get( getCollectionName() ).count( query );

        ProfileCertificateDataNode dataNode = null;

        synchronized( getLock( identifier ) ) {

            /* No data node found create the first */
            if( count == 0 ) {
                dataNode = createDataNode();
            } else {

            }

            /* If the data node is full, a new should be created */
            if( dataNode.getNumber() > getCountPerNode() ) {
                dataNode = createDataNode();
            }
        }
    }

    protected ProfileCertificateDataNode createDataNode() {

        return null;
    }
}
