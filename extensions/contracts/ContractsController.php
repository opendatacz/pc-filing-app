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

    /*
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
    
    public function setusertypeAction()
    {
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

    public function publishbusinessAction()
    {
        $model = $this->_owApp->selectedModel;
        $resource = $this->_owApp->selectedResource;
        $title = $resource->getTitle();
        $translate   = $this->_owApp->translate;
        //$rUriEncoded = urlencode((string)$resource);
        //$mUriEncoded = urlencode((string)$model);
        
        $windowTitle = sprintf($translate->_('Publish business entity %1$s'), $title);
        $this->view->placeholder('main.window.title')->set($windowTitle);
    }
    
    public function publishpriornoticeAction()
    {
        echo "<p>resolves</p>";
    }
    
    public function publishnoticeAction()
    {
        echo "<p>resolves</p>";
    }

    /*
     * The main action which is retrieved via ajax
     */
    public function exploreAction()
    {


        // save state to session
        $this->savestateServer($this->view, $this->setup);

        return;
    }

}
