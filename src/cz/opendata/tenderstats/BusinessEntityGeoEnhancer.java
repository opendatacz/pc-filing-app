package cz.opendata.tenderstats;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import cz.opendata.tenderstats.Geocoder.GeoProvider;
import cz.opendata.tenderstats.Geocoder.GeoProviderFactory;
import cz.opendata.tenderstats.Geocoder.Position;

/**
 * @author Matej Snoha
 * 
 */
public class BusinessEntityGeoEnhancer {

	protected static Map<String, Position> geocodeAddresses(String queryURL, String queryUsername, String queryPassword,
			String querySPARQL, String geoProvider, String useCache) {
		Geocoder.loadCacheIfEmpty("cache/geocoder.cache");

		boolean mix = false;
		boolean useCacheB = new Boolean(useCache);
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
			case "local city":
				gp = GeoProviderFactory.LOCAL_CITY;
				break;
			case "local mix":
				mix = true;
				break;
			default:
				gp = GeoProviderFactory.DEFAULT;
				break;
		}
		GeoProvider gpLocal = GeoProviderFactory.LOCAL;
		GeoProvider gpCity = GeoProviderFactory.LOCAL_CITY;
		Query query = QueryFactory.create(querySPARQL);

//		/* @formatter:off */
//		Query query = QueryFactory.create(
//				"PREFIX gr: <" + Prefixes.GR + "> " + 
//				"PREFIX  s: <" + Prefixes.S + "> " + 
//				"SELECT DISTINCT ?url ?city ( concat (?street, \", \", ?city, \", \", ?country) as ?address ) {  " + 
//				( graphURL.length > 0 ? 
//				"	GRAPH ?g { " : "" ) + 
//				" 		?be a gr:BusinessEntity ; " + 
//				"			s:address ?url . " + 
//				"		?url s:streetAddress ?street ; " + 
//				"			s:addressLocality ?city ; " + 
//				"			s:addressCountry ?country . " + 
//				( graphURL.length > 0 ?
//				" } VALUES ?g { <" + graphURL[0] + "> } " : "" ) +
//				" FILTER NOT EXISTS { ?url s:geo ?geo }" +	// TODO for updates
//				"}");
//		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(query);

		Map<String, Position> results = new HashMap<>();

