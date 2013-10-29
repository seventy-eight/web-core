package org.seventyeight.web.project.model.temp;

import org.seventyeight.database.mongodb.MongoDocument;
import org.seventyeight.web.model.Node;
import org.seventyeight.web.model.data.DataNode;
import org.seventyeight.web.project.model.Skill;

/**
 * @author cwolfgang
 * @deprecated
 */
public class ProfileCertificateDataNode extends DataNode<Skill> {

    public ProfileCertificateDataNode( MongoDocument document ) {
        super( document );
    }

    @Override
    public <E extends Element<Skill>> E getElement( MongoDocument document ) {
        return (E) new ProfileCertificateElement( document );
    }

    @Override
    public Node getParent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getDisplayName() {
        return "Data node for Profile Certificates";
    }

    @Override
    public String getMainTemplate() {
        return null;
    }

    public class ProfileCertificateElement extends Element<Skill> {

        public ProfileCertificateElement( MongoDocument document ) {
            super( document );
        }
    }
}
