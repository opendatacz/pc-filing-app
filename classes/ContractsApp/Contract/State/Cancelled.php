<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Cancelled contract
 */
class ContractsApp_Contract_State_Cancelled extends ContractsApp_Contract_State
{
    public function __toString() {
        return "Contract cancelled";
    }
    
    public function canView() {
        return true;
    }
}