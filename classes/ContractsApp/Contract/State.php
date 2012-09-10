<?php

/**
 * Public Contracts Filing App
 *  - Contract State abstract class
 */
abstract class ContractsApp_Contract_State
{
    /**
     * Can delete contract in private space?
     * Only if the contract is not published
     * @return boolean          
     */
    public function canDelete() {
        return false;
    }
    
    /**
     * Can publish prior information notice?
     * Only if the contract is not cancelled, nor deleted and hasn't been yet published
     * @return boolean          
     */
    public function canPublishPriorInformationNotice() {
        return false;
    }
    
    /**
     * Can publish contract notice?
     * Only if the contract is not cancelled, nor deleted and neither contract notice nor contract award was published
     * @return boolean          
     */
    public function canPublishContractNotice() {
        return false;
    }
    
    /**
     * Can publish contract award?
     * Only if the contract is not cancelled, nor deleted and contract award was not published
     * @return boolean          
     */
    public function canPublishContractAward() {
        return false;
    }
    
    /**
     * Can cancel contract?
     * Only if the contract is not cancelled, nor deleted and was published
     * @return boolean          
     */
    public function canCancel() {
        return false;
    }
    
    /**
     * Can view values?
     * Only if the contract is not deleted
     * @return boolean          
     */
    public function canView() {
        return false;
    }
    
    /**
     * Can edit values?
     * Only if the contract is not cancelled nor finished
     * @return boolean          
     */
    public function canEdit() {
        return false;
    }

}