<?php

require_once 'Erfurt/Sparql/Query2.php';

class ContractsController extends OntoWiki_Controller_Component
{
    private $store;
    private $cache;
    private $translate;
    private $session;
    private $ac;
    private $model;
    /* an array of arrays, each has type and text */
    private $messages = array();
    /* the setup consists of state and config */
    private $setup = null;
    private $limit = 50;
    
    private $pref = array(
        "dct" => "http://purl.org/dc/terms/",
        "dcterms" => "http://purl.org/dc/terms/",
        "pc" => "http://purl.org/procurement/public-contracts#",
        "br" => "http://purl.org/business-register#",
        "gr" => "http://purl.org/goodrelations/v1#",
        "vcard" => "http://www.w3.org/2006/vcard/ns#",
        "xsd" => "http://www.w3.org/2001/XMLSchema#",
        "rdf" => "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        "rdfs" => "http://www.w3.org/2000/01/rdf-schema#");

    /***************************************************************************
     * Initializes Naviagation Controller,
     * creates class vars for current store, session and model
     */
    public function init()
    {
        parent::init();
        $this->store = $this->_owApp->erfurt->getStore();
        $this->translate = $this->_owApp->translate;
        $this->session = $this->_owApp->session->testModule;
        $this->ac = $this->_erfurt->getAc();
        
        $sessionKey = 'PcfaContracts' . (isset($config->session->identifier) ? $config->session->identifier : '');        
        $this->stateSession = new Zend_Session_Namespace($sessionKey);

        $this->model = $this->_owApp->selectedModel;
        if (isset($this->_request->m)) {
            $this->model = $store->getModel($this->_request->m);
        }
        if (empty($this->model)) {
            //throw new OntoWiki_Exception('Missing parameter m (model) and no selected model in session!');
            //exit;
            //select publicstore
            $this->_owApp->selectedModel = $this->store->getModel($this->_privateConfig->publicstore);
            $this->model = $this->_owApp->selectedModel;
        }
        // create title helper
        $this->titleHelper = new OntoWiki_Model_TitleHelper($this->model);
        
        // Model Based Access Control
        if (!$this->ac->isModelAllowed('view', $this->model->getModelIri()) ) {
            throw new Erfurt_Ac_Exception('You are not allowed to read this model.');
        }
    }
    
    /***************************************************************************
     * Sets or unsets user from contractors and suppliers group
     */         
    public function setusertypeAction()
    {
        $translate = $this->_owApp->translate;
        $windowTitle = $translate->_('Usergroup change');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        $configModel = $this->store->getModel('http://localhost/OntoWiki/Config/',false);
        $user = $this->_owApp->getUser();
        $options['use_ac'] = false;
        if (isset($_REQUEST["contractor"])) {
            if ($_REQUEST["contractor"] == "yes") {
                $this->store->addStatement($configModel->getModelIri(),
                    'http://localhost/OntoWiki/Config/Contractors',
                    'http://rdfs.org/sioc/ns#has_member',
                    array('value' => $user->getUri(), 'type'  => 'uri'),
                    false);
            } else if ($_REQUEST["contractor"] == "no") {
                $this->store->deleteMatchingStatements($configModel->getModelIri(),
                    'http://localhost/OntoWiki/Config/Contractors',
                    'http://rdfs.org/sioc/ns#has_member',
                    array('value' => $user->getUri(), 'type'  => 'uri'),
                    $options);
            }
        }
        if (isset($_REQUEST["supplier"])) {
            if ($_REQUEST["supplier"] == "yes") {
                $this->store->addStatement($configModel->getModelIri(),
                    'http://localhost/OntoWiki/Config/Suppliers',
                    'http://rdfs.org/sioc/ns#has_member',
                    array('value' => $user->getUri(), 'type'  => 'uri'),
                    false);
            } else if ($_REQUEST["supplier"] == "no") {
                $this->store->deleteMatchingStatements($configModel->getModelIri(),
                    'http://localhost/OntoWiki/Config/Suppliers',
                    'http://rdfs.org/sioc/ns#has_member',
                    array('value' => $user->getUri(), 'type'  => 'uri'),
                    $options);
            }
        }
    }
    
    /***************************************************************************
     * 
     */
    public function newbusinessentityAction()
    {
        $model = $this->_owApp->selectedModel;
        $translate = $this->_owApp->translate;
        $store = $this->_owApp->erfurt->getStore();
        $newentityprefix = $this->_privateConfig->resource->prefix;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $userprefix = $this->_privateConfig->user->prefix;
        $usruri = $userprefix.$username;
        
        $windowTitle = $translate->_('Create business entity');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        $dctp = $model->getNamespacePrefix('http://purl.org/dc/terms/');
        $xsdp = $model->getNamespacePrefix('http://www.w3.org/2001/XMLSchema#');
        
        $usergroups = ContractsHelper::getContractUseroups();
                
        if (isset($_POST["prop_gr_legalName"]) && $usergroups["any"]) //TODO: kontrola vsech atributu??
        {
            if (!$model->isEditable()) {
                $this->view->placeholder('added_resource')->set(false);
                return false;
            }
            $name = trim($_POST["prop_gr_legalName"]);
            $tid = trim($_POST["prop_br_officialNumber"]);
            $countryname = trim($_POST["prop_vcard_country-name"]);
            $locality = trim($_POST["prop_vcard_locality"]);
            $postalcode = trim($_POST["prop_vcard_postal-code"]);
            $street = trim($_POST["prop_vcard_street"]);
            
            if (strlen($tid) > 4) { //TODO: jake univerzalni pravidlo pro platny kod?
                $resuriend = strtoupper($countryname).strtolower($tid);
                $resuriend = str_replace(' ','-',$resuriend); //TODO: bezpecnejsi prevod na URL slug
            } else
                $resuriend = ContractsHelper::generateGuid();
            $resname = $newentityprefix."business-entity/".$resuriend;
            //$bnodePrefix = '_:'.$reshexcodelong;
            //$vcard = $bnodePrefix . '_vcard';
            //$vcard_adr = $bnodePrefix . '_vcard_adr';
            //$vcard_org = $bnodePrefix . '_vcard_org';
            $vcard = $resname . '/vcard-class/1';
            $vcard_adr = $vcard . '/vcard-address-class/1';
            $vcard_org = $vcard . '/vcard-organization-class/1';
            $stmtArray = array(
                $resname => array(
                    EF_RDF_TYPE => array(array(
                        'type'  => 'uri',
                        'value' => 'http://purl.org/goodrelations/v1#BusinessEntity' 
                    )),
                    'http://purl.org/goodrelations/v1#legalName' => array(array(
                        'type'  => 'literal',
                        'value' => $name
                    )),
                    'http://purl.org/business-register#officialNumber' => array(array(
                        'type'  => 'literal',
                        'value' => $tid
                    )),
                    'http://purl.org/business-register#contact' => array(array(
                        'type'  => 'uri', //bnode
                        'value' => $vcard
                    )),
                    $dctp.':creator' => array(array( //id toho kdo vytvoril BE
                        'type'  => 'uri',
                        'value' => $usruri
                    )),
                    $dctp.':created' => array(array( //datum vytvoreni BE
                        'type'  => 'literal', 'datatype' => $xsdp.':date',
                        'value' => Date("Y-m-d")
                    ))
                ),
                $vcard => array(
                    EF_RDF_TYPE => array(array(
                        'type'  => 'uri',
                        'value' => 'http://www.w3.org/2006/vcard/ns#VCard' 
                    )),
                    'http://www.w3.org/2006/vcard/ns#adr' => array(array(
                        'type'  => 'uri', //bnode
                        'value' => $vcard_adr
                    )),
                    'http://www.w3.org/2006/vcard/ns#org' => array(array(
                        'type'  => 'uri', //bnode
                        'value' => $vcard_org
                    ))
                ),
                $vcard_org => array(
                    EF_RDF_TYPE => array(array(
                        'type'  => 'uri',
                        'value' => 'http://www.w3.org/2006/vcard/ns#Organization' 
                    )),
                    'http://www.w3.org/2006/vcard/ns#organization-name' => array(array(
                        'type'  => 'literal',
                        'value' => $name
                    ))
                ),
                $vcard_adr => array(
                    EF_RDF_TYPE => array(array(
                        'type'  => 'uri',
                        'value' => 'http://www.w3.org/2006/vcard/ns#Address' 
                    )),
                    'http://www.w3.org/2006/vcard/ns#country-name' => array(array(
                        'type'  => 'literal',
                        'value' => $countryname
                    )),
                    'http://www.w3.org/2006/vcard/ns#locality' => array(array(
                        'type'  => 'literal',
                        'value' => $locality
                    )),
                    'http://www.w3.org/2006/vcard/ns#postal-code' => array(array(
                        'type'  => 'literal',
                        'value' => $postalcode
                    )),
                    'http://www.w3.org/2006/vcard/ns#street' => array(array(
                        'type'  => 'literal',
                        'value' => $street
                    ))
                )
            );
            
            $store->addMultipleStatements($model->getModelIri(), $stmtArray, true);
            
            //when creating business entity describing himself
            if (isset($_GET["self"]) && ($_GET["self"] == "true") && (ContractsHelper::getUserBusiness() === false)) {
                $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
                $user = $this->_owApp->getUser();
                $username = $user->getUsername();
                $useruri = 'http://localhost/OntoWiki/Config/'.$username; //je spolehlive? TODO: najit lepsi zpusob zjisteni uri uzivatele
                //add information about business entity to user profile
                $predicate = $this->_privateConfig->contracts->configns.$this->_privateConfig->ownbusiness->predicate;
                $store->addStatement($configModel->getModelIri(),
                    $useruri,
                    $predicate,
                    array('value' => $resname, 'type'  => 'uri'),
                    false);
                $this->view->placeholder('is_own_business')->set(true);
                //echo "iri:",$configModel->getModelIri()," sub:",$useruri," pre:",$predicate," obj:",$resname;
            } else
                $this->view->placeholder('is_own_business')->set(false);
            
            $this->view->placeholder('added_resource')->set($resname);

            //$resource = $model->getResource($resname);
            //$this->_owApp->selectedResource = $resource;
        } else {
            $this->view->placeholder('added_resource')->set(false);
        }
    }
    
    /***************************************************************************
     * 
     */
    public function newcontractAction()
    {
        $model = $this->_owApp->selectedModel;
        $translate = $this->_owApp->translate;
        $store = $this->_owApp->erfurt->getStore();
        $newentityprefix = $this->_privateConfig->resource->prefix;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $userprefix = $this->_privateConfig->user->prefix;
        $usruri = $userprefix.$username;
        
        $windowTitle = $translate->_('Create contract');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        $usergroups = ContractsHelper::getContractUseroups();
        $businesses = ContractsHelper::getUserBusiness();
        if ($usergroups["contractor"] && ($businesses !== false)) {
            //$nid = dechex(rand(16777216,268435455)); //tj mezi 0x1000000 a 0xFFFFFFF
            $nid = ContractsHelper::generateGuid();
            $resname = $newentityprefix.'public-contract/'.$nid;
            $priceflaturi = $resname."/price-specification/1";
            $priceminuri  = $resname."/price-specification/2";
            $pricemaxuri  = $resname."/price-specification/3";
            $contacturi  = $resname."/vcard-class/1";
            $adruri  = $contacturi."/vcard-address-class/1";
            $orguri  = $contacturi."/vcard-organization-class/1";
            $business = $businesses[0];
            try {
                $this->store->addStatement($model->getModelIri(),
                    $resname,
                    EF_RDF_TYPE,
                    array('value' => 'http://purl.org/procurement/public-contracts#Contract', 'type'  => 'uri'),
                    true);
                /*$this->store->addStatement($model->getModelIri(),
                    $resname,
                    'http://purl.org/dc/terms/title',
                    array('value' => 'new public contract', 'type'  => 'literal', 'datatype' => 'xsd:string'),
                    true);*/
                $this->store->addStatement($model->getModelIri(),
                    $resname,
                    'http://purl.org/procurement/public-contracts#contractingAuthority',
                    array('value' => $business, 'type'  => 'uri'),
                    true);
                //creator id
                $this->store->addStatement($model->getModelIri(),
                    $resname,
                    'http://purl.org/dc/terms/creator',
                    array('value' => $usruri, 'type'  => 'uri'), true);
                //exact estimated price
                $this->store->addStatement($model->getModelIri(),
                    $priceflaturi, EF_RDF_TYPE,
                    array('value' => 'http://purl.org/goodrelations/v1#PriceSpecification', 'type'  => 'uri'), true);
                $this->store->addStatement($model->getModelIri(),
                    $resname,'http://purl.org/procurement/public-contracts#estimatedPrice',
                    array('value' => $priceflaturi, 'type'  => 'uri'), true);
                //min estimated price
                $this->store->addStatement($model->getModelIri(),
                    $priceminuri, EF_RDF_TYPE,
                    array('value' => 'http://purl.org/goodrelations/v1#PriceSpecification', 'type'  => 'uri'), true);
                $this->store->addStatement($model->getModelIri(),
                    $resname,'http://purl.org/procurement/public-contracts#estimatedPriceLower',
                    array('value' => $priceminuri, 'type'  => 'uri'), true);
                //max estimated price
                $this->store->addStatement($model->getModelIri(),
                    $pricemaxuri, EF_RDF_TYPE,
                    array('value' => 'http://purl.org/goodrelations/v1#PriceSpecification', 'type'  => 'uri'), true);
                $this->store->addStatement($model->getModelIri(),
                    $resname,'http://purl.org/procurement/public-contracts#estimatedPriceUpper',
                    array('value' => $pricemaxuri, 'type'  => 'uri'), true);
                //Address & Organization -> get from business
                $options = array();
                $query2[0] = 'SELECT ?s ?p ?o WHERE {
                    <'.$business.'> <http://purl.org/business-register#contact> ?y .
                    ?y <http://www.w3.org/2006/vcard/ns#adr> ?x .
                    ?x ?p ?o. }';
                $options["default_subject"] = $adruri;
                $stmtArray2 = ContractsHelper::constuctStmtArray($model,$query2,$options);
                $query3[0] = 'SELECT ?s ?p ?o WHERE {
                    <'.$business.'> <http://purl.org/business-register#contact> ?y .
                    ?y <http://www.w3.org/2006/vcard/ns#org> ?x .
                    ?x ?p ?o. }';
                $options["default_subject"] = $orguri;
                $stmtArray3 = ContractsHelper::constuctStmtArray($model,$query3,$options);
                $stmtArray = array_merge($stmtArray2,$stmtArray3);
                $store->addMultipleStatements($model->getModelIri(), $stmtArray, true);
                //VCard contact
                $this->store->addStatement($model->getModelIri(),
                    $contacturi, EF_RDF_TYPE,
                    array('value' => $this->pref["vcard"].'VCard', 'type'  => 'uri'), true);
                $this->store->addStatement($model->getModelIri(),
                    $contacturi, $this->pref["vcard"].'adr',
                    array('value' => $adruri, 'type'  => 'uri'), true);
                $this->store->addStatement($model->getModelIri(),
                    $contacturi, $this->pref["vcard"].'org',
                    array('value' => $orguri, 'type'  => 'uri'), true);
                $this->store->addStatement($model->getModelIri(),
                    $resname,'http://purl.org/procurement/public-contracts#contact',
                    array('value' => $contacturi, 'type'  => 'uri'), true);
                
                $this->view->placeholder('added_resource')->set($resname);
            } catch (Exception $e) {
                //echo $e->getMessage();
                $this->view->placeholder('added_resource')->set(false);
                //no ACL to edit
            }
        } else
            $this->view->placeholder('added_resource')->set(false);
    }
    
    /***************************************************************************
     * 
     */
    public function updatecontractAction()
    {
        return; //deprecated???
        /*
        $translate  = $this->_owApp->translate;
        $store      = $this->_owApp->erfurt->getStore();
        //$model      = $this->_owApp->selectedModel;
        $graph      = $this->_owApp->selectedModel;
        $resource   = $this->_owApp->selectedResource;
        //$navigation = $this->_owApp->navigation;
        //$resourceMenu = OntoWiki_Menu_Registry::getInstance()->getMenu('contracts');
        
        $title = $resource->getTitle($this->_config->languages->locale) 
               ? $resource->getTitle($this->_config->languages->locale) 
               : OntoWiki_Utils::contractNamespace((string)$resource);
        
        $windowTitle = $translate->_(sprintf('Update contract %1$s',$title));
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        if (!empty($resource)) {
            $event = new Erfurt_Event('onPrePropertiesContentAction');
            $event->uri = (string)$resource;
            $result = $event->trigger();

            if ($result) {
                $this->view->prePropertiesContent = $result;
            }

            $model = new OntoWiki_Model_Resource($store, $graph, (string)$resource);
            $values = $model->getValues();
            $predicates = $model->getPredicates();
        }*/
    }

    /***************************************************************************
     * 
     */
    public function publishbusinessAction()
    {
        $store = $this->_owApp->erfurt->getStore();
        $model = $this->_owApp->selectedModel;
        $publicmodeluri = $this->_privateConfig->publicstore;
        $publicmodel = new Erfurt_Rdf_Model($publicmodeluri);
        $resource = $this->_owApp->selectedResource;
        $resourceuri = (string)$resource;
        $title = $resource->getTitle();
        $translate   = $this->_owApp->translate;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $userprefix = $this->_privateConfig->user->prefix;
        $usruri = $userprefix.$username;
        //$rUriEncoded = urlencode((string)$resource);
        //$mUriEncoded = urlencode((string)$model);
        
        $windowTitle = sprintf($translate->_('Publish business entity %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        $xsdp = $model->getNamespacePrefix('http://www.w3.org/2001/XMLSchema#');
        
        //check if it doesnt exist
        $res = $publicmodel->sparqlQuery('SELECT ?x WHERE {<'.$resourceuri.'> ?z ?x}');
        if ($res !== array()) {
            //resource already exists
            $this->view->placeholder('already_published')->set(true);
        }
        
        //$serialized = $resource->serialize('n3');
        //echo nl2br(htmlspecialchars($serialized));
        //} catch (Exception $e) { echo $e->getMessage(); }
        //$resmod = new OntoWiki_Model_Resource($store,$model,(string)$resource);
        //$resmod->getValues(); //getPredicates();
        
        $query[0] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> ?p ?o. }';
        $query[1] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <http://purl.org/business-register#contact> ?s.
            ?s ?p ?o. }';
        $query[2] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <http://purl.org/business-register#contact> ?c.
            ?c <http://www.w3.org/2006/vcard/ns#org> ?s.
            ?s ?p ?o. }';
        $query[3] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <http://purl.org/business-register#contact> ?c.
            ?c <http://www.w3.org/2006/vcard/ns#adr> ?s.
            ?s ?p ?o. }';
        $options = array();
        $options["default_subject"] = $resourceuri;
        //$options["bnode"][] = "http://purl.org/business-register#contact";
        //$options["bnode"][] = "http://www.w3.org/2006/vcard/ns#adr";
        //$options["bnode"][] = "http://www.w3.org/2006/vcard/ns#org";
        $stmtArray = ContractsHelper::constuctStmtArray($model,$query,$options);
        //print_r($stmtArray);
        $stmtArray[$resourceuri]["http://purl.org/dc/terms/publisher"] = array(array(
            'type'  => 'uri',
            'value' => $usruri
        ));
        $stmtArray[$resourceuri]["http://purl.org/procurement/public-contracts#publicationDate"] = array(array(
            'type'  => 'literal', 'datatype' => $xsdp.'date',
            'value' => Date("Y-m-d") 
        ));
        $store->addMultipleStatements($publicmodel->getModelIri(), $stmtArray, false);
        $this->view->placeholder('published_business')->set(true);
    }
    
    /***************************************************************************
     * 
     */
    public function publishpriornoticeAction()
    {
        $store = $this->_owApp->erfurt->getStore();
        $model = $this->_owApp->selectedModel;
        $modeluri = (string)$model;
        $publicmodeluri = $this->_privateConfig->publicstore;
        $publicmodel = new Erfurt_Rdf_Model($publicmodeluri);
        $ontomodeluri = "http://purl.org/procurement/public-contracts#";
        $ontomodel = new Erfurt_Rdf_Model($ontomodeluri);
        $resource = $this->_owApp->selectedResource;
        $resourceuri = (string)$resource;
        $userprefix = $this->_privateConfig->user->prefix;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $title = $resource->getTitle();
        $translate   = $this->_owApp->translate;
        $xsdp = $model->getNamespacePrefix($this->pref["xsd"]);
        //window title
        $windowTitle = sprintf($translate->_('Publish prior information notice for contract %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
        //CHECK EXISTENCE IN PUBLIC STORE
        //try {
        $res_inpublic = $publicmodel->sparqlQuery('SELECT ?x
            WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?x .
                ?x a <'.$this->pref["pc"].'PriorInformationNotice> .
                }');
        //} catch (Exception $e) { echo $e->getMessage(); }
        if ($res_inpublic !== array()) {
            //resource already exists
            $this->view->placeholder('already_published')->set(true);
            return; //can not republish
        }
        $this->view->placeholder('already_published')->set(false);
        //CHECK REQUIRED PROPERTIES
        $required = array();
        $required[] = EF_RDF_TYPE;
        $required[] = $this->pref["dcterms"]."title";
        $required[] = $this->pref["dcterms"]."description";
        $required[] = $this->pref["pc"]."contact";
        $required[] = $this->pref["pc"]."contractingAuthority";
        /*$required[] = $this->pref["pc"]."estimatedPrice"; //checked further on
        $required[] = $this->pref["pc"]."estimatedPriceLower";
        $required[] = $this->pref["pc"]."estimatedPriceUpper";*/
        /*$required[] = $this->pref["pc"]."notice";*/ //added further on, o check required
        //TODO: add support for kind + mainObject
        //$required[] = $this->pref["pc"]."kind";
        //$required[] = $this->pref["pc"]."mainObject";
        $required[] = $this->pref["pc"]."referenceNumber";
        $missing = ContractsHelper::checkRequiredProperties($resourceuri,$required);
        $this->view->placeholder('missing')->set($missing);
        //check price: estimatedPrice OR estimatedPriceLower + estimatedPriceUpper
        $query_prices = 'SELECT ?p ?o ?ov ?oc
            WHERE {
                <'.$resourceuri.'> a <'.$this->pref["pc"].'Contract> ;
                  ?p ?o .
                ?o a <'.$this->pref["gr"].'PriceSpecification> ;
                  <'.$this->pref["gr"].'hasCurrencyValue> ?ov ;
                  <'.$this->pref["gr"].'hasCurrency> ?oc .
            }';
        $res_prices = $model->sparqlQuery($query_prices);
        $ep = $epl = $epu = $price_problem = false;
        foreach ($res_prices as $r) {
            if ($r["p"] == $this->pref["pc"].'estimatedPrice') $ep = true;
            if ($r["p"] == $this->pref["pc"].'estimatedPriceLower') $epl = true;
            if ($r["p"] == $this->pref["pc"].'estimatedPriceUpper') $epu = true;
        }
        if ($ep == ($epl && $epu)) //both missing or both set
            $price_problem = true;
        $this->view->placeholder('price_problem')->set($price_problem);
        //CAN NOT PUBLISH (ERRORS)
        if (!(($missing == array()) && ($ep xor ($epl && $epu)))) {
            /* //invalidate cache //TODO: invalidate only executed queries (not whole model)
            $cache = $this->_owApp->erfurt->getQueryCache();
            $cache->invalidateWithModelIri($model->getModelIri());*/
            $this->view->placeholder('published_priornotice')->set(false);
            return;
        }
        //LOAD CONTRACT DETAILS
        //stmt array sparql queries
        $query[0] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> ?p ?o. }';
        if ($ep) {
            $query[1] = 'SELECT ?s ?p ?o WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'estimatedPrice> ?s.
                ?s ?p ?o. }';
        } else {
            $query[1] = 'SELECT ?s ?p ?o WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'estimatedPriceLower> ?s.
                ?s ?p ?o. }';
            $query[2] = 'SELECT ?s ?p ?o WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'estimatedPriceUpper> ?s.
                ?s ?p ?o. }';
        }
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'location> ?s.
            ?s ?p ?o. }';
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'contact> ?s.
            ?s ?p ?o. }';
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'contact> ?x.
            ?x <'.$this->pref["vcard"].'adr> ?s.
            ?s ?p ?o. }';
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'contact> ?x.
            ?x <'.$this->pref["vcard"].'org> ?s.
            ?s ?p ?o. }';
        /*$query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'mainObject> ?s.
            ?s ?p ?o. }';*/
        //invalidate cache //TODO: invalidate only executed queries (not whole model)
        $cache = $this->_owApp->erfurt->getQueryCache();
        $cache->invalidateWithModelIri($model->getModelIri());
        
        $options = array();
        $options["default_subject"] = $resourceuri;
        $options["construct_only"][$resourceuri][] = EF_RDF_TYPE;
        $options["construct_only"][$resourceuri][] = $this->pref["dcterms"]."title";
        $options["construct_only"][$resourceuri][] = $this->pref["dcterms"]."description";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."contact";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."contractingAuthority";
        if ($ep)
            $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedPrice";
        else {
            $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedPriceLower";
            $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedPriceUpper";
        }
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."location";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."kind";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."mainObject";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."notice";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."referenceNumber";
        $stmtArray1 = ContractsHelper::constuctStmtArray($model,$query,$options);        
        //CREATION OF PRIOR INFORMATION NOTICE
        $pinuri = $resourceuri."/prior-information-notice/1";
        $publisheruri = $userprefix.$username;
        //select creator
        $query_creator = 'SELECT ?creator WHERE {
            <'.$resourceuri.'> <'.$this->pref["dcterms"].'creator> ?creator. }';
        $res = $model->sparqlQuery($query_creator);
        if ($res == array())
            $creatoruri = $publisheruri;
        else
            $creatoruri = $res[0]["creator"];
        $publicationdate = Date("Y-m-d");
        $fillingAppUri = $this->_privateConfig->fillingapp->uri;
        $stmtArray2 = array(
            $pinuri => array(
                EF_RDF_TYPE => array(array(
                    'type'  => 'uri',
                    'value' => 'http://purl.org/procurement/public-contracts#PriorInformationNotice' 
                ),
                array(
                    'type'  => 'uri',
                    'value' => 'http://www.w3.org/ns/prov#Entity' 
                )),
                'http://www.w3.org/ns/prov#wasAttributedTo' => array(array(
                    'type'  => 'uri',
                    'value' => $fillingAppUri
                )),
                'http://purl.org/dc/terms/creator' => array(array( //id toho kdo vytvoril kontrakt
                    'type'  => 'uri',
                    'value' => $creatoruri
                )),
                'http://purl.org/dc/terms/publisher' => array(array( //id toho kdo publikuje
                    'type'  => 'uri',
                    'value' => $publisheruri
                )),
                'http://purl.org/procurement/public-contracts#publicationDate' => array(array(
                    'type'  => 'literal', 'datatype' => $xsdp.':date',
                    'value' => $publicationdate
                ))
        ));
        //add notice link
        $stmtArray1[$resourceuri][$this->pref["pc"]."notice"][] = array(
            'type'  => 'uri',
            'value' => $pinuri
        );
        $stmtArray = array_merge($stmtArray1,$stmtArray2);
        //print_r($stmtArray);
        //PUBLISH
        $store->addMultipleStatements($publicmodel->getModelIri(), $stmtArray, false);
        $this->view->placeholder('published_priornotice')->set(true);
    }
    
    /***************************************************************************
     * 
     */
    public function publishnoticeAction()
    {
        $store = $this->_owApp->erfurt->getStore();
        $model = $this->_owApp->selectedModel;
        $modeluri = (string)$model;
        $publicmodeluri = $this->_privateConfig->publicstore;
        $publicmodel = new Erfurt_Rdf_Model($publicmodeluri);
        $ontomodeluri = "http://purl.org/procurement/public-contracts#";
        $ontomodel = new Erfurt_Rdf_Model($ontomodeluri);
        $resource = $this->_owApp->selectedResource;
        $resourceuri = (string)$resource;
        $userprefix = $this->_privateConfig->user->prefix;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $title = $resource->getTitle();
        $translate   = $this->_owApp->translate;
        $xsdp = $model->getNamespacePrefix($this->pref["xsd"]);
        //window title
        $windowTitle = sprintf($translate->_('Publish contract notice for contract %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
        //CHECK IF PRIOR NOTICE PUBLISHED
        $res_priorpubished = $publicmodel->sparqlQuery('SELECT ?x
            WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?x .
                ?x a <'.$this->pref["pc"].'PriorInformationNotice> .
                }');
        if ($res_priorpubished == array()) {
            //resource does not exist
            $this->view->placeholder('published_priornotice')->set(false);
            return; //can not publish notice
        }
        $this->view->placeholder('published_priornotice')->set(true);
        //CHECK EXISTENCE IN PUBLIC STORE
        $res_inpublic = $publicmodel->sparqlQuery('SELECT ?x
            WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?x .
                ?x a <'.$this->pref["pc"].'ContractNotice> .
                }');
        if ($res_inpublic !== array()) {
            //resource already exists
            $this->view->placeholder('already_published')->set(true);
            return; //can not republish
        }
        $this->view->placeholder('already_published')->set(false);
        
        //following similar to prior information notice
        //CHECK REQUIRED PROPERTIES
        $required = array();
        $required[] = EF_RDF_TYPE;
        $required[] = $this->pref["dcterms"]."title";
        $required[] = $this->pref["dcterms"]."description";
        $required[] = $this->pref["pc"]."contact";
        $required[] = $this->pref["pc"]."contractingAuthority";
        $required[] = $this->pref["pc"]."tenderDeadline";
        //TODO: add support for kind + mainObject + procedureType
        //$required[] = $this->pref["pc"]."kind";
        //$required[] = $this->pref["pc"]."mainObject";
        //$required[] = $this->pref["pc"]."procedureType";
        $required[] = $this->pref["pc"]."referenceNumber";
        $missing = ContractsHelper::checkRequiredProperties($resourceuri,$required);
        $this->view->placeholder('missing')->set($missing);
        //check price: estimatedPrice OR estimatedPriceLower + estimatedPriceUpper
        $query_prices = 'SELECT ?p ?o ?ov ?oc
            WHERE {
                <'.$resourceuri.'> a <'.$this->pref["pc"].'Contract> ;
                  ?p ?o .
                ?o a <'.$this->pref["gr"].'PriceSpecification> ;
                  <'.$this->pref["gr"].'hasCurrencyValue> ?ov ;
                  <'.$this->pref["gr"].'hasCurrency> ?oc .
            }';
        $res_prices = $model->sparqlQuery($query_prices);
        $ep = $epl = $epu = $price_problem = false;
        foreach ($res_prices as $r) {
            if ($r["p"] == $this->pref["pc"].'estimatedPrice') $ep = true;
            if ($r["p"] == $this->pref["pc"].'estimatedPriceLower') $epl = true;
            if ($r["p"] == $this->pref["pc"].'estimatedPriceUpper') $epu = true;
        }
        if ($ep == ($epl && $epu)) //both missing or both set
            $price_problem = true;
        $this->view->placeholder('price_problem')->set($price_problem);
        //CAN NOT PUBLISH (ERRORS)
        if (!(($missing == array()) && ($ep xor ($epl && $epu)))) {
            $this->view->placeholder('published_contractnotice')->set(false);
            return;
        }
        //LOAD CONTRACT DETAILS
        //stmt array sparql queries
        $query[0] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> ?p ?o. }';
        if ($ep) {
            $query[1] = 'SELECT ?s ?p ?o WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'estimatedPrice> ?s.
                ?s ?p ?o. }';
        } else {
            $query[1] = 'SELECT ?s ?p ?o WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'estimatedPriceLower> ?s.
                ?s ?p ?o. }';
            $query[2] = 'SELECT ?s ?p ?o WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'estimatedPriceUpper> ?s.
                ?s ?p ?o. }';
        }
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'location> ?s.
            ?s ?p ?o. }';
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'contact> ?s.
            ?s ?p ?o. }';
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'contact> ?x.
            ?x <'.$this->pref["vcard"].'adr> ?s.
            ?s ?p ?o. }';
        $query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'contact> ?x.
            ?x <'.$this->pref["vcard"].'org> ?s.
            ?s ?p ?o. }';
        /*$query[] = 'SELECT ?s ?p ?o WHERE {
            <'.$resourceuri.'> <'.$this->pref["pc"].'awardCriteriaCombination> ?s.
            ?s ?p ?o. }';*/
        //invalidate cache //TODO: invalidate only executed queries (not whole model)
        $cache = $this->_owApp->erfurt->getQueryCache();
        $cache->invalidateWithModelIri($model->getModelIri());
        
        $options = array();
        $options["default_subject"] = $resourceuri;
        $options["construct_only"][$resourceuri][] = EF_RDF_TYPE;
        $options["construct_only"][$resourceuri][] = $this->pref["dcterms"]."title";
        $options["construct_only"][$resourceuri][] = $this->pref["dcterms"]."description";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."contact";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."contractingAuthority";
        if ($ep)
            $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedPrice";
        else {
            $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedPriceLower";
            $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedPriceUpper";
        }
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."location";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."kind";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."mainObject";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."notice";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."referenceNumber";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."tenderDeadline";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."procedureType";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."estimatedEndDate";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."awardCriteriaCombination";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."previousNotice";
        $stmtArray1 = ContractsHelper::constuctStmtArray($model,$query,$options);        
        //CREATION OF CONTRACT NOTICE
        $pinuri = $resourceuri."/contract-notice/1";
        $publisheruri = $userprefix.$username;
        //select creator
        $query_creator = 'SELECT ?creator WHERE {
            <'.$resourceuri.'> <'.$this->pref["dcterms"].'creator> ?creator. }';
        $res = $model->sparqlQuery($query_creator);
        if ($res == array())
            $creatoruri = $publisheruri;
        else
            $creatoruri = $res[0]["creator"];
        $publicationdate = Date("Y-m-d");
        $fillingAppUri = $this->_privateConfig->fillingapp->uri;
        $stmtArray2 = array(
            $pinuri => array(
                EF_RDF_TYPE => array(array(
                    'type'  => 'uri',
                    'value' => 'http://purl.org/procurement/public-contracts#ContractNotice' 
                ),
                array(
                    'type'  => 'uri',
                    'value' => 'http://www.w3.org/ns/prov#Entity' 
                )),
                'http://www.w3.org/ns/prov#wasAttributedTo' => array(array(
                    'type'  => 'uri',
                    'value' => $fillingAppUri
                )),
                'http://purl.org/dc/terms/creator' => array(array( //id toho kdo vytvoril kontrakt
                    'type'  => 'uri',
                    'value' => $creatoruri
                )),
                'http://purl.org/dc/terms/publisher' => array(array( //id toho kdo publikuje
                    'type'  => 'uri',
                    'value' => $publisheruri
                )),
                'http://purl.org/procurement/public-contracts#publicationDate' => array(array(
                    'type'  => 'literal', 'datatype' => $xsdp.':date',
                    'value' => $publicationdate
                ))
        ));
        //add notice link
        $stmtArray1[$resourceuri][$this->pref["pc"]."notice"][] = array(
            'type'  => 'uri',
            'value' => $pinuri
        );
        $stmtArray = array_merge($stmtArray1,$stmtArray2);
        //print_r($stmtArray);
        //PUBLISH
        $store->addMultipleStatements($publicmodel->getModelIri(), $stmtArray, false);
        $this->view->placeholder('published_contractnotice')->set(true);
    }
    
    /***************************************************************************
     * 
     */
    public function cancelcontractAction()
    {
        $store = $this->_owApp->erfurt->getStore();
        $model = $this->_owApp->selectedModel;
        $modeluri = (string)$model;
        $publicmodeluri = $this->_privateConfig->publicstore;
        $publicmodel = new Erfurt_Rdf_Model($publicmodeluri);
        $ontomodeluri = "http://purl.org/procurement/public-contracts#";
        $ontomodel = new Erfurt_Rdf_Model($ontomodeluri);
        $resource = $this->_owApp->selectedResource;
        $resourceuri = (string)$resource;
        $userprefix = $this->_privateConfig->user->prefix;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $title = $resource->getTitle();
        $translate   = $this->_owApp->translate;
        $xsdp = $model->getNamespacePrefix($this->pref["xsd"]);
        //window title
        $windowTitle = sprintf($translate->_('Cancel contract %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
        //CHECK IF PRIOR NOTICE PUBLISHED
        $res_priorpubished = $publicmodel->sparqlQuery('SELECT ?x
            WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?x .
                ?x a <'.$this->pref["pc"].'PriorInformationNotice> .
                }');
        if ($res_priorpubished == array()) {
            //resource does not exist
            $this->view->placeholder('published_priornotice')->set(false);
            return; //can not publish notice
        }
        $this->view->placeholder('published_priornotice')->set(true);
        //CHECK EXISTENCE IN PUBLIC STORE
        $res_inpublic = $publicmodel->sparqlQuery('SELECT ?x
            WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?x .
                ?x a <'.$this->pref["pc"].'CancellationNotice> .
                }');
        if ($res_inpublic !== array()) {
            //resource already exists
            $this->view->placeholder('already_published')->set(true);
            return; //can not republish
        }
        $this->view->placeholder('already_published')->set(false);
        //CREATION OF CANCELLATION NOTICE
        $pinuri = $resourceuri."/cancellation-notice/1";
        $publisheruri = $userprefix.$username;
        //select creator
        $query_creator = 'SELECT ?creator WHERE {
            <'.$resourceuri.'> <'.$this->pref["dcterms"].'creator> ?creator. }';
        $res = $model->sparqlQuery($query_creator);
        if ($res == array())
            $creatoruri = $publisheruri;
        else
            $creatoruri = $res[0]["creator"];
        $publicationdate = Date("Y-m-d");
        $fillingAppUri = $this->_privateConfig->fillingapp->uri;
        $stmtArray = array(
            $resourceuri => array( //add notice link
                $this->pref["pc"]."notice" => array(array(
                    'type'  => 'uri',
                    'value' => $pinuri
                ))
            ),
            $pinuri => array( //create notice
                EF_RDF_TYPE => array(array(
                    'type'  => 'uri',
                    'value' => 'http://purl.org/procurement/public-contracts#CancellationNotice' 
                ),
                array(
                    'type'  => 'uri',
                    'value' => 'http://www.w3.org/ns/prov#Entity'
                )),
                'http://www.w3.org/ns/prov#wasAttributedTo' => array(array(
                    'type'  => 'uri',
                    'value' => $fillingAppUri
                )),
                'http://purl.org/dc/terms/creator' => array(array( //id toho kdo vytvoril kontrakt
                    'type'  => 'uri',
                    'value' => $creatoruri
                )),
                'http://purl.org/dc/terms/publisher' => array(array( //id toho kdo publikuje
                    'type'  => 'uri',
                    'value' => $publisheruri
                )),
                'http://purl.org/procurement/public-contracts#publicationDate' => array(array(
                    'type'  => 'literal', 'datatype' => $xsdp.':date',
                    'value' => $publicationdate
                ))
        ));
        //print_r($stmtArray);
        //PUBLISH
        $store->addMultipleStatements($publicmodel->getModelIri(), $stmtArray, false);
        $this->view->placeholder('published_cancellationnotice')->set(true);
    }

    /***************************************************************************
     * 
     */
    public function newtenderAction()
    {
        $model = $this->_owApp->selectedModel;
        $translate = $this->_owApp->translate;
        $store = $this->_owApp->erfurt->getStore();
        $user = $this->_owApp->getUser();
        $resource = $this->_owApp->selectedResource;
        $resourceuri = (string)$resource;
        
        $windowTitle = $translate->_('Create tender');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        //check user rights
        $usergroups = ContractsHelper::getContractUseroups();
        $businesses = ContractsHelper::getUserBusiness();
        if ($usergroups["contractor"] && ($businesses !== false)) {
            //check if contractNotice was already published
            $res3 = $model->sparqlQuery('SELECT ?x
                    WHERE {
                        <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?z ;
                            a <'.$this->pref["pc"].'Contract> .
                        ?z a <'.$this->pref["pc"].'ContractNotice> .
                    }');
            if ($res3 !== array()) {
                $this->view->placeholder('contract_notice_already_published')->set(true);
                $this->view->placeholder('added_resource')->set(false);
                return;
            }
            $this->view->placeholder('contract_notice_already_published')->set(false);
            //check id of previous tender
            $res = $model->sparqlQuery('SELECT ?x
                WHERE {
                    <'.$resourceuri.'> <'.$this->pref["pc"].'tender> ?x .
                    ?x a <'.$this->pref["pc"].'Tender> .
                    }');
            $nexti = 1;
            foreach ($res as $r) {
                $found = $r["x"];
                $tenderi = substr($found,strrpos($found,"/")+1);
                if ($tenderi >= $nexti)
                    $nexti = $tenderi + 1;
            }
            $resname = $resourceuri.'/tender/'.$nexti;
            $priceuri = $resname."/price-specification/1";
            try {
                $stmtArray = array(
                    $resourceuri => array(
                        $this->pref["pc"].'tender' => array(array(
                            'type'  => 'uri',
                            'value' => $resname
                        ))
                    ),
                    $resname => array(
                        EF_RDF_TYPE => array(array(
                            'type'  => 'uri',
                            'value' => $this->pref["pc"].'Tender' 
                        )),
                        $this->pref["pc"].'offeredPrice' => array(array(
                            'type'  => 'uri',
                            'value' => $priceuri 
                        ))
                        //TODO: +supplier
                    ),
                    $priceuri => array(
                        EF_RDF_TYPE => array(array(
                            'type'  => 'uri',
                            'value' => $this->pref["gr"].'PriceSpecification' 
                        )),
                ));
                //print_r($stmtArray);
                $store->addMultipleStatements($model->getModelIri(), $stmtArray, true);
                /*$store->addStatement($model->getModelIri(),
                    $resourceuri, $this->pref["pc"].'tender',
                    array('value' => $resname, 'type'  => 'uri'), true);*/
                $this->view->placeholder('added_resource')->set($resname);
                //invalidate cache (po publish nebylo vidět v properties contractu)
                $cache = $this->_owApp->erfurt->getQueryCache();
                $cache->invalidateWithModelIri($model->getModelIri());
            } catch (Exception $e) {
                $this->view->placeholder('added_resource')->set(false);
            }
        } else
            $this->view->placeholder('added_resource')->set(false);
    }
    
    /***************************************************************************
     * 
     */
    public function awardtenderAction()
    {
        $model = $this->_owApp->selectedModel;
        $publicmodeluri = $this->_privateConfig->publicstore;
        $publicmodel = new Erfurt_Rdf_Model($publicmodeluri);
        $translate = $this->_owApp->translate;
        $store = $this->_owApp->erfurt->getStore();
        $user = $this->_owApp->getUser();
        $resource = $this->_owApp->selectedResource;
        $resource_uri = (string)$resource;
        $userprefix = $this->_privateConfig->user->prefix;
        $user = $this->_owApp->getUser();
        $username = $user->getUsername();
        $xsdp = $model->getNamespacePrefix($this->pref["xsd"]);
        
        $windowTitle = $translate->_('Award tender');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        //check user rights
        $usergroups = ContractsHelper::getContractUseroups();
        $businesses = ContractsHelper::getUserBusiness();
        if ($usergroups["contractor"] && ($businesses !== false)) {
            //check if tender is linked to contract
            $res = $model->sparqlQuery('SELECT ?x
                WHERE {
                    ?x <'.$this->pref["pc"].'tender> <'.$resource_uri.'> ;
                        a <'.$this->pref["pc"].'Contract> .
                    <'.$resource_uri.'> a <'.$this->pref["pc"].'Tender> .
                }');
            if ($res == array()) {
                $this->view->placeholder('tender_not_linked')->set(true);
            }
            else {
                $resource_contract_uri = $res[0]["x"];
                //check if contractNotice was published //TODO: zjistit zda je toto omezeni platne
                $res3 = $model->sparqlQuery('SELECT ?x
                    WHERE {
                        <'.$resource_contract_uri.'> <'.$this->pref["pc"].'notice> ?z ;
                            a <'.$this->pref["pc"].'Contract> .
                        ?z a <'.$this->pref["pc"].'ContractNotice> .
                    }');
                if ($res3 !== array()) {
                    $this->view->placeholder('contract_notice_already_published')->set(true);
                    $this->view->placeholder('tender_awarded')->set(false);
                    return;
                }
                $this->view->placeholder('contract_notice_already_published')->set(false);
                //check if there already is awarded tender for contract
                $res2 = $model->sparqlQuery('SELECT ?y
                    WHERE {
                        ?x <'.$this->pref["pc"].'tender> <'.$resource_uri.'> ;
                            a <'.$this->pref["pc"].'Contract> ;
                            <'.$this->pref["pc"].'awardedTender> ?y.
                        <'.$resource_uri.'> a <'.$this->pref["pc"].'Tender> .
                    }');
                if ($res2 != array()) {
                    $contract_already_awarded = $res2[0]["y"];
                    $this->view->placeholder('contract_already_awarded')->set($contract_already_awarded);
                    $this->view->placeholder('tender_awarded')->set(false);
                    return;
                }
                else {
                    $this->view->placeholder('contract_already_awarded')->set(false);
                    $this->view->placeholder('tender_not_linked')->set(false);
                    $this->view->placeholder('tender_contract')->set($resource_contract_uri);
                    $xsdp = $model->getNamespacePrefix("http://www.w3.org/2001/XMLSchema#");
                    //add :awardedTender property
                    $this->store->addStatement($model->getModelIri(),
                        $resource_contract_uri,
                        $this->pref["pc"].'awardedTender',
                        array('value' => $resource_uri, 'type'  => 'uri'),
                        true);
                    //add :awardDate property
                    $today = Date("Y-m-d");
                    $this->store->addStatement($model->getModelIri(),
                        $resource_contract_uri,
                        $this->pref["pc"].'awardDate',
                        array('value' => $today, 'type'  => 'literal', 'datatype' => $xsdp.":date"),
                        true);
                    
                    //CREATION OF CONTRACT AWARD NOTICE
                    $pinuri = $resource_contract_uri."/contract-award-notice/1";
                    $publisheruri = $userprefix.$username;
                    //select creator
                    $query_creator = 'SELECT ?creator WHERE {
                        <'.$resource_contract_uri.'> <'.$this->pref["dcterms"].'creator> ?creator. }';
                    $res = $model->sparqlQuery($query_creator);
                    if ($res == array())
                        $creatoruri = $publisheruri;
                    else
                        $creatoruri = $res[0]["creator"];
                    $publicationdate = Date("Y-m-d");
                    $fillingAppUri = $this->_privateConfig->fillingapp->uri;
                    //stmtArray for tender
                    $queryT[0] = 'SELECT ?s ?p ?o WHERE {
                        <'.$resource_uri.'> ?p ?o. }';
                    $options = array();
                    $options["default_subject"] = $resource_uri;
                    $stmtArray0 = ContractsHelper::constuctStmtArray($model,$queryT,$options); 
                    $stmtArray = array(
                        $resource_contract_uri => array( //add notice link
                            $this->pref["pc"]."notice" => array(array(
                                'type'  => 'uri',
                                'value' => $pinuri
                            )),
                            $this->pref["pc"].'awardedTender' => array(array(
                                'type'  => 'uri',
                                'value' => $resource_uri
                            )),
                        ),
                        $pinuri => array( //create notice
                            EF_RDF_TYPE => array(array(
                                'type'  => 'uri',
                                'value' => 'http://purl.org/procurement/public-contracts#ContractAwardNotice' 
                            ),
                            array(
                                'type'  => 'uri',
                                'value' => 'http://www.w3.org/ns/prov#Entity'
                            )),
                            'http://www.w3.org/ns/prov#wasAttributedTo' => array(array(
                                'type'  => 'uri',
                                'value' => $fillingAppUri
                            )),
                            'http://purl.org/dc/terms/creator' => array(array( //id toho kdo vytvoril kontrakt
                                'type'  => 'uri',
                                'value' => $creatoruri
                            )),
                            'http://purl.org/dc/terms/publisher' => array(array( //id toho kdo publikuje
                                'type'  => 'uri',
                                'value' => $publisheruri
                            )),
                            'http://purl.org/procurement/public-contracts#publicationDate' => array(array(
                                'type'  => 'literal', 'datatype' => $xsdp.':date',
                                'value' => $publicationdate
                            ))
                    ));
                    $stmtArray = array_merge($stmtArray,$stmtArray0);
                    //PUBLISH
                    $store->addMultipleStatements($publicmodel->getModelIri(), $stmtArray, false);
                    $this->view->placeholder('tender_awarded')->set(true);
                }
            }
        }
    }
    
    /***************************************************************************
     * 
     */
    public function rejecttenderAction()
    {
        //TODO: in next version
    }

    /***************************************************************************
     * The main action which is retrieved via ajax
     */
    public function exploreAction()
    {
        // save state to session
        $this->savestateServer($this->view, $this->setup);
        return;
    }

}
