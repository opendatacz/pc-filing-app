<?php

class ContractstoolboxModule extends OntoWiki_Module
{
    protected $session = null;

    public function init() {
        $this->session = $this->_owApp->session;
    }

    public function getTitle() {
        return "Public Contract Filing App";
    }

    /**
     * Returns the menu of the module
     * @return string
     */
    public function getMenu() {
		// check if menu must be shown
		if(!$this->_privateConfig->defaults->showMenu) return new OntoWiki_Menu();
		
        // build main menu (out of sub menus below)
        $mainMenu = new OntoWiki_Menu();
        
        $pcMenu = new OntoWiki_Menu();
        $pcMenu->setEntry('Publish prior information notice', "javascript:publishPriorInformationNotice()");
        $pcMenu->setEntry('Publish contract notice', "javascript:publishContractNotice()");
        $pcMenu->setEntry('Create tender', "javascript:createTender()");
        $pcMenu->setEntry('Cancel contract', "javascript:cancelContract()");
        $mainMenu->setEntry('Public contract', $pcMenu);
        $beMenu = new OntoWiki_Menu();
        $beMenu->setEntry('Publish business entity', "javascript:publishBusinessEntity()");
        $mainMenu->setEntry('Business entity', $beMenu);
        $tMenu = new OntoWiki_Menu();
        $tMenu->setEntry('Award tender', "javascript:awardTender()");
        $tMenu->setEntry('Reject tender', "javascript:rejectTender()");
        $mainMenu->setEntry('Tender', $tMenu);

        return $mainMenu;
    }
    
    /**
     * Returns the content
     */
    public function getContents() {
        //register controller
        $url = new OntoWiki_Url(array('controller' => 'contracts', 'action' => 'publishbusiness'), array());
        $this->view->actionUrl = (string)$url;
    
        // scripts and css only if module is visible
        $this->view->headScript()->appendFile($this->view->moduleUrl . 'contracts.js');
        $this->view->headLink()->appendStylesheet($this->view->moduleUrl . 'contracts.css');
        
        $sessionKey = 'PcfaContracts' . (isset($config->session->identifier) ? $config->session->identifier : '');        
        $stateSession = new Zend_Session_Namespace($sessionKey);

        $data = array();
        $subdata = array();
        $subdata["ow"] = $this->_owApp;
        $subdata["mod"] = $this->_owApp->selectedModel;
        $subdata["store"] = $this->_owApp->erfurt->getStore();
        if (isset($this->_owApp->selectedResource) && $this->_owApp->selectedResource->__toString() !== $this->_owApp->selectedModel->__toString()) {
            $subdata["res"] = $this->_owApp->selectedResource;
            $model = new OntoWiki_Model_Resource($subdata["store"], $subdata["mod"], (string)$subdata["res"]);
            $values = $model->getValues();
            $isType = false;
            if (isset($values[(string)$subdata["mod"]]["http://www.w3.org/1999/02/22-rdf-syntax-ns#type"]))
                $types = $values[(string)$subdata["mod"]]["http://www.w3.org/1999/02/22-rdf-syntax-ns#type"];
            else
                $types = array();
            foreach ($types as $type) {
                if ($type["uri"] === "http://purl.org/procurement/public-contracts#Contract") {
                    $isType = "contract";
                    break;
                }
            }
            if ($isType === false) {
                foreach ($types as $type) {
                    if ($type["uri"] === "http://purl.org/goodrelations/v1#BusinessEntity") {
                        $isType = "business";
                        break;
                    }
                }
            }
            if ($isType === "contract")
                $data["content"] = $this->render("pcfacontracts/pcfa-res-contract", $subdata, 'data');
            else if ($isType === "business")
                $data["content"] = $this->render("pcfacontracts/pcfa-res-business", $subdata, 'data');
            else
                $data["content"] = $this->render("pcfacontracts/pcfa-model", $subdata, 'data');
        } else {
            $data["content"] = $this->render("pcfacontracts/pcfa-model", $subdata, 'data');
        }
        $data["ow"] = $this->_owApp;
        $content = $this->render('pcfacontracts_content', $data, 'data'); // 
        return $content;
    }
	
    public function shouldShow(){
        if (isset($this->_owApp->selectedModel)) {
            return true;
        } else {
            return false;
        }
    }

}


