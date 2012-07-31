<?php

class ContractsHelper extends OntoWiki_Component_Helper
{
    public function init()
    {
        // get the main application
        $owApp = OntoWiki::getInstance();

        // get current route info
        $front  = Zend_Controller_Front::getInstance();
        $router = $front->getRouter();

        /*OntoWiki_Navigation::register('contracts', array(
            'controller' => 'contracts',
            'action'     => 'list',
            'name'       => 'Contracts',
            'priority'   => 30));*/
    }
    
    private $ns = array(
        'br' => 'http://purl.org/business-register#',
        'gr' => 'http://purl.org/goodrelations/v1#',
        'vcard' => 'http://www.w3.org/2006/vcard/ns#'
    );
    
    public static function getPropertiesFromForm($addNs = array())
    {
        $results = array();
        $ns = array_merge($this->ns,$addNs);
        foreach($_POST as $candidate => $value) {
            $candidateparts = explode('_',$candidate,3);
            if ($candidateparts[0] !== 'prop')
                continue;
            $results[] = array('prefix' => $candidateparts[1],
                'namespace' => $ns[$candidateparts[1]],
                'property' => $candidateparts[2],
                'value' => $value);
        }
        return $results;
    }
    
    /**
     * return array with keys "contractor" and "supplier" containing true/false
     */         
    public static function getContractUseroups()
    {
        $app = OntoWiki::getInstance();
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
    
    public static function getUserDataSpace()
    {
        $app = OntoWiki::getInstance();
        $store = $app->erfurt->getStore();
        $user = $app->getUser();
        $username = $user->getUsername();
        $useruri = 'http://localhost/OntoWiki/Config/'.$username; //TODO: check je to skutecne vzdy pravda?
        $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
        $config2ns = "http://localhost/OntoWiki/Config2/";
        $pspredicate = "PrivateStore";
        //$predicate = $this2->_privateConfig->ownbusiness->predicate;
        //clear cache
        $cache = $app->erfurt->getQueryCache(); //$cache = Erfurt_App::getInstance()->getQueryCache();
        $cache->invalidateWithModelIri($configModel->getModelIri());
        //add ns
        $config2prefix = $configModel->getNamespacePrefix($config2ns);
        //load contractor
        $resUser = $configModel->getResource($useruri);
        require_once 'Erfurt/Syntax/RdfSerializer.php';
        $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
        $serialized = $serializer->serializeResourceToString($resUser->getIri(), $configModel->getModelIri(), false, false);
        $domdoc = new DOMDocument();
        @$domdoc->LoadXml($serialized);
        $xpath = new DOMXPath($domdoc);
        $members = $xpath->query("//$config2prefix:$pspredicate/@rdf:resource"); //TODO: parametry
        $res = array();
        for ($i=0;$i<$members->length;$i++)
            $res[] = $members->item($i)->nodeValue;
        return $res;
    }
    
    public static function getUserBusiness()
    {
        $app = OntoWiki::getInstance();
        $store = $app->erfurt->getStore();
        $user = $app->getUser();
        $username = $user->getUsername();
        $useruri = 'http://localhost/OntoWiki/Config/'.$username; //TODO: check je to skutecne vzdy pravda?
        $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
        $config2ns = "http://localhost/OntoWiki/Config2/";
        $bspredicate = "BusinessSelf";
        //clear cache
        $cache = $app->erfurt->getQueryCache(); //$cache = Erfurt_App::getInstance()->getQueryCache();
        $cache->invalidateWithModelIri($configModel->getModelIri());
        //add ns
        $config2prefix = $configModel->getNamespacePrefix($config2ns);
        //load contractor
        $resUser = $configModel->getResource($useruri);
        require_once 'Erfurt/Syntax/RdfSerializer.php';
        $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
        $serialized = $serializer->serializeResourceToString($resUser->getIri(), $configModel->getModelIri(), false, false);
        $domdoc = new DOMDocument();
        @$domdoc->LoadXml($serialized);
        $xpath = new DOMXPath($domdoc);
        $members = $xpath->query("//$config2prefix:$bspredicate/@rdf:resource"); //TODO: parametry
        $res = array();
        for ($i=0;$i<$members->length;$i++)
            $res[] = $members->item($i)->nodeValue;
        if ($res === array())
            return false;
        return $res;
    }
}

