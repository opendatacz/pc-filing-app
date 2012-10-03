<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Contract with published contract award (with awarded tender)
 */
class ContractsApp_Contract_State_PublishedContractAward extends ContractsApp_Contract_State
{
    public function __toString() {
        return "Awarded contract";
    }
    
    public function canCancel() {
        return true;
    }
    public function canView() {
        return true;
    }
    /*public function canEdit() {
        return true;
    }*/
}