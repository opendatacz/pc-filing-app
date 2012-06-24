<?php
/**
 * OntoWiki PC Filing App - Private data space plugin
 *  
 * @category Ontowiki PC Filing Application
 * @package  PCFilingApp
 */
class PCFADataSpacePlugin extends OntoWiki_Plugin
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
        
        $personalModel = $store->getNewModel($this->_privateConfig->privatestore.$username.'/');
        $configModel = $store->getModel('http://localhost/OntoWiki/Config/');
        
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
            'http://localhost/OntoWiki/Config/'.$username, //TODO: najit lepsi zpusob zjisteni uri uzivatele
            'http://ns.ontowiki.net/SysOnt/grantModelEdit',
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
    }
}