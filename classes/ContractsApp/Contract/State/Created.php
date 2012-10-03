<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Newly created contract (non published, non deleted)
 */
class ContractsApp_Contract_State_Created extends ContractsApp_Contract_State
{
    public function __toString() {
        return "Newly created contract";
    }
    
    public function canDelete() {
        return true;
    }
    public function canPublishPriorInformationNotice() {
        return true;
    }
    public function canPublishContractNotice() {
        return true;
    }
    public function canPublishContractAward() {
        return true;
    }
    public function canView() {
        return true;
    }
    public function canEdit() {
        return true;
    }
}