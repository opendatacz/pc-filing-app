<?php
$GLOBALS["ns_var"] = array(
        'rdf' => 'http://www.w3.org/1999/02/22-rdf-syntax-ns#',
        'pc' => 'http://purl.org/procurement/public-contracts#',
        'br' => 'http://purl.org/business-register#',
        'gr' => 'http://purl.org/goodrelations/v1#',
        'vcard' => 'http://www.w3.org/2006/vcard/ns#',
        'dct' => 'http://purl.org/dc/terms/',
        'dcterms' => 'http://purl.org/dc/terms/',
        'xsd' => 'http://www.w3.org/2001/XMLSchema#',
        'prov' => 'http://www.w3.org/ns/prov#'
    );
$GLOBALS["all_countries"] = array(
    "" => "",
    "AL" => "Albania",
    "AD" => "Andorra",
    "AT" => "Austria",
    "BY" => "Belarus",
    "BE" => "Belgium",
    "BA" => "Bosnia and Herzegovina",
    "BG" => "Bulgaria",
    "HR" => "Croatia",
    "CZ" => "Czech Republic",
    "DK" => "Denmark",
    "EE" => "Estonia",
    "FI" => "Finland",
    "FR" => "France",
    "GE" => "Georgia",
    "DE" => "Germany",
    "GR" => "Greece",
    "HU" => "Hungary",
    "IS" => "Iceland",
    "IE" => "Ireland",
    "IT" => "Italy",
    "LV" => "Latvia",
    "LI" => "Liechtenstein",
    "LT" => "Lithuania",
    "LU" => "Luxembourg",
    "MK" => "Macedonia",
    "MT" => "Malta",
    "MC" => "Monaco",
    "ME" => "Montenegro",
    "NL" => "Netherlands",
    "NO" => "Norway",
    "PL" => "Poland",
    "PT" => "Portugal",
    "RO" => "Romania",
    "RU" => "Russia",
    "RS" => "Serbia",
    "SK" => "Slovakia",
    "SI" => "Slovenia",
    "ES" => "Spain",
    "SE" => "Sweden",
    "CH" => "Switzerland",
    "TR" => "Turkey",
    "UA" => "Ukraine",
    "GB" => "United Kingdom",
    "VA" => "Vatican City State",
    );
$GLOBALS["all_currencies"] = array(
    "" => "",
    "CZK" => "CZK",
    "EUR" => "EUR",
    "GBP" => "GBP",
);

function propagateGet($exclude)
{
    $uri_query = "?";
    foreach ($_GET as $g => $v)
        if (!in_array($g,$exclude))
            $uri_query .= $g . "=" . $v . "&amp;";
    return $uri_query;
}

function genChT()
{
    return rand(10000000,99999999);
}

function getLanguages()
{
    return array("en","cs");
}

function generateGuid()
{
    if (function_exists('com_create_guid') === true)
    {
        return trim(com_create_guid(), '{}');
    }
    return sprintf('%04X%04X-%04X-%04X-%04X-%04X%04X%04X', mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(16384, 20479), mt_rand(32768, 49151), mt_rand(0, 65535), mt_rand(0, 65535), mt_rand(0, 65535));
}

function printMessage($message,$level)
{
    $class = "";
    switch ($level) {
        case OntoWiki_Message::SUCCESS:
            $class = "success";
            break;
        case OntoWiki_Message::INFO:
            $class = "info";
            break;
        case OntoWiki_Message::WARNING:
            $class = "warning";
            break;
        case OntoWiki_Message::ERROR:
        default:
            $class = "error";
            break;
    }
    echo '<p class="messagebox ',$class,'">',$message,'</p>',"\n";
}

function getPrivateSpace($ow)
{
    $templateData = loadData();
    $store = $ow->erfurt->getStore();
    $user = $ow->getUser();
    $username = $user->getUsername();
    $useruri = 'http://localhost/OntoWiki/Config/'.$username; //TODO: check je to skutecne vzdy pravda?
    $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
    $config2ns = $templateData["configns"];
    $pspredicate = $templateData["configprivatestorepredicate"];
    //clear cache
    $cache = $ow->erfurt->getQueryCache();
    $cache->invalidateWithModelIri($configModel->getModelIri());
    //add ns
    $prefixes = $configModel->getNamespacePrefixes();
    $config2prefixes = array_keys($prefixes,$config2ns);
    //load contractor
    $resUser = $configModel->getResource($useruri);
    require_once 'Erfurt/Syntax/RdfSerializer.php';
    $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
    $serialized = $serializer->serializeResourceToString($resUser->getIri(), $configModel->getModelIri(), false, false);
    $domdoc = new DOMDocument();
    @$domdoc->LoadXml($serialized);
    $xpath = new DOMXPath($domdoc);
    $res = array();
    foreach ($config2prefixes as $c2p) {
        @$members = $xpath->query("//$c2p:$pspredicate/@rdf:resource"); //TODO: parametry
        if (is_object($members))
            for ($i=0;$i<$members->length;$i++)
                $res[] = $members->item($i)->nodeValue;
    }
    if ($res === array())
        return false;
    return $res;
}

