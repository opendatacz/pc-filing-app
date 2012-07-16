<?php
/**
 * OntoWiki PC Filing App - Private data space plugin
 *  
 * @category Ontowiki PC Filing Application
 * @package  PCFilingApp
 */
class DataSpacePlugin extends OntoWiki_Plugin
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
        $useruri = 'http://localhost/OntoWiki/Config/'.$username; //je spolehlive? TODO: najit lepsi zpusob zjisteni uri uzivatele
        
        $personalModel = $store->getNewModel($this->_privateConfig->privatestore->prefix.$username.'/',
            '',
            Erfurt_Store::MODEL_TYPE_RDFS,
            false);
        $configModel = $store->getModel('http://localhost/OntoWiki/Config/',false);
        
        //add user to Contractors user group and remove from default user group
        $store->addStatement($configModel->getModelIri(),
            'http://localhost/OntoWiki/Config/Contractors',
            'http://rdfs.org/sioc/ns#has_member',
            array('value' => $useruri, 'type'  => 'uri'),
            false);
        $store->deleteMatchingStatements($configModel->getModelIri(),
            'http://localhost/OntoWiki/Config/DefaultUserGroup',
            'http://rdfs.org/sioc/ns#has_member',
            array('value' => $useruri, 'type'  => 'uri'),
            array('use_ac' => false));
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
            $useruri,
            'http://ns.ontowiki.net/SysOnt/grantModelEdit',
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
        //add information about private store to user profile
        $store->addStatement($configModel->getModelIri(),
            $useruri,
            $this->_privateConfig->privatestore->predicate,
            array('value' => $personalModel->getModelIri(), 'type'  => 'uri'),
            false);
    }
}