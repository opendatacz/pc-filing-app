<?php

class ContractscontracttoolboxModule extends OntoWiki_Module
{
    protected $session = null;

    public function init()
    {
        $this->session = $this->_owApp->session;
    }

    public function getTitle()
    {
        return "Public Contract App: Public Contract";
    }

    /**
     * Returns the menu of the module
     * @return OntoWiki_Menu
     */
    public function getMenu()
    {
        return new OntoWiki_Menu();
    }
    
    /**
     * Returns the content
     */
    public function getContents()
    {
        //register controller
        /*$url = new OntoWiki_Url(array('controller' => 'contracts', 'action' => 'publishbusiness'), array());
        //$this->view->actionUrl = (string)$url;*/
        
        $sessionKey = 'PcfaContracts' . (isset($config->session->identifier) ? $config->session->identifier : '');        
        $stateSession = new Zend_Session_Namespace($sessionKey);
        
        $data = array();
        $data["ow"] = $this->_owApp;
        $data["res"] = $this->_owApp->selectedResource;
        
        $content = $this->render('contracts/module-contract', $data, 'data'); // 
        return $content;
    }
    
    public function shouldShow()
    {
        $ow = $this->_owApp;
        $graph = $this->_owApp->selectedModel;
        $store = $this->_owApp->erfurt->getStore();
        $resource = $this->_owApp->selectedResource;
    
        if (!($this->_owApp->getUser()->isAnonymousUser())) {
            $modelresource = new OntoWiki_Model_Resource($store, $graph, (string)$resource);
            $values = $modelresource->getValues();
            if (isset($values[(string)$graph]["http://www.w3.org/1999/02/22-rdf-syntax-ns#type"]))
                $types = $values[(string)$graph]["http://www.w3.org/1999/02/22-rdf-syntax-ns#type"];
            else
                $types = array();
            foreach ($types as $type) {
                if ($type["uri"] === "http://purl.org/procurement/public-contracts#Contract") {
                    return true;
                }
            }
        } else
            return false;
    }
    
    private function getMembersFromSerialized($serialized)
    {
    }

}