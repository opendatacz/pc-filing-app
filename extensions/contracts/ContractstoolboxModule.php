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
        
        /*$pcMenu = new OntoWiki_Menu();
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
        $mainMenu->setEntry('Tender', $tMenu);*/

        return $mainMenu;
    }
    
    /**
     * Returns the content
     */
    public function getContents() {
        /*$app = OntoWiki::getInstance();
        //clear cache*/
        //$cache->invalidateWithModelIri($configModel->getModelIri());
        
    
        //register controller
        /*$url = new OntoWiki_Url(array('controller' => 'contracts', 'action' => 'publishbusiness'), array());
        $this->view->actionUrl = (string)$url;*/
        // scripts and css only if module is visible
        $this->view->headScript()->appendFile($this->view->moduleUrl . 'contracts.js');
        $this->view->headLink()->appendStylesheet($this->view->moduleUrl . 'contracts.css');
        
        $sessionKey = 'PcfaContracts' . (isset($config->session->identifier) ? $config->session->identifier : '');        
        $stateSession = new Zend_Session_Namespace($sessionKey);

        $data = array();
        if (isset($this->_owApp->selectedModel))
            $data["onmodel"] = true;
        else
            $data["onmodel"] = false;
        
        $content = $this->render('contracts/module-main', $data, 'data'); // 
        return $content;
    }
	
    public function shouldShow(){
        if (!($this->_owApp->getUser()->isAnonymousUser())) //&& isset($this->_owApp->selectedModel)
            return true;
        else
            return false;
    }

}


