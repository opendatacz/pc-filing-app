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
        
        $usergroups = ContractsHelper::getContractUseroups();
                
        if (isset($_POST["prop_gr_legalName"]) && $usergroups["any"]) //TODO: kontrola vsech atributu??
        {
            if (!$model->isEditable()) {
                $this->view->placeholder('added_resource')->set(false);
                return false;
            }
            $name = $_POST["prop_gr_legalName"];
            $tid = $_POST["prop_br_officialNumber"];
            $countryname = $_POST["prop_vcard_country-name"];
            $locality = $_POST["prop_vcard_locality"];
            $postalcode = $_POST["prop_vcard_postal-code"];
            $street = $_POST["prop_vcard_street"];
            if (strlen(trim($tid)) > 4) { //TODO: jake univerzalni pravidlo pro platny kod?
                $tid = trim($tid);
                $tid = strtolower($tid);
                str_replace(' ','-',$tid); //TODO: bezpecnejsi prevod na URL slug
                $resuriend = $countryname.$tid;
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
                    'http://purl.org/dc/terms/creator' => array(array( //id toho kdo vytvoril kontrakt
                        'type'  => 'uri',
                        'value' => $usruri
                    )),
                    'http://purl.org/business-register#contact' => array(array(
                        'type'  => 'uri', //bnode
                        'value' => $vcard
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
                echo "iri:",$configModel->getModelIri()," sub:",$useruri," pre:",$predicate," obj:",$resname;
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
            $business = $businesses[0];
            try {
                $this->store->addStatement($model->getModelIri(),
                    $resname,
                    EF_RDF_TYPE,
                    array('value' => 'http://purl.org/procurement/public-contracts#Contract', 'type'  => 'uri'),
                    true);
                $this->store->addStatement($model->getModelIri(),
                    $resname,
                    'http://purl.org/dc/terms/title',
                    array('value' => 'new public contract', 'type'  => 'literal', 'datatype' => 'xsd:string'),
                    true);
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
                $this->view->placeholder('added_resource')->set($resname);
            } catch (Exception $e) {
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
        
            
        }
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
        //$rUriEncoded = urlencode((string)$resource);
        //$mUriEncoded = urlencode((string)$model);
        
        $windowTitle = sprintf($translate->_('Publish business entity %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
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
        //window title
        $windowTitle = sprintf($translate->_('Publish prior information notice for contract %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
        //CHECK EXISTENCE IN PUBLIC STORE
        //try {
        $res = $publicmodel->sparqlQuery('SELECT ?x
            WHERE {
                <'.$resourceuri.'> <'.$this->pref["pc"].'notice> ?x .
                ?x a <'.$this->pref["pc"].'PriorInformationNotice> .
                }');
        //} catch (Exception $e) { echo $e->getMessage(); }
        if ($res !== array()) {
            //resource already exists
            $this->view->placeholder('already_published')->set(true);
            return; //can not republish
        }
        //CHECK REQUIRED PROPERTIES
        $required = array();
        $required[] = EF_RDF_TYPE;
        $required[] = $this->pref["dcterms"]."title";
        $required[] = $this->pref["dcterms"]."description";
        $required[] = $this->pref["pc"]."contact";
        $required[] = $this->pref["pc"]."contractingAuthority";
        //$required[] = $this->pref["pc"]."estimatedPrice"; //checked further on
        //$required[] = $this->pref["pc"]."estimatedPriceLower";
        //$required[] = $this->pref["pc"]."estimatedPriceUpper";
        $required[] = $this->pref["pc"]."kind"; //???
        $required[] = $this->pref["pc"]."mainObject"; //???
        //$required[] = $this->pref["pc"]."notice";
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
        $res = $model->sparqlQuery($query_prices);
        $ep = $epl = $epu = $price_problem = false;
        foreach ($res as $r) {
            if ($r["p"] == $this->pref["pc"].'estimatedPrice') $ep = true;
            if ($r["p"] == $this->pref["pc"].'estimatedPriceLower') $epl = true;
            if ($r["p"] == $this->pref["pc"].'estimatedPriceUpper') $epu = true;
        }
        if ($ep == ($epl && $epu)) //both missing or both set
            $price_problem = true;
        $this->view->placeholder('price_problem')->set($price_problem);
        //CAN NOT PUBLISH (ERRORS)
        if (!(($missing == array()) && ($ep xor ($epl && $epu))))
            return;
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
            <'.$resourceuri.'> <'.$this->pref["pc"].'mainObject> ?s.
            ?s ?p ?o. }';
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
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."kind";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."location";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."mainObject";
        //$options["construct_only"][$resourceuri][] = $this->pref["pc"]."notice";
        $options["construct_only"][$resourceuri][] = $this->pref["pc"]."referenceNumber";
        $stmtArray1 = ContractsHelper::constuctStmtArray($model,$query,$options);        
        //CREATION OF PRIOR INFORMATION NOTICE
        $pinuri = $resourceuri."/prior-information-notice/1";
        $publisheruri = $userprefix.$username;
        //creator
        $query_creator = 'SELECT ?creator WHERE {
            <'.$resourceuri.'> <'.$this->pref["dcterms"].'creator> ?creator. }';
        $res = $model->sparqlQuery($query_creator);
        if ($res == array())
            $creatoruri = $publisheruri;
        else
            $creatoruri = $res[0]["creator"];
        $publicationdate = Date("Y-m-d");
        $stmtArray2 = array(
            $pinuri => array(
                EF_RDF_TYPE => array(array(
                    'type'  => 'uri',
                    'value' => 'http://purl.org/procurement/public-contracts#PriorInformationNotice' 
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
                    'type'  => 'literal', //bnode
                    'value' => $publicationdate
                ))
        ));
        $stmtArray = array_merge($stmtArray1,$stmtArray2);
        print_r($stmtArray);
        //PUBLISH
        //$store->addMultipleStatements($publicmodel->getModelIri(), $stmtArray, false);
        //$this->view->placeholder('published_priornotice')->set(true);
    }
    
    /***************************************************************************
     * 
     */
    public function publishnoticeAction()
    {
        //echo "<p>TODO</p>";
        //TODO
    }
    
    /***************************************************************************
     * 
     */
    public function cancelcontractAction()
    {
        //TODO
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
        
        $usergroups = ContractsHelper::getContractUseroups();
        $businesses = ContractsHelper::getUserBusiness();
        //TODO: check which notices were published ????
        if ($usergroups["contractor"] && ($businesses !== false)) {
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
                        //+supplier?
                    ),
                    $priceuri => array(
                        EF_RDF_TYPE => array(array(
                            'type'  => 'uri',
                            'value' => $this->pref["gr"].'PriceSpecification' 
                        )),
                ));
                $store->addMultipleStatements($model->getModelIri(), $stmtArray, false);
                $this->view->placeholder('added_resource')->set($resname);
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
        $translate = $this->_owApp->translate;
        $store = $this->_owApp->erfurt->getStore();
        $user = $this->_owApp->getUser();
        $resource = $this->_owApp->selectedResource;
        $resource_uri = (string)$resource;
        
        $windowTitle = $translate->_('Award tender');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        $usergroups = ContractsHelper::getContractUseroups();
        $businesses = ContractsHelper::getUserBusiness();
        //TODO: check which notices were published ????
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
                }
                else {
                    $this->view->placeholder('contract_already_awarded')->set(false);
                    $this->view->placeholder('tender_not_linked')->set(false);
                    $resource_contract_uri = $res[0]["x"];
                    $this->view->placeholder('tender_contract')->set($resource_contract_uri);
                    //add :awardedTender property
                    $this->store->addStatement($model->getModelIri(),
                        $resource_contract_uri,
                        $this->pref["pc"].'awardedTender',
                        array('value' => $resource_uri, 'type'  => 'uri'),
                        true);
                    //TODO: publish award tender notice ??!!
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
        //TODO
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
