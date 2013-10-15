package cz.opendata.tenderstats.matchmaker;

import org.apache.commons.configuration.Configuration;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import cz.opendata.tenderstats.Geocoder;
import cz.opendata.tenderstats.Geocoder.GeoProvider;
import cz.opendata.tenderstats.Geocoder.GeoProviderFactory;
import cz.opendata.tenderstats.Geocoder.Position;

import static cz.opendata.tenderstats.Prefixes.*;

/**
 * @author Matej Snoha
 */
public class GeoDistanceComparer extends AbstractComparer {

	public GeoDistanceComparer() {
	}

	public GeoDistanceComparer(Configuration config) {
		super(config);
	}

	@Override
	public VerboseResult compareVerbose(Contract base, Contract other) {
		try {
			Resource basePlaceUrl =
					base.rdf.getProperty(ResourceFactory.createResource(base.getUrl()),
							ResourceFactory.createProperty(PC, "location")).getResource();
			String basePlace = base.rdf.getProperty(basePlaceUrl, ResourceFactory.createProperty(RDFS, "label")).getString();
			Position basePosition = Geocoder.locate(basePlace);

			Resource otherPlaceUrl =
					other.rdf.getProperty(ResourceFactory.createResource(other.getUrl()),
							ResourceFactory.createProperty(PC, "location")).getResource();
			String otherPlace = other.rdf.getProperty(otherPlaceUrl, ResourceFactory.createProperty(RDFS, "label")).getString();

			String geoProvider = getConfiguration().getString("geoProvider", "default");
			GeoProvider gp = null;
			switch (geoProvider.toLowerCase()) {
				case "google maps":
					gp = GeoProviderFactory.GOOGLE_MAPS;
					break;
				case "nominatim":
					gp = GeoProviderFactory.NOMINATIM;
					break;
				case "local":
					gp = GeoProviderFactory.LOCAL;
					break;
				default:
					gp = GeoProviderFactory.DEFAULT;
					break;
			}
			boolean useCache = getConfiguration().getBoolean("useCache", true);
			Position otherPosition = Geocoder.locate(otherPlace, gp, useCache);

			if (!basePosition.isUndefined() && !otherPosition.isUndefined()) {
				double distance = basePosition.distanceTo(otherPosition);

				// if (Math.abs(basePosition.distanceTo(otherPosition) - basePosition.distanceToHaversine(otherPosition)) > 10000)
				// {
				// System.out.println(basePlace + " @ " + basePosition);

				// System.out.println(otherPlace + " @ " + otherPosition);

				// System.out.println("geo distance: " + basePosition.distanceTo(otherPosition) / 1000 + "km");
				// System.out.println("geo approx d: " + basePosition.distanceToHaversine(otherPosition) / 1000 + "km");
				// System.out.println();
				// System.out.println("geo diff "
				// + Math.abs(basePosition.distanceTo(otherPosition) - basePosition.distanceToHaversine(otherPosition)));
				// System.out.println();
				// }
				double maxDistance = getConfiguration().getDouble("maxDistance", 2000) * 1000;
				return new VerboseResult(Math.max(0, 1 - (distance / maxDistance)), "distance " + (int) distance / 1000
						+ " km (position " + otherPosition + ")");
			} else {
				return new VerboseResult(0, "cannot locate address (" + otherPosition + ")");
			}
		} catch (Exception e) {
			return new VerboseResult(0, "missing or corrupted data");
		}
	}
}
