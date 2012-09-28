<?php

/**
 * Public Contracts Filing App
 *  - Contract Review class
 */
class ContractsApp_Contract_Review
{

    public static function newReview($store, $model_public, $contract_uri, $author_uri)
    {
        //$this->_store = $store;
        //$this->_model_public = $model_public;
        //$this->_contract_uri = $contract_uri;
        
        $templateData = loadData();
        $uri = $templateData["resource"]."review/".generateGuid();
        $rating_uri = $uri."/rating/1";
        //$this->_uri = $templateData["resource"]."review/".generateGuid();
        
        $stmtArray = array(
            $uri => array(
                EF_RDF_TYPE => array(array(
                    'type'  => 'uri',
                    'value' => 'http://schema.org/Review' 
                )),
                'http://schema.org/itemReviewed' => array(array(
                    'type'  => 'uri',
                    'value' => $contract_uri
                )),
                'http://schema.org/reviewBody' => array(array(
                    'type'  => 'literal',
                    'value' => ""
                )),
                'http://schema.org/reviewRating' => array(array(
                    'type'  => 'uri',
                    'value' => $rating_uri
                )),
                'http://schema.org/author' => array(array(
                    'type'  => 'uri',
                    'value' => $author_uri
                ))
            ),
            $rating_uri => array(
                EF_RDF_TYPE => array(array(
                    'type'  => 'uri',
                    'value' => 'http://schema.org/Rating'
                )),
                'http://schema.org/bestRating' => array(array(
                    'type'  => 'literal',
                    'value' => "5"
                )),
                'http://schema.org/worstRating' => array(array(
                    'type'  => 'literal',
                    'value' => "1"
                )),
                'http://schema.org/ratingValue' => array(array(
                    'type'  => 'literal',
                    'value' => ""
                ))
            ),
        );
        
        //echo $model_public->getModelIri();
        //print_r($stmtArray);
        //Die();
        $store->addMultipleStatements($model_public->getModelIri(), $stmtArray, false);
        //TODO: invalidate cache
        
        return new ContractsApp_Contract_Review($store, $model_public, $uri);
    }

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
     * @var string 
     */
    private $_uri = null;
    
    /** 
     * @var string 
     */
    private $_contract_uri = null;
    
    /** 
     * @var string 
     */
    private $_text = null;
    
    /** 
     * @var string 
     */
    private $_author_uri = null;
    
    /** 
     * rating (1 to 5)
     * @var int 
     */
    private $_rating = null;

    /**
     * Constructor
     * @param string $uri
     */
    public function __construct($store, $model_public, $uri)
    {
        $this->_store = $store;
        $this->_model_public = $model_public;
        $this->_uri = $uri;
        $this->_initiate();
    }
    
    // -------------------------------------------------------------------------
    // --- Private Methods -----------------------------------------------------
    // -------------------------------------------------------------------------
    
    private function _initiate()
    {
        $ruri = $this->_uri;
        $contentQuery = "SELECT ?contract ?text ?author ?rating WHERE {
            <$ruri> a <{$GLOBALS["ns_var"]["schema"]}Review> .
            <$ruri> <{$GLOBALS["ns_var"]["schema"]}itemReviewed> ?contract .
            OPTIONAL {
                <$ruri> <{$GLOBALS["ns_var"]["schema"]}reviewBody> ?text .
            }
            OPTIONAL {
                <$ruri> <{$GLOBALS["ns_var"]["schema"]}author> ?author .
            }
            OPTIONAL {
                <$ruri> <{$GLOBALS["ns_var"]["schema"]}reviewRating> ?r .
                ?r <{$GLOBALS["ns_var"]["schema"]}ratingValue> ?rating .
            }
            }";
        $result = $this->_model_public->sparqlQuery($contentQuery);
        $result = $result[0];
        if (empty($result["contract"]))
            throw new ContractsApp_Exception("Review not found in public data space.");
        $this->_contract_uri = $result["contract"];
        if (!empty($result["text"]))
            $this->_text = $result["text"];
        if (!empty($result["author"]))
            $this->_author_uri = $result["author"];
        if (!empty($result["rating"]))
            $this->_rating = $result["rating"];
    }
    
    // -------------------------------------------------------------------------
    // --- Public Methods ------------------------------------------------------
    // -------------------------------------------------------------------------
    
    public function getUri()
    {
        return $this->_uri;
    }
    
    public function getAuthorUri()
    {
        return $this->_author_uri;
    }
    
    public function getText()
    {
        return $this->_text;
    }
    
    public function getRating()
    {
        return $this->_rating;
    }
    
    public function setReview($text, $rating)
    {
        $ruri = $this->_uri;
        //rating
        if (is_numeric($rating) && ($rating != $this->getRating()) && ($rating >= 0) && ($rating <= 5)) {
            if ($rating == 0) $rating = "";
            //find rating object
            $ratingQuery = "SELECT ?r WHERE {
                <$ruri> a <{$GLOBALS["ns_var"]["schema"]}Review> .
                <$ruri> <{$GLOBALS["ns_var"]["schema"]}reviewRating> ?r .
                ?r a <{$GLOBALS["ns_var"]["schema"]}Rating> .
                }";
            $result = $this->_model_public->sparqlQuery($ratingQuery);
            $result = $result[0];
            if (!isset($result["r"]))
                throw new ContractsApp_Exception("Rating object not found.");
            $raturi = $result["r"];
            //change value
            $this->_store->deleteMatchingStatements($this->_model_public->getModelUri(),
                $raturi,
                $GLOBALS["ns_var"]["schema"]."ratingValue",
                array('value' => $this->_rating, 'type'  => 'literal'),
                array("use_ac" => false));
            $this->_store->addStatement($this->_model_public->getModelUri(),
                $raturi,
                $GLOBALS["ns_var"]["schema"]."ratingValue",
                array('value' => $rating, 'type'  => 'literal' ),
                false);
        }
        //text
        if (!empty($text) && ($text != $this->getText())) {
            $this->_store->deleteMatchingStatements($this->_model_public->getModelUri(),
                $this->_uri,
                $GLOBALS["ns_var"]["schema"]."reviewBody",
                array('value' => $this->_text, 'type'  => 'literal'),
                array("use_ac" => false));
            $this->_store->addStatement($this->_model_public->getModelUri(),
                $this->_uri,
                $GLOBALS["ns_var"]["schema"]."reviewBody",
                array('value' => $text, 'type'  => 'literal' ),
                false);
        }
    }
}