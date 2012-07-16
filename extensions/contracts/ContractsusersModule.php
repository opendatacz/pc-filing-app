<?php

class ContractsusersModule extends OntoWiki_Module
{
    protected $session = null;

    public function init() {
        $this->session = $this->_owApp->session;
    }

    public function getTitle() {
        return "Public Contract User Type";
    }

    /**
     * Returns the menu of the module
     * @return OntoWiki_Menu
     */
    public function getMenu() {
        return new OntoWiki_Menu();
    }
    
    /**
     * Returns the content
     */
    public function getContents() {
        //register controller
        $url = new OntoWiki_Url(array('controller' => 'contracts', 'action' => 'setusertype'), array());
        $this->view->actionUrl = (string)$url;
    
        // scripts and css only if module is visible
        //$this->view->headScript()->appendFile($this->view->moduleUrl . 'contracts.js');
        //$this->view->headLink()->appendStylesheet($this->view->moduleUrl . 'contracts.css');
        
        $sessionKey = 'PcfaContracts' . (isset($config->session->identifier) ? $config->session->identifier : '');        
        $stateSession = new Zend_Session_Namespace($sessionKey);

        $data = array();
        $data["url"] = (string)$url;
        $data["ow"] = $this->_owApp;
        $data["store"] = $this->_owApp->erfurt->getStore();
        $data["user"] = $this->_owApp->getUser();
        $data["username"] = $data["user"]->getUsername();
        $data["useruri"] = $data["user"]->getUri();//$this->_erfurt->getAuth()->getIdentity()->getUri();
        $data["ac"] = $this->_erfurt->getAc();
        $data["contractor"] = false;
        $data["supplier"] = false;
        
        $configModel = $data["store"]->getModel('http://localhost/OntoWiki/Config/',false);
        
        $resContractors = $configModel->getResource('http://localhost/OntoWiki/Config/Contractors');
        require_once 'Erfurt/Syntax/RdfSerializer.php';
        $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
        $serialized = $serializer->serializeResourceToString($resContractors->getIri(), $configModel->getModelIri(), false, false);
        $domdoc = new DOMDocument();
        @$domdoc->LoadXml($serialized);
        $xpath = new DOMXPath($domdoc);
        $members = $xpath->query("//sioc:has_member/@rdf:resource");
        for ($i=0;$i<$members->length;$i++)
            if ($members->item($i)->nodeValue == $data["username"])
                $data["contractor"] = true;
        
        $resSuppliers = $configModel->getResource('http://localhost/OntoWiki/Config/Suppliers');
        $serializer = Erfurt_Syntax_RdfSerializer::rdfSerializerWithFormat('rdfxml');
        $serialized = $serializer->serializeResourceToString($resSuppliers->getIri(), $configModel->getModelIri(), false, false);
        $domdoc = new DOMDocument();
        @$domdoc->LoadXml($serialized);
        $xpath = new DOMXPath($domdoc);
        $members = $xpath->query("//sioc:has_member/@rdf:resource");
        for ($i=0;$i<$members->length;$i++)
            if ($members->item($i)->nodeValue == $data["username"])
                $data["supplier"] = true;
        
        //if ($data["supplier"]) echo "sup";
        //if ($data["contractor"]) echo "con";
        
        $content = $this->render('contracts/usertype-modul', $data, 'data');
        return $content;
    }
	
    public function shouldShow(){
        return true;
    }
    
    private function getMembersFromSerialized($serialized)
    {
    }

}


