<?php
/**
 * OntoWiki PC Filing App - Private data space plugin
 *  
 * @category Ontowiki PC Filing Application
 * @package  PCFilingApp
 */
class DataSpacePlugin extends OntoWiki_Plugin
{
    /**
     * Event called when new user is created.
     * Creates his personal model.     
     * TODO: pouze local (v ApplicationController) nebo i jiný??? (foaf+ssl snad v AuthController?)
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
        
        //add to contractors by default - not desired anymore
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
        $store->addStatement($configModel->getModelIri(),
            'http://ns.ontowiki.net/SysOnt/Anonymous',
            'http://ns.ontowiki.net/SysOnt/denyModelView',
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
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
    
    //TODO: move to different plugin
    public function onPropertiesActionData($event)
    {
        $modelobj = OntoWiki::getInstance()->selectedModel;
        $model = (string)$modelobj;
        
        /*try {
            $modelobj->addNamespacePrefix("pc",);
            $modelobj->addNamespacePrefix("dct","http://purl.org/dc/terms/");
            //$modelobj->addNamespacePrefix("dcterms","http://purl.org/dc/terms/");
        } catch (Exception $e) { }*/
        
        $isContract = false; $isBusiness = false;
        if (!isset($event->values[$model]['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']))
            return false; //no rdf:type
        foreach($event->values[$model]['http://www.w3.org/1999/02/22-rdf-syntax-ns#type'] as $value) {
            if ($value['uri'] === 'http://purl.org/procurement/public-contracts#Contract')
                $isContract = true;
            if ($value['uri'] === 'http://purl.org/goodrelations/v1#BusinessEntity')
                $isBusiness = true;
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
        //::: BUSINESS ENTITY ::::::::::::::::::::::::::::::::::::::::::::::::::
        if ($isBusiness) {
            $grp = $modelobj->getNamespacePrefix("http://purl.org/goodrelations/v1#");
            $brp = $modelobj->getNamespacePrefix("http://purl.org/business-register#");
            //legalName 
            $uri = 'http://purl.org/goodrelations/v1#legalName';
            if (!in_array($uri,$predicates)) {
                $short = $grp.':legalName';
                $content = "Default name";
                $datatype = 'xsd:string';
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
                $datatype = 'xsd:string';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //rename properties
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
            //title
            $uri = 'http://purl.org/dc/terms/title';
            if (!in_array($uri,$predicates)) {
                $short = $dctp.':title';
                $content = "Default title";
                $datatype = 'xsd:string';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //description
            $uri = 'http://purl.org/dc/terms/description';
            if (!in_array($uri,$predicates)) {
                $short = $dctp.':description';
                $content = "Default description";
                $datatype = 'xsd:string';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //startDate
            $uri = 'http://purl.org/procurement/public-contracts#startDate';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':startDate';
                $content = Date("Y-m-d");
                $datatype = 'xsd:date';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //endDate
            $uri = 'http://purl.org/procurement/public-contracts#endDate';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':endDate';
                $content = Date("Y-m-d");
                $datatype = 'xsd:date';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //awardDate
            $uri = 'http://purl.org/procurement/public-contracts#awardDate';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':awardDate';
                $content = Date("Y-m-d");
                $datatype = 'xsd:date';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //tenderDeadline
            $uri = 'http://purl.org/procurement/public-contracts#tenderDeadline';
            if (!in_array($uri,$predicates)) {
                $short = $pcp.':tenderDeadline';
                $content = Date("Y-m-d");
                $datatype = 'xsd:date';
                $ep[$model][$uri] = array( 'uri' => $uri , 'curi' => $short ,
                    'url' => 'http://ontowiki2.ranec.net/view/r/'.urlencode($short) ,
                    'title' => $short , 'has_more' => '');
                $ev[$model][$uri][] = array( 'content' => $content , 'object' => 'not set' ,
                    'object_hash' => md5(Erfurt_Utils::buildLiteralString($content,$datatype)) ,
                    'datatype' => $datatype ,
                    'lang' => '' , 'url' => '' , 'uri' => '' , 'curi' => '' );
            }
            //rename properties
            $uri = 'http://purl.org/dc/terms/title';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'contract name';
            $uri = 'http://purl.org/dc/terms/description';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'contract description';
            $uri = 'http://purl.org/procurement/public-contracts#startDate';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'start date of contract';
            $uri = 'http://purl.org/procurement/public-contracts#endDate';
            if (isset($ep[$model][$uri]))
                $ep[$model][$uri]['title'] = 'end date of contract';
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
            //results
            //print_r($ep);
            $event->predicates = $ep;
            $event->values = $ev;
            return true;
        }
    }
}