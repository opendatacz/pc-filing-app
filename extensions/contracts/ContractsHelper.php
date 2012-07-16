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
}

