package org.seventyeight.web.model;

import java.util.List;

/**
 * @author cwolfgang
 */
public interface NaturalSearchable {

    public String getType();
    public List<NaturalSearchVerb> getVerbs();

    public List<NaturalSearchTree.AbstractSearchTreeData> getSearchTreeString();
}
