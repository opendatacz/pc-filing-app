<?php
/**
 * OntoWiki PC Filing App - Contracts plugin
 * Handles: 
 *   private data spaces
 *   customized resource editation 
 *  
 * @category Ontowiki PC Filing Application
 * @package  PCFilingApp
 */
class ContractsPlugin extends OntoWiki_Plugin
{
    //TODO: pouze local (v ApplicationController) nebo i jiný??? (foaf+ssl snad v AuthController?)
    /**
     * Event called when new user is created.
     * Creates his personal model.
     *     
     * $event->username contains the username of newly created user.
     *
     * @param Erfurt_Event $event The event containing the username.
     */
    public function onUserRegistered($event)
    {
        $store = OntoWiki::getInstance()->erfurt->getStore();
        $username = $event->username;
        $useruri = 'http://localhost/OntoWiki/Config/'.$username; //je spolehlive? TODO: najit lepsi zpusob zjisteni uri uzivatele
        
        $personalModel = $store->getNewModel($this->_privateConfig->privatestore->prefix.$username.'/',
            '',
            Erfurt_Store::MODEL_TYPE_RDFS,
            false);
        $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
        
        //add to contractors by default
        //Not desired anymore
        /*$store->addStatement($configModel->getModelIri(),
            'http://localhost/OntoWiki/Config/Contractors',
            'http://rdfs.org/sioc/ns#has_member',
            array('value' => $useruri, 'type'  => 'uri'),
            false);*/
        //remove user from default user group
        $store->deleteMatchingStatements($configModel->getModelIri(),
            'http://localhost/OntoWiki/Config/DefaultUserGroup',
            'http://rdfs.org/sioc/ns#has_member',
            array('value' => $useruri, 'type'  => 'uri'),
            array('use_ac' => false));
        //deny access for anonymous
        //Anonymous has deny view for sysont:AnyModel from now on
        /*$store->addStatement($configModel->getModelIri(),
            'http://ns.ontowiki.net/SysOnt/Anonymous',
            'http://ns.ontowiki.net/SysOnt/denyModelView',
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);*/
        //deny access for default group
        $store->addStatement($configModel->getModelIri(),
            'http://localhost/OntoWiki/Config/DefaultUserGroup',
            'http://ns.ontowiki.net/SysOnt/denyModelView',
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
        //grant access for new user
        $store->addStatement($configModel->getModelIri(),
            $useruri,
            'http://ns.ontowiki.net/SysOnt/grantModelEdit',
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
        //add information about private store to user profile
        $predicate = $this->_privateConfig->contracts->configns.$this->_privateConfig->privatestore->predicate;
        $store->addStatement($configModel->getModelIri(),
            $useruri,
            $predicate,
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
        //add prefixes to privatestore model
        $personalModel->addNamespacePrefix("pc","http://purl.org/procurement/public-contracts#");
        $personalModel->addNamespacePrefix("dcterms","http://purl.org/dc/terms/");
        $personalModel->addNamespacePrefix("vcard","http://www.w3.org/2006/vcard/ns#");
        $personalModel->addNamespacePrefix("gr","http://purl.org/goodrelations/v1#");
        $personalModel->addNamespacePrefix("br","http://purl.org/business-register#");
        //add basic information to privatestore: label
        $store->addStatement($personalModel->getModelIri(),
            $personalModel->getModelIri(),
            'http://www.w3.org/2000/01/rdf-schema#label',
            array('value' => "Private data space of user '$username'", 'type'  => 'literal'),
            false);
    }
    
    public function onPropertiesAction($event)
    {
        /*$modelobj = OntoWiki::getInstance()->selectedModel;
        try {
            if(!$modelobj->getNamespaceByPrefix("pc"))
                $modelobj->addNamespacePrefix("pc","http://purl.org/procurement/public-contracts#");
            if(!$modelobj->getNamespaceByPrefix("dcterms"))
                $modelobj->addNamespacePrefix("dcterms","http://purl.org/dc/terms/");
        } catch (Exception $e) { echo $e->getMessage(); }*/
    }
    
    //TODO: decide whether to move to its own plugin
    /**
     * Event called when user is editing resource properties.
     * Adds recommended properties to editation. Renames properties label.     
     *     
     * $event->values contains the property values.
     * $event->predicates contains the property predicates
     *
     * @param Erfurt_Event $event The event containing values and predicates. (can be set)
     */
    public function onPropertiesActionData($event)
    {
        $modelobj = OntoWiki::getInstance()->selectedModel;
        $model = (string)$modelobj;
        
        /*try {
            $modelobj->addNamespacePrefix("pc",);
            $modelobj->addNamespacePrefix("dct","http://purl.org/dc/terms/");
            //$modelobj->addNamespacePrefix("dcterms","http://purl.org/dc/terms/");
        } catch (Exception $e) { }*/
        
        $isContract = $isBusiness = $isPrice = $isContact = $isAdr = $isOrg = false; 
        if (!isset($event->values[$model]['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']))
            return false; //no rdf:type
        foreach($event->values[$model]['http://www.w3.org/1999/02/22-rdf-syntax-ns#type'] as $value) {
            if ($value['uri'] === 'http://purl.org/procurement/public-contracts#Contract')
                $isContract = true;
            if ($value['uri'] === 'http://purl.org/goodrelations/v1#BusinessEntity')
                $isBusiness = true;
            if ($value['uri'] === 'http://purl.org/goodrelations/v1#PriceSpecification')
                $isPrice = true;
            if ($value['uri'] === 'http://www.w3.org/2006/vcard/ns#VCard')
                $isContact = true;
            if ($value['uri'] === 'http://www.w3.org/2006/vcard/ns#Address')
                $isAdr = true;
            if ($value['uri'] === 'http://www.w3.org/2006/vcard/ns#Organization')
                $isOrg = true;
        }
        $ep = $event->predicates;
        //print_r($ep);
        $ev = $event->values;
        $predicates = array();
        foreach ($ep[$model] as $predicate)
            $predicates[] = $predicate['uri'];
        //unset type (no need to change type)
         //unset($ep[$model]['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']);
         //unset($ev[$model]['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']);
        //::: VCARD ADDRESS ::::::::::::::::::::::::::::::::::::::::::::::::::::
        if ($isContact) {
            //rename properties
            $uri = 'http://www.w3.org/2006/vcard/ns#adr';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'address';
            $uri = 'http://www.w3.org/2006/vcard/ns#org';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'organization';
            //results
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
        //::: VCARD ADDRESS ::::::::::::::::::::::::::::::::::::::::::::::::::::
        if ($isAdr) {
            $vcardp = $modelobj->getNamespacePrefix("http://www.w3.org/2006/vcard/ns#");
            $xsdp = $modelobj->getNamespacePrefix("http://www.w3.org/2001/XMLSchema#");
            //country-name
            $uri = 'http://www.w3.org/2006/vcard/ns#country-name';
            if (!in_array($uri,$predicates)) {
                $short = $vcardp.':country-name';
                $content = "no name";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //locality
            $uri = 'http://www.w3.org/2006/vcard/ns#locality';
            if (!in_array($uri,$predicates)) {
                $short = $vcardp.':locality';
                $content = "no locality";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //postal-code
            $uri = 'http://www.w3.org/2006/vcard/ns#postal-code';
            if (!in_array($uri,$predicates)) {
                $short = $vcardp.':postal-code';
                $content = "no postal code";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //street
            $uri = 'http://www.w3.org/2006/vcard/ns#street';
            if (!in_array($uri,$predicates)) {
                $short = $vcardp.':street';
                $content = "no street";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //rename properties
            $uri = 'http://www.w3.org/2006/vcard/ns#country-name';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'country';
            $uri = 'http://www.w3.org/2006/vcard/ns#locality';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'locality (e.g., city)';
            $uri = 'http://www.w3.org/2006/vcard/ns#postal-code';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'postal code';
            $uri = 'http://www.w3.org/2006/vcard/ns#street';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'street';
            //results
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
        //::: VCARD ORGANIZATION :::::::::::::::::::::::::::::::::::::::::::::::
        if ($isOrg) {
            $vcardp = $modelobj->getNamespacePrefix("http://www.w3.org/2006/vcard/ns#");
            $xsdp = $modelobj->getNamespacePrefix("http://www.w3.org/2001/XMLSchema#");
            //organization-name
            $uri = 'http://www.w3.org/2006/vcard/ns#organization-name';
            if (!in_array($uri,$predicates)) {
                $short = $vcardp.':organization-name';
                $content = "no organization";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //rename properties
            $uri = 'http://www.w3.org/2006/vcard/ns#organization-name';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'organization name';
            //results
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
        //::: PRICE SPECIFICATION ::::::::::::::::::::::::::::::::::::::::::::::
        if ($isPrice) {
            $grp = $modelobj->getNamespacePrefix("http://purl.org/goodrelations/v1#");
            $xsdp = $modelobj->getNamespacePrefix("http://www.w3.org/2001/XMLSchema#");
            //hasCurrency
            $uri = 'http://purl.org/goodrelations/v1#hasCurrency';
            if (!in_array($uri,$predicates)) {
                $short = $grp.':hasCurrency';
                $content = "no currency";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //hasCurrencyValue
            $uri = 'http://purl.org/goodrelations/v1#hasCurrencyValue';
            if (!in_array($uri,$predicates)) {
                $short = $grp.':hasCurrencyValue';
                $content = "0";
                $datatype = "$xsdp:float";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //rename properties
            $uri = 'http://purl.org/goodrelations/v1#hasCurrency';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'currency';
            $uri = 'http://purl.org/goodrelations/v1#hasCurrencyValue';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'value';
            //results
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
        //::: BUSINESS ENTITY ::::::::::::::::::::::::::::::::::::::::::::::::::
        if ($isBusiness) {
            $grp = $modelobj->getNamespacePrefix("http://purl.org/goodrelations/v1#");
            $brp = $modelobj->getNamespacePrefix("http://purl.org/business-register#");
            $xsdp = $modelobj->getNamespacePrefix("http://www.w3.org/2001/XMLSchema#");
            //legalName 
            $uri = 'http://purl.org/goodrelations/v1#legalName';
            if (!in_array($uri,$predicates)) {
                $short = $grp.':legalName';
                $content = "Default name";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //officialNumber
            $uri = 'http://purl.org/business-register#officialNumber';
            if (!in_array($uri,$predicates)) {
                $short = $brp.':legalName';
                $content = "Not set";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //rename properties
            $uri = 'http://purl.org/business-register#contact';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'organization contact';
            $uri = 'http://purl.org/goodrelations/v1#legalName';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'legal name';
            $uri = 'http://purl.org/business-register#officialNumber';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'official identification number';
            //results
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
        //::: PUBLIC CONTRACT ::::::::::::::::::::::::::::::::::::::::::::::::::
        if ($isContract) {
            $pcp = $modelobj->getNamespacePrefix("http://purl.org/procurement/public-contracts#");
            $dctp = $modelobj->getNamespacePrefix("http://purl.org/dc/terms/");
            $xsdp = $modelobj->getNamespacePrefix("http://www.w3.org/2001/XMLSchema#");
            //title
            $uri = 'http://purl.org/dc/terms/title';
            if (!in_array($uri,$predicates)) {
                $short = $dctp.':title';
                $content = "Default title";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set (required for prior information notice)' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //description
            $uri = 'http://purl.org/dc/terms/description';
            if (!in_array($uri,$predicates)) {
                $short = $dctp.':description';
                $content = "Default description";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set (required for prior information notice)' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //startDate
            $uri = 'http://purl.org/procurement/public-contracts#startDate';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':startDate';
                $content = Date("Y-m-d");
                $datatype = "$xsdp:date";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //estimatedEndDate
            $uri = 'http://purl.org/procurement/public-contracts#estimatedEndDate';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':estimatedEndDate';
                $content = Date("Y-m-d");
                $datatype = "$xsdp:date";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //awardDate
            /*$uri = 'http://purl.org/procurement/public-contracts#awardDate';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':awardDate';
                $content = Date("Y-m-d");
                $datatype = "$xsdp:date";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }*/
            //tenderDeadline
            $uri = 'http://purl.org/procurement/public-contracts#tenderDeadline';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':tenderDeadline';
                $content = Date("Y-m-d");
                $datatype = "$xsdp:date";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set (required for contract notice)' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //referenceNumber
            $uri = 'http://purl.org/procurement/public-contracts#referenceNumber';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':referenceNumber';
                $content = "Empty";
                $datatype = "$xsdp:string";
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set (required for prior information notice)' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //mainObject
            //location
            
            //rename properties
            $uri = 'http://purl.org/procurement/public-contracts#contact';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'main contact';
            $uri = 'http://purl.org/dc/terms/title';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'contract name';
            $uri = 'http://purl.org/dc/terms/description';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'contract description';
            $uri = 'http://purl.org/procurement/public-contracts#startDate';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'start date of contract';
            $uri = 'http://purl.org/procurement/public-contracts#estimatedEndDate';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'estimated end date of contract';
            $uri = 'http://purl.org/procurement/public-contracts#awardDate';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'date of contract award';
            $uri = 'http://purl.org/procurement/public-contracts#tenderDeadline';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'time-limit for receipt of tenders';
            $uri = 'http://purl.org/procurement/public-contracts#contractingAuthority';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'contracting authority';
            $uri = 'http://purl.org/procurement/public-contracts#numberOfTenders';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'number of tenders received';
            $uri = 'http://purl.org/procurement/public-contracts#mainObject';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'main object of contract'; 
            $uri = 'http://purl.org/procurement/public-contracts#referenceNumber';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'file reference number';
            $uri = 'http://purl.org/dc/terms/creator';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'creator';
            $uri = 'http://purl.org/procurement/public-contracts#tender';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'Tender';
            $uri = 'http://purl.org/procurement/public-contracts#awardedTender';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'Awarded tender';
            $uri = 'http://purl.org/procurement/public-contracts#estimatedPrice';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'Exact estimated price';
            $uri = 'http://purl.org/procurement/public-contracts#estimatedPriceLower';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'Lower range of estimated price';
            $uri = 'http://purl.org/procurement/public-contracts#estimatedPriceUpper';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'Upper range of estimated price';
            //results
            //print_r($ep);
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
        //TODO: all rename properties pull from ontology graph
        //onotology graph would have to contain at least rdf,pc,dct,gr,br ontology
    }
}