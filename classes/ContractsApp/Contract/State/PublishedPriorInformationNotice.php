<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Contract with published prior information notice
 */
class ContractsApp_Contract_State_PublishedPriorInformationNotice extends ContractsApp_Contract_State
{
    public function __toString() {
        return "Contract with publsihed prior information notice";
    }
    
    public function canPublishContractNotice() {
        return true;
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