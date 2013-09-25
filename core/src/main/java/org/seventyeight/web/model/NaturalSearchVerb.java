package org.seventyeight.web.model;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface NaturalSearchVerb {
    
    public String getVerb();
    
    public List<NaturalSearchVerb> getFollowingVerbs();
}
