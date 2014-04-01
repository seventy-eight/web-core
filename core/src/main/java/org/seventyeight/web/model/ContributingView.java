package org.seventyeight.web.model;

/**
 * @author cwolfgang
 */
public class ContributingView {
    private String view;
    private String title;
    private Node node;

    public ContributingView( String title, String view, Node node ) {
        this.view = view;
        this.title = title;
        this.node = node;
    }

    public String getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return title + ", " + node + ", " + view;
    }
}
