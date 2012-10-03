<?php

/**
 * Public Contracts Filing App
 *  - Contract State: Nonexistent contract (not created or deleted)
 */
class ContractsApp_Contract_State_Nonexistent extends ContractsApp_Contract_State
{
    public function __toString() {
        return "Nonexistent contract";
    }
}