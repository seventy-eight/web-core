import org.seventyeight.web.DatabaseContextListener;

import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * @author cwolfgang
 */
@WebListener
public class CMSListener extends DatabaseContextListener<CMSCore> {
    @Override
    public CMSCore getCore( File path, String dbname ) {
        return new CMSCore( path, dbname );
    }
}
