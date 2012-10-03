<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Contract with published contract notice
 */
class ContractsApp_Contract_State_PublishedContractNotice extends ContractsApp_Contract_State
{
    public function __toString() {
        return "Contract with published contract notice";
    }
    
    public function canPublishContractAward() {
        return true;
    }
    public function canCancel() {
        return true;
    }
    public function canView() {
        return true;
    }
    public function canEdit() {
        return true;
    }
}