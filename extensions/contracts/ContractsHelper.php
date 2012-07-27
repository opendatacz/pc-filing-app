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
}

