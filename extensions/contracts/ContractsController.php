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
    
    /*
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
    
    public function newbusinessentityAction()
    {
        $model = $this->_owApp->selectedModel;
        $translate = $this->_owApp->translate;
        $store = $this->_owApp->erfurt->getStore();
        
        $windowTitle = $translate->_('Create business entity');
        $this->view->placeholder('main.window.title')->set($windowTitle);
        
        if (isset($_POST["gr_legal_name"]))
        {
            $name = $_POST["gr_legal_name"];
            $countryname = $_POST["vcard_country_name"];
            $locality = $_POST["vcard_locality"];
            $postalcode = $_POST["vcard_postal_code"];
            $street = $_POST["vcard_street"];
            $reshexcode = dechex(rand(1,16777215));
            $reshexcodelong = (string)$reshexcode;
            while (strlen($reshexcodelong) < 6)
                $reshexcodelong = "0".$reshexcodelong;
            $resname = $model->getModelIri()."be_".$reshexcodelong;
            
            $bnodePrefix = '_:'.$reshexcodelong;
            $vcard = $bnodePrefix . '_vcard';
            $vcard_adr = $bnodePrefix . '_vcard_adr';
            $vcard_org = $bnodePrefix . '_vcard_org';
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
                    'http://purl.org/business-register#contact' => array(array(
                        'type'  => 'bnode',
                        'value' => $vcard
                    ))
                ),
                $vcard => array(
                    EF_RDF_TYPE => array(array(
                        'type'  => 'uri',
                        'value' => 'http://www.w3.org/2006/vcard/ns#VCard' 
                    )),
                    'http://www.w3.org/2006/vcard/ns#adr' => array(array(
                        'type'  => 'bnode',
                        'value' => $vcard_adr
                    )),
                    'http://www.w3.org/2006/vcard/ns#org' => array(array(
                        'type'  => 'bnode',
                        'value' => $vcard_org
                    ))
                ),
                $vcard_org => array(
                    EF_RDF_TYPE => array(array(
                        'type'  => 'uri',
                        'value' => 'http://www.w3.org/2006/vcard/ns#Organization' 
                    )),
                    'http://www.w3.org/2006/vcard/ns#organization-name' => array(array(
                        'type'  => 'bnode',
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
            
            $this->view->placeholder('added_resource')->set($resname);
            //$resource = $model->getResource($resname);
            //$this->_owApp->selectedResource = $resource;

        }
    }
    
    public function newcontractAction()
    {
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
        
        //TODO
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
