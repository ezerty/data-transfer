package rubiconproject.model;

import java.util.List;
import java.util.Objects;

public class SiteCollection {

    private String collectionId;
    private List<Site> sites;

    public SiteCollection() {
    }

    public SiteCollection(String collectionId, List<Site> sites) {
        this.collectionId = collectionId;
        this.sites = sites;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.collectionId);
        hash = 37 * hash + Objects.hashCode(this.sites);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SiteCollection other = (SiteCollection) obj;
        if (!Objects.equals(this.collectionId, other.collectionId)) {
            return false;
        }
        if (!Objects.equals(this.sites, other.sites)) {
            return false;
        }
        return true;
    }

}