		try {
			QueryEngineHTTP qe = new QueryEngineHTTP(queryURL, query);
			qe.setBasicAuthentication(queryUsername, queryPassword.toCharArray());
			ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				QuerySolution row = rs.next();
				String addressURL = row.get("url").asResource().toString();
				URL url = new URL(addressURL); // for %hex escaping
				URI uri =
						new URI(url.getProtocol(),
								url.getUserInfo(),
								url.getHost(),
								url.getPort(),
								url.getPath(),
								url.getQuery(),
								url.getRef());
				addressURL = uri.toURL().toString();

				if (mix) {
					String city = row.get("city").asLiteral().toString();
					Position position = Geocoder.locate(city, gpCity, useCacheB);
					if (position != null) {
						System.out.println("[NOTICE] city : " + city + " @ " + position);
						results.put(addressURL, position);
					} else {
						String address = row.get("address").asLiteral().toString();
						position = Geocoder.locate(address, gpLocal, useCacheB);
						if (position != null) {
							System.out.println("[NOTICE] (fallback to fulltext) address : " + address + " @ " + position);
							results.put(addressURL, position);
						} else {
							System.out.println("[INFO] Couldn't find city or address : " + address);
						}
					}
				} else {
					String address = row.get("address").asLiteral().toString();
					Position position = Geocoder.locate(address, gp, useCacheB);
					if (position != null) {
						System.out.println("[NOTICE] address : " + address + " @ " + position);
						results.put(addressURL, position);
					} else {
						System.out.println("[INFO] Couldn't find address : " + address);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Geocoder.saveCache("cache/geocoder.cache");

		return results;
	};

	protected static void insertGeoData(String addressURL, Position position, String updateURL, String updateUsername,
			String updatePassword, String graphUrl) {
		String placeURL = addressURL + "/place";
		String geoURL = addressURL + "/geo";

		/* @formatter:off */
		UpdateRequest request = UpdateFactory.create(
				"PREFIX gr: <" + Prefixes.GR + "> " + 
				"PREFIX  s: <" + Prefixes.S + "> " +
				"INSERT DATA " +
				"{ " +
				( !graphUrl.isEmpty() ?
				"	GRAPH <" + graphUrl +"> { " : "" ) +
				//"	<" + beURL + ">			s:location		<" + placeURL + "> . " + // TODO get beURL
				"	<" + placeURL + ">  	a				s:Place ;" +
				"							s:address		<" + addressURL + "> ;" +
				"							s:geo			<" + geoURL + "> ." +
				"	<" + geoURL + ">  		a				s:GeoCoordinates ;" +
				"							s:latitude		\"" + position.getLatitude() + "\";" +
				"							s:longitude		\"" + position.getLongitude() + "\"." +
				( !graphUrl.isEmpty() ?
				"	} " : "" ) +
				"}");
		/* @formatter:on */

		System.out.println("###################################################");
		System.out.println(request);

		// todo move context out of func?
		HttpContext httpContext = new BasicHttpContext();
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(updateUsername, updatePassword));
		httpContext.setAttribute(ClientContext.CREDS_PROVIDER, provider);
		UpdateProcessRemote upr = new UpdateProcessRemote(request, updateURL, Context.emptyContext);
		upr.setHttpContext(httpContext);
		upr.execute(); // TODO enable
	}

	protected static String geoAsN3(String addressURL, Position position) {
		String placeURL = addressURL + "/place";
		String geoURL = addressURL + "/geo";
		String a = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";

		/* @formatter:off */
		return
				//"	<" + beURL + ">			s:location		<" + placeURL + "> . " + // TODO get beURL
				"<" + placeURL + "> " + a + " <" + Prefixes.S + "Place> .\n" +
				"<" + placeURL + "> <" + Prefixes.S + "address> <" + addressURL + "> .\n" +
				"<" + placeURL + "> <" + Prefixes.S + "geo> <" + geoURL + "> .\n" +
				"<" + geoURL + "> " + a	+ " <" + Prefixes.S + "GeoCoordinates> .\n" +
				"<" + geoURL + "> <" + Prefixes.S + "latitude> \"" + position.getLatitude() + "\" .\n" +
				"<" + geoURL + "> <" + Prefixes.S + "longitude> \"" + position.getLongitude() + "\" .\n" ;
		/* @formatter:on */

	}

	public static void addMissingGeoPoints(HttpServletResponse response, String queryURL, String queryUsername,
			String queryPassword, String querySPARQL, String updateURL, String updateUsername, String updatePassword,
			String graphURL, String geoProvider, String useCache) {
		synchronized (queryURL.intern()) {
			Map<String, Position> positions =
					geocodeAddresses(queryURL, queryUsername, queryPassword, querySPARQL, geoProvider, useCache);

			for (Map.Entry<String, Position> entry : positions.entrySet()) {
				String addressURL = entry.getKey();
				Position position = entry.getValue();
				System.out.println("[NOTICE] inserting triples for <" + addressURL + "> @ " + position);
				insertGeoData(addressURL, position, updateURL, updateUsername, updatePassword, graphURL);
			}

			System.out.println("[NOTICE] finished inserting triples for " + positions.size() + " addresses");
		}
	}

	public static void downloadN3(HttpServletResponse response, String queryURL, String queryUsername, String queryPassword,
			String querySPARQL, String geoProvider, String useCache) {
		synchronized (queryURL.intern()) {
			Map<String, Position> positions =
					geocodeAddresses(queryURL, queryUsername, queryPassword, querySPARQL, geoProvider, useCache);

			try {
				for (Map.Entry<String, Position> entry : positions.entrySet()) {
					String addressURL = entry.getKey();
					Position position = entry.getValue();
					response.getWriter().println(geoAsN3(addressURL, position));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("[NOTICE] finished sending triples for " + positions.size() + " addresses");
		}
	}
}