/**
 * return array with keys "contractor" and "supplier" containing true/false
 */         
function getContractUseroups($ow)
{
    $app = $ow;
    $store = $app->erfurt->getStore();
    $user = $app->getUser();
    $username = $user->getUsername();
    $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
    $data = array();
    $data["contractor"] = false;
    $data["supplier"] = false;
    $data["any"] = false;
    $data["both"] = false;
    //clear cache
    $cache = $app->erfurt->getQueryCache(); //$cache = Erfurt_App::getInstance()->getQueryCache();
    $cache->invalidateWithModelIri($configModel->getModelIri());
    //load contractor
    $resContractors = $configModel->getResource('http://localhost/OntoWiki/Config/Contractors');
    require_once 'Erfurt/Syntax/RdfSerializer.php';
    $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
    $serialized = $serializer->serializeResourceToString($resContractors->getIri(), $configModel->getModelIri(), false, false);
    $domdoc = new DOMDocument();
    @$domdoc->LoadXml($serialized);
    $xpath = new DOMXPath($domdoc);
    $members = $xpath->query("//sioc:has_member/@rdf:resource");
    for ($i=0;$i<$members->length;$i++)
        if ($members->item($i)->nodeValue == $username)
            $data["contractor"] = true;
    //load supplier
    $resSuppliers = $configModel->getResource('http://localhost/OntoWiki/Config/Suppliers');
    $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
    $serialized = $serializer->serializeResourceToString($resSuppliers->getIri(), $configModel->getModelIri(), false, false);
    $domdoc = new DOMDocument();
    @$domdoc->LoadXml($serialized);
    $xpath = new DOMXPath($domdoc);
    $members = $xpath->query("//sioc:has_member/@rdf:resource");
    for ($i=0;$i<$members->length;$i++)
        if ($members->item($i)->nodeValue == $username)
            $data["supplier"] = true;
    //any & both
    if ($data["supplier"] || $data["contractor"])
        $data["any"] = true;
    if ($data["supplier"] && $data["contractor"])
        $data["both"] = true;
    //return
    return $data;
}

function getUserBusiness($ow)
{
    $app = $ow;
    $store = $app->erfurt->getStore();
    $user = $app->getUser();
    $username = $user->getUsername();
    $useruri = 'http://localhost/OntoWiki/Config/'.$username; //TODO: check je to skutecne vzdy pravda?
    $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
    $templateData = loadData();
    $config2ns = $templateData["configns"];
    $bspredicate = $templateData["configownbusinesspredicate"];
    //clear cache
    $cache = $app->erfurt->getQueryCache(); //$cache = Erfurt_App::getInstance()->getQueryCache();
    $cache->invalidateWithModelIri($configModel->getModelIri());
    //add ns
    //$config2prefix = $configModel->getNamespacePrefix($config2ns);
    $prefixes = $configModel->getNamespacePrefixes();
    $config2prefixes = array_keys($prefixes,$config2ns);
    //load contractor
    $resUser = $configModel->getResource($useruri);
    require_once 'Erfurt/Syntax/RdfSerializer.php';
    $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
    $serialized = $serializer->serializeResourceToString($resUser->getIri(), $configModel->getModelIri(), false, false);
    //echo "SERIALIZED: ",$serialized;
    $domdoc = new DOMDocument();
    @$domdoc->LoadXml($serialized);
    $xpath = new DOMXPath($domdoc);
    $res = array();
    foreach ($config2prefixes as $c2p) {
        @$members = $xpath->query("//$c2p:$bspredicate/@rdf:resource"); //TODO: parametry
        if (is_object($members))
            for ($i=0;$i<$members->length;$i++)
                $res[] = $members->item($i)->nodeValue;
    }
    if ($res === array())
        return false;
    return $res;
}

