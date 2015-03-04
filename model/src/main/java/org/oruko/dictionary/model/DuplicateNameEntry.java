package org.oruko.dictionary.model;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity for persisting duplicated entries
 *
 * Created by dadepo on 2/4/15.
 */
@Entity
@Table(name = "duplicate_entry")
public class DuplicateNameEntry extends AbstractNameEntry {

    @NotEmpty
    private String name;

    public DuplicateNameEntry() {
    }

    public DuplicateNameEntry(NameEntry entry) {
        this.name = entry.getName();
        this.geoLocation= entry.getGeoLocation();
        this.meaning = entry.getMeaning();
        this.tonalMark = entry.getTonalMark();
    }

    /**
     * Returns the identifier, in this case the database primary key
     * @return the identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the name
     * @return returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get the tonal mark
     * @return the tonal mark
     */
    public char[] getTonalMark() {
        return tonalMark;
    }

    /**
     * Set the tonal mark
     * @param tonalMark the total mark
     */
    public void setTonalMark(char[] tonalMark) {
        this.tonalMark = tonalMark;
    }

    /**
     * Get the meaning
     * @return the meaning
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Sets the meaning
     * @param meaning the meaning
     */
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    /**
     * Get the geo location
     * @return the geo location
     */
    public String getGeoLocation() {
        return geoLocation;
    }

    /**
     * Sets the geolocation
     * @param geoLocation the geolocation
     */
    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getMorphology() {
        return morphology;
    }

    public void setMorphology(String morphology) {
        this.morphology = morphology;
    }

    /**
     * Get's the name entry represented as {@link Name}
     * @return the {@link Name}
     */
    @Transient
    public Name toName() {
        return new Name(name, meaning, geoLocation, new Tone(tonalMark));
    }
}
