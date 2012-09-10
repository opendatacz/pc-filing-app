<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Finished contract
 */
class ContractsApp_Contract_State_Finished extends ContractsApp_Contract_State
{
    public function canView() {
        return true;
    }
}