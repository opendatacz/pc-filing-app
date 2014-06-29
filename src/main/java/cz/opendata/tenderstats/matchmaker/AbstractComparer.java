package cz.opendata.tenderstats.matchmaker;

import java.util.Collection;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Matej Snoha
 *
 */
public abstract class AbstractComparer implements Comparer {

    protected CompositeConfiguration config;

    private double weight = Double.NaN;

    @Override
    public double getWeight() {
        if (Double.isNaN(weight)) {
            weight = config.getDouble("weight");
        }
        return weight;
    }

    @Override
    public void setWeight(double weight) {
        config.setProperty("weight", weight);
        this.weight = weight;
    }

    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(getName()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AbstractComparer other = (AbstractComparer) obj;
        return new EqualsBuilder().append(getName(), other.getName()).isEquals();
    }

    @Override
    public String toString() {
        return getName();
    }

    public AbstractComparer() {
        this(null);
    }

    public AbstractComparer(Configuration config) {
        this.config = new CompositeConfiguration();
        String fileName = "../config/" + getName() + ".xml";
        try {
            this.config.addConfiguration(new XMLConfiguration(getClass().getResource(fileName)));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        if (config != null) {
            this.config.copy(config.subset(getName()));
            // this.config.addConfiguration(config.subset(getName()));
        }
    }

    @Override
    public Comparer addConfiguration(Configuration config) {
        return addConfiguration(config, true);
    }

    @Override
    public Comparer addConfiguration(Configuration config, boolean filterRelevantItems) {
        if (config != null) {
            if (filterRelevantItems) {
                this.config.copy(config.subset(getName()));
            } else {
                this.config.copy(config);
            }
        }
        return this;
    }

	// public AbstractComparer(ServletRequestConfiguration config) {
    // this.config = new CompositeConfiguration();
    // if (config != null) {
    // this.config.copy(config);
    // }
    // }
    @Override
    public final String getName() {
        return getClass().getSimpleName().intern(); // there might be lots of references to the same name (hashtables)
    }

    @Override
    public void run(Contract base, Contract other) {
        if (!other.containsScore(this)) {
            other.setScore(this, compare(base, other));
        }
    }

    @Override
    public void runVerbose(Contract base, Contract other) {
        if (!other.containsScore(this) || !other.containsMessage(this)) {
            VerboseResult vr = compareVerbose(base, other);
            other.setScore(this, vr.score);
            other.setMessage(this, vr.message);
        }
    }

    @Override
    public void run(Contract base, Collection<Contract> others) {
        for (Contract other : others) {
            run(base, other);
        }
    }

    @Override
    public void runVerbose(Contract base, Collection<Contract> others) {
        for (Contract other : others) {
            runVerbose(base, other);
        }
    }

    @Override
    public double compare(Contract base, Contract other) { // override at least one of compare / compareVerbose
        return compareVerbose(base, other).score;
    }

    @Override
    public VerboseResult compareVerbose(Contract base, Contract other) { // override at least one of compare / compareVerbose
        return new VerboseResult(compare(base, other), "");
    }

}
