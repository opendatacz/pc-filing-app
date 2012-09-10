<?php

/**
 * Main class for Public Contracts Filing Application.
 *
 */
class ContractsApp
{

    // ------------------------------------------------------------------------
    // --- Properties
    // ------------------------------------------------------------------------

    /** 
     * Singleton instance
     * @var ContractsApp 
     */
    private static $_instance = null;

    // ------------------------------------------------------------------------
    // --- Construct Methods
    // ------------------------------------------------------------------------ 

    /**
     * Constructor
     * @param OntoWiki $ow     
     */
    private function __construct($ow)
    {
        $user = $ow->getUser();
        $store = $ow->erfurt->getStore();
        $username = $user->getUsername();
        $psuri = getPrivateSpace($ow);
        if ($psuri === false) throw new ContractsApp_Exception("User has no private space.");
        $modeluri = $psuri[0];
        $model_private = $store->getModel($modeluri);
    }

    /**
     * Singleton instance
     * @return ContractsApp
     */
    public static function getInstance()
    {
        if (self::$_instance === null) {
            throw new ContractsApp_Exception("ContractsApp not instantiated.");
            //self::$_instance = new self();
        }
        return self::$_instance;
    }
    
    /**
     * Singleton instance with instantiation
     * @param OntoWiki $ow   
     * @return ContractsApp
     */
    public static function newInstance($ow)
    {
        if (self::$_instance === null) {
            self::$_instance = new self($ow);
        }
        return self::$_instance;
    }

    // ------------------------------------------------------------------------
    // --- Public Methods
    // ------------------------------------------------------------------------ 

    /**
     * Returns the application config array
     *
     * @return Array
     */
    public function getConfig()
    {
        
    }
    
    /**
     * @return string
     */
    public function getUsername()
    {
        
    }
    
    /**
     * @return string
     */
    public function getUserUri()
    {
        
    }
}