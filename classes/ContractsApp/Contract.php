<?php

/**
 * Public Contracts Filing App - Contract class
 * 
 */
class ContractsApp_Contract
{
    /*const CONTRACT_STATE_NONEXISTENT               = -1;
    const CONTRACT_STATE_CANCELLED                 =  0;
    const CONTRACT_STATE_CREATED                   =  1;
    const CONTRACT_STATE_PUBLISHED_PRIOR_NOTICE    =  2;
    const CONTRACT_STATE_PUBLISHED_CONTRACT_NOTICE =  3;
    const CONTRACT_STATE_PUBLISHED_CONTRACT_AWARD  =  4;
    const CONTRACT_STATE_FINISHED                  =  5;*/
    
    // ------------------------------------------------------------------------
    // --- Properties
    // ------------------------------------------------------------------------

    /** 
     * @var Erfurt_Store 
     */
    private $_store = null;

    /** 
     * @var Erfurt_Rdf_Model 
     */
    private $_model_public = null;
    
    /** 
     * @var Erfurt_Rdf_Model 
     */
    private $_model_private = null;
    
    /** 
     * @var string 
     */
    private $_uri = null;
    
    /** 
     * @var ContractsApp_Contract_State 
     */
    private $_state = null;
    
    /**
     * Constructor
     * @param ContractsApp $contractsApp
     * @param string $uri
     */
    public function __construct($store, $model_public, $model_private, $uri)
    {
        $this->_store = $store;
        $this->_model_public = $model_public;
        $this->_model_private = $model_private;
        $this->_uri = $uri;
        $this->_initiateState();
    }
    
    // -------------------------------------------------------------------------
    // --- Private Methods -----------------------------------------------------
    // -------------------------------------------------------------------------
    
    private function _initiateState()
    {
        $curi = $this->_uri;
        $stateQueryPublic = "SELECT ?pin ?cn ?can ?cncln WHERE {
            <$curi> a <{$GLOBALS["ns_var"]["pc"]}Contract> .
            OPTIONAL {
                <$curi> <{$GLOBALS["ns_var"]["pc"]}notice> ?pin .
                ?pin a <{$GLOBALS["ns_var"]["pc"]}PriorInformationNotice> .
            }
            OPTIONAL {
                <$curi> <{$GLOBALS["ns_var"]["pc"]}notice> ?cn .
                ?cn a <{$GLOBALS["ns_var"]["pc"]}ContractNotice> .
            }
            OPTIONAL {
                <$curi> <{$GLOBALS["ns_var"]["pc"]}notice> ?can .
                ?can a <{$GLOBALS["ns_var"]["pc"]}ContractAwardNotice> .
            }
            OPTIONAL {
                <$curi> <{$GLOBALS["ns_var"]["pc"]}notice> ?cncln .
                ?cncln a <{$GLOBALS["ns_var"]["pc"]}CancellationNotice> .
            }
            }";
        $publicResult = $this->_model_public->sparqlQuery($stateQueryPublic);
        //print_r($publicResult);
        if ($this->_model_private != false) {
            $stateQueryPrivate = "SELECT ?t WHERE {
                <$curi> a <{$GLOBALS["ns_var"]["pc"]}Contract> .
                <$curi> a ?t .
                }";
            $privateResult = $this->_model_private->sparqlQuery($stateQueryPrivate);
        } else {
            $privateResult = array();
        }
        if ($publicResult == array()) {
            if ($privateResult == array())
                $this->_state = new ContractsApp_Contract_State_Nonexistent();
            else
                $this->_state = new ContractsApp_Contract_State_Created();
        } else {
            if (!empty($publicResult[0]["cncln"]))
                $this->_state = new ContractsApp_Contract_State_Cancelled();
            else if (!empty($publicResult[0]["can"]))
                $this->_state = new ContractsApp_Contract_State_PublishedContractAward();
            else if (!empty($publicResult[0]["cn"]))
                $this->_state = new ContractsApp_Contract_State_PublishedContractNotice();
            else if (!empty($publicResult[0]["pin"]))
                $this->_state = new ContractsApp_Contract_State_PublishedPriorInformationNotice();
            else //this should not happen
                $this->_state = new ContractsApp_Contract_State_Nonexistent();
        }
    }

    // -------------------------------------------------------------------------
    // --- Public Methods ------------------------------------------------------
    // -------------------------------------------------------------------------
    
    public function getState()
    {
        return $this->_state;
    }
    
}