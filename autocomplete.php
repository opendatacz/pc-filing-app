<?php

@$q = strtolower($_GET["term"]);
//@$q = $_GET["q"];
if (!$q) return;

header('Content-type: application/json; charset=utf-8');

$templateData = loadData();
$ow = OntoWiki::getInstance();
$user = $ow->getUser();
$publicmodel = new Erfurt_Rdf_Model($templateData["publicspace"]);

$json = array();
if (isset($_GET["autocomplete"]) && $_GET["autocomplete"] == "business")
{
    $query = 'SELECT DISTINCT ?b ?t WHERE {
            ?b a <http://purl.org/goodrelations/v1#BusinessEntity> .
            ?b <http://purl.org/goodrelations/v1#legalName> ?t .
            }';
    //FILTER (regex(str(?t),"'.$q.'"))
    //?t bif:contains "'.$q.'" .
    //Die($query);
    $publicresults = $publicmodel->sparqlQuery($query);
    //print_r($publicresults);
    $privateresults = array();
    if (!$user->isAnonymousUser()) {
        $privatespace = getPrivateSpace($ow);
        if ($privatespace !== false) {
            $privatemodel = new Erfurt_Rdf_Model($privatespace[0]);
            $privateresults = $privatemodel->sparqlQuery($query);
        }
    }
    $results = array_merge($publicresults,$privateresults);
    //print_r($results);
    foreach ($results as $r)
        if (strpos(strtolower($r["t"]), $q) !== false)
            $json[] = array("label" => $r["t"], "value" => $r["t"], "id" => $r["b"]);
}

if (isset($_GET["autocomplete"]) && $_GET["autocomplete"] == "cpv")
{
    $cpvmodel = new Erfurt_Rdf_Model($templateData["cpv"]);
    //@prefix cpv-def: <http://purl.org/weso/pscs/cpv/ontology/> .
    //a     skos:Concept, cpv-def:Category , cpv-def:Class , gr:ProductOrServiceModel ;
    $query = 'SELECT DISTINCT ?cpv ?label WHERE {
        ?cpv a <http://purl.org/weso/pscs/cpv/ontology/Class> .
        ?cpv <http://www.w3.org/2000/01/rdf-schema#label> ?label .
        FILTER ( lang(?label) = "en" )
        }'; //LIMIT 100
    //Die($query);
    $results = $cpvmodel->sparqlQuery($query);
    //print_r($results);
    foreach ($results as $r)
        if (strpos(strtolower($r["label"]), $q) !== false)
            $json[] = array("label" => $r["label"], "value" => $r["label"], "id" => $r["cpv"]);
}

echo json_encode($json);