function loadData()
{
    $templateData = array();
    //models
    $templateData["publicspace"] = "http://ld.opendata.cz/resource/";
    $templateData["web"] = "http://contracts.opendata.cz/web/";
    //extended config
    $templateData["configns"] = "http://localhost/OntoWiki/Config2/";
    $templateData["configprivatestorepredicate"] = "PrivateStore";
    //$templateData["configprivatestore"] = "http://localhost/OntoWiki/Config2/PrivateStore";
    $templateData["configownbusinesspredicate"] = "BusinessSelf";
    //$templateData["configownbusiness"] = "http://localhost/OntoWiki/Config2/BusinessSelf";
    //local resource
    $templateData["privatespace"] = "http://contracts.opendata.cz/privatespace/"; //prefix //"http://localhost/OntoWiki/privatestore/";
    $templateData["user"] = "http://contracts.opendata.cz/resource/user/"; //prefix
    //global resource
    $templateData["resource"] = "http://ld.opendata.cz/resource/";
    //app uri
    $templateData["fillingapp"] = "http://ld.opendata.cz/resource/fillingApp";
    
    return $templateData;
}

/**
 * param $model Erfurt_Rdf_Model to be queried
 * param $query array of SPARQL queries returning ?s ?p ?o columns
 *         
 * @return array statment array usable by Erfurt_Rdf_Model->addMultipleStatements()
 */         
function constuctStmtArray($model, $query, $options)
{
    $stmtArray = array();
    $defaultsubject = $options["default_subject"];
    $bnodePredicates = array();
    if (isset($options["bnode"]))
        $bnodePredicates = $options["bnode"];
    $constructOnly = array();
    if (isset($options["construct_only"]))
        $constructOnly = $options["construct_only"];
    //loop for subjects
    for ($i=0;$i<count($query);$i++) {
        //echo $query[$i];
        $res = $model->sparqlQuery($query[$i]);
        //if (count($res) == 0) break;
        if (count($res) == 0) continue;
        if (isset($res[0]["s"]) && !empty($res[0]["s"]))
            $subject = $res[0]["s"];
        else
            $subject = $defaultsubject;
        $stmtArray[$subject] = array();
        //loop for predicates
        for ($j=0;$j<count($res);$j++) {
            $predicate = $res[$j]["p"];
            //not allowed predicate for this subject
            if (isset($constructOnly[$subject]) && !in_array($predicate,$constructOnly[$subject]))
                continue;
            //is defines as bnode
            if (in_array($predicate,$bnodePredicates))
                $type = "bnode";
            else if (substr($res[$j]["o"],0,7)=="http://") //TODO: is this heuristic enough?
                $type = "uri";
            else
                $type = "literal";
            $stmtArray[$subject][$predicate][] = array( "type" => $type,
                "value" => $res[$j]["o"]);
        }
    }
    return $stmtArray;
}

/**
 * param $model Erfurt_Rdf_Model to be queried
 * param $resources array of resource names
 *         
 * @return array statment array usable by Erfurt_Rdf_Model->addMultipleStatements()
 */         
function constuctStmtArray2($store, $model, $resourceuris, $options)
{
    $stmtArray = array();
    $constructOnly = array();
    if (isset($options["construct_only"]))
        $constructOnly = $options["construct_only"];
    //loop for subjects
    for ($i=0;$i<count($resourceuris);$i++) {
        $subject = $resourceuris[$i];
        $resource = new OntoWiki_Model_Resource($store,$model,$subject);
        $values = $resource->getValues();
        if ($values == array()) //resource dn exist -> continue with next
            continue;
        $values = $values[$model->getModelIri()];
        $stmtArray[$subject] = array();
        //loop for predicates
        foreach ($values as $predicate => $predicate_values) {
            //not allowed predicate for this subject
            if (isset($constructOnly[$subject]) && !in_array($predicate,$constructOnly[$subject]))
                continue;
            foreach ($predicate_values as $value)
            {
                if (!empty($value["uri"])) {
                    $type = "uri";
                    $content = $value["uri"];
                    $stmtArray[$subject][$predicate][] = array( "type" => $type,
                        "value" => $content);
                }
                else {
                    $type = "literal";
                    $content = $value["content"];
                    $datatype = $value["datatype"];
                    $lang = $value["lang"];
                    $stmtArray[$subject][$predicate][] = array( "type" => $type,
                        "value" => $content,
                        "datatype" => $datatype,
                        "lang" => $lang );
                }
            }
        }
    }
    return $stmtArray;
}

function checkRequiredProperties($ow,$graph,$object,$required)
{
    $app = $ow;
    $store = $app->erfurt->getStore();
    $model = new OntoWiki_Model_Resource($store, $graph, (string)$object);
    $ojectpredicates = $model->getPredicates();
    $predicates = array_keys($ojectpredicates[(string)$graph]);
    $missing = array();
    foreach ($required as $onereq) {
        if (!in_array($onereq,$predicates))
            $missing[] = $onereq;
    }
    return $missing;
}