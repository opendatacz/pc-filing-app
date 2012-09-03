<?php
//= FUNCTIONS ==============================================================
function printInputBox($privatevalues,$publicvalues,$predicate,$sid=0,$canEdit=false,$new=false)
{
    //$value = getStoredValue($privatevalues,$publicvalues,$predicate,$id);
    $id = $sid;
    $privateValues = getValuesForPredicate($privatevalues,$predicate);
    $publicValues = getValuesForPredicate($publicvalues,$predicate);
    //if ($predicate == "br:officialNumber")
    //    print_r($privatevalues);
    //$allValues = array_keys(array_merge(array_flip($privateValues),array_flip($publicValues)));
    $allValues = array_unique(array_merge($privateValues,$publicValues));
    if ($new && $canEdit)
        echo "<input type=\"text\" id=\"$predicate$id\" name=\"$predicate$id\" value=\"\" />";
    else foreach ($allValues as $value) {
        if ($id != $sid)
            echo "<br />";
        if ($canEdit && in_array($value,$privateValues))
            echo "<input type=\"text\" id=\"$predicate$id\" name=\"$predicate$id\" value=\"$value\" />";
        else
            echo "<span id=\"$predicate$id\" class=\"inputspan\">$value</span>";
        if (in_array($value,$privateValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
        if (in_array($value,$publicValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
        $id++;
    }
}
function printInputBox2($privatevalues,$publicvalues,$predicate,$sid=0,$canEdit=false,$new=false,$class="medium",$additional="")
{
    //$value = getStoredValue($privatevalues,$publicvalues,$predicate,$id);
    $id = $sid;
    //print_r($privatevalues);
    $privateValues = getValuesForPredicate($privatevalues,$predicate);
    //print_r($privateValues);
    $publicValues = getValuesForPredicate($publicvalues,$predicate);
    //if ($predicate == "br:officialNumber")
    //    print_r($privatevalues);
    //$allValues = array_keys(array_merge(array_flip($privateValues),array_flip($publicValues)));
    $allValues = array_unique(array_merge($privateValues,$publicValues));
    if ($new && $canEdit)
        echo "<input type=\"text\" id=\"$predicate$id\" name=\"$predicate$id\" value=\"\" class=\"element text $class\"$additional />";
    else if ($allValues == array())
        echo "<input disabled=\"disabled\" type=\"text\" id=\"$predicate$id\" name=\"$predicate$id\" value=\"\" class=\"element text $class\"$additional />";
    else foreach ($allValues as $value) {
        if ($id != $sid)
            echo "<br />";
        if ($canEdit && in_array($value,$privateValues))
            echo "<input type=\"text\" id=\"$predicate$id\" name=\"$predicate$id\" value=\"$value\" class=\"element text $class\"$additional />";
        else
            echo "<input disabled=\"disabled\" type=\"text\" id=\"$predicate$id\" name=\"$predicate$id\" value=\"$value\" class=\"element text $class\"$additional />";
        if (in_array($value,$privateValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
        if (in_array($value,$publicValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
        $id++;
    }
}
function printTextarea2($privatevalues,$publicvalues,$predicate,$sid=0,$canEdit=false,$new=false)
{
    $id = $sid;
    $privateValues = getValuesForPredicate($privatevalues,$predicate);
    $publicValues = getValuesForPredicate($publicvalues,$predicate);
    $allValues = array_unique(array_merge($privateValues,$publicValues));
    $disabled=' disabled="disabled"';
    if ($new && $canEdit) {
        $disabled='';
        $allValues = array(0 => "");
    } else if ($allValues == array()) {
        $allValues = array(0 => "");
    }
    foreach ($allValues as $value) {
        if ($id != $sid)
            echo "<br />";
        if ($canEdit && in_array($value,$privateValues))
            $disabled='';
        else if ($new && $canEdit)
            $disabled='';
        else
            $disabled=' disabled="disabled"';
        echo '<textarea',$disabled,' id="',$predicate,$id,'" name="',$predicate,$id,'" class="element textarea small">',$value,'</textarea>';
        if (in_array($value,$privateValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
        if (in_array($value,$publicValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
        $id++;
    }
}
function printSelectCountry2($privatevalues,$publicvalues,$predicate,$sid=0,$canEdit=false,$new=false,$class="medium")
{
    $id = $sid;
    $privateValues = getValuesForPredicate($privatevalues,$predicate);
    $publicValues = getValuesForPredicate($publicvalues,$predicate);
    $allValues = array_unique(array_merge($privateValues,$publicValues));
    $disabled=' disabled="disabled"';
    if ($new && $canEdit) {
        $disabled='';
        $allValues = array(0 => "");
    } else if ($allValues == array()) {
        $allValues = array(0 => "");
    }
    foreach ($allValues as $value) {
        if ($id != $sid)
            echo "<br />";
        if ($canEdit && in_array($value,$privateValues))
            $disabled='';
        echo '<select',$disabled,' class="element select ',$class,'" id="',$predicate,$id,'" name="',$predicate,$id,'">';
        foreach ($GLOBALS["all_countries"] as $code => $country) {
            if ($value == $code)
                $selected = ' selected="selected"';
            else $selected = "";
            echo "<option value=\"$code\"$selected>$country</option>";
        }
		echo "</select>";
        if (in_array($value,$privateValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
        if (in_array($value,$publicValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
        $id++;
    }
}
function printSelectDate2($privatevalues,$publicvalues,$predicate,$sid=0,$canEdit=false,$new=false)
{
    $id = $sid;
    $privateValues = getValuesForPredicate($privatevalues,$predicate);
    $publicValues = getValuesForPredicate($publicvalues,$predicate);
    $allValues = array_unique(array_merge($privateValues,$publicValues));
    $disabled=' disabled="disabled"';
    if ($new && $canEdit) {
        $disabled='';
    }
    if ($allValues == array()) {
        $allValues = array(0 => "");
    }
    foreach ($allValues as $value) {
        $dateparts = explode("-",$value);
        if(count($dateparts) != 3)
            $dateparts = array("","","");
        if ($id != $sid)
            echo "<br />";
        if ($canEdit && in_array($value,$privateValues))
            $disabled='';
        echo '
        <span>
			<input',$disabled,' id="',$predicate,$id,'_2" name="',$predicate,$id,'_2" class="element text" size="2" maxlength="2" value="',$dateparts[2],'" type="text"> /
			<label for="',$predicate,$id,'_2">DD</label>
		</span>
		<span>
			<input',$disabled,' id="',$predicate,$id,'_1" name="',$predicate,$id,'_1" class="element text" size="2" maxlength="2" value="',$dateparts[1],'" type="text"> /
			<label for="',$predicate,$id,'_1">MM</label>
		</span>
		<span>
	 		<input',$disabled,' id="',$predicate,$id,'_3" name="',$predicate,$id,'_3" class="element text" size="4" maxlength="4" value="',$dateparts[0],'" type="text">
			<label for="',$predicate,$id,'_3">YYYY</label>
		</span>
        ';
        if ($disabled == "") {
            echo '
    		<span id="calendar_',$predicate,$id,'">
    			<img id="cal_img_',$predicate,$id,'" class="datepicker" src="',$GLOBALS["cBaseUri"],'/img/calendar.gif" alt="Pick a date.">	
    		</span>
    		<script type="text/javascript">
    			Calendar.setup({
    			inputField	 : "',$predicate,$id,'_3",
    			baseField    : "',$predicate,$id,'",
    			displayArea  : "calendar_',$predicate,$id,'",
    			button		 : "cal_img_',$predicate,$id,'",
    			ifFormat	 : "%B %e, %Y",
    			onSelect	 : selectDate
    			});
    		</script>
            ';
        }
        if (in_array($value,$privateValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
        if (in_array($value,$publicValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
        $id++;
    }
}
function printSelectPrice2($privatevalues,$publicvalues,$predicate,$predicate2,$sid=0,$canEdit=false,$new=false,$class="")
{
    $id = $sid;
    $privateValues = getValuesForPredicate($privatevalues,$predicate);
    $publicValues = getValuesForPredicate($publicvalues,$predicate);
    $privateValues2 = getValuesForPredicate($privatevalues,$predicate2);
    $publicValues2 = getValuesForPredicate($publicvalues,$predicate2);
    $allValues = array_unique(array_merge($privateValues,$publicValues));
    $allValues2 = array_unique(array_merge($privateValues2,$publicValues2));
    $disabled=' disabled="disabled"';
    if ($new && $canEdit) {
        $disabled='';
    }
    if ($allValues == array()) {
        $allValues = array(0 => "");
        $allValues2 = array(0 => "");
    }
    foreach ($allValues as $index => $value) {
        $value2 = $allValues2[$index];
        if ($id != $sid)
            echo "<br />";
        if ($canEdit && in_array($value,$privateValues))
            $disabled='';
        echo '<span>';
		echo '<input ',$disabled,' id="',$predicate2,$id,'" name="',$predicate2,$id,'" class="element text currency" size="10" value="',$value2,'" type="text" />'; 		
		echo '<label for="',$predicate2,$id,'">Price</label>';
		echo '</span>';
        echo '<span>';
        echo '<select',$disabled,' class="element select',$class,'" id="',$predicate,$id,'" name="',$predicate,$id,'">';
        foreach ($GLOBALS["all_currencies"] as $code => $currency) {
            if ($value == $code)
                $selected = ' selected="selected"';
            else $selected = "";
            echo "<option value=\"$code\"$selected>$currency</option>";
        }
		echo "</select>";
        echo '<label for="',$predicate,$id,'">Currency</label>';
        echo '</span>';
        if (in_array($value,$privateValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
        if (in_array($value,$publicValues))
            echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
        $id++;
    }
}
function printEditTender($ppvalues,$pppricevalues,$uri,$canEdit,$canPublish)
{
    //zjednodusena logika - nepocita s rozdily v public a private store a s vice hodnotami
    $tid = substr($uri,strrpos($uri,"/")+1);
    $getfrom = "private";
    if ($ppvalues["private"] == array())
        $getfrom = "public";
    $supplierVal = getValuesForPredicate($ppvalues[$getfrom],"pc:supplier");
    $currencyVal = getValuesForPredicate($pppricevalues[$getfrom],"gr:hasCurrency");
    $priceVal = getValuesForPredicate($pppricevalues[$getfrom],"gr:hasCurrencyValue");
    echo '<div id="div_4t_',$tid,'">';
    echo '<label class="description" for="pc:tender">Tender ',$tid;
    if ($canEdit)
        echo "<small> <a href=\"javascript:;\" onclick=\"removeTender('div_4t_$tid')\">(Remove tender)</a></small>";
    echo '</label>';
    echo '<span><input id="pc:tender_gr:hasCurrencyValue',$tid,'" name="pc:tender_gr:hasCurrencyValue',$tid,'" class="element text currency" size="10" value="',$priceVal[0],'" type="text" /><label for="pc:tender_gr:hasCurrencyValue',$tid,'">Price</label></span>';
    echo '<span><select class="element select" id="pc:tender_gr:hasCurrency',$tid,'" name="pc:tender_gr:hasCurrency',$tid,'">';
    foreach ($GLOBALS["all_currencies"] as $code => $currency) {
            if ($currencyVal[0] == $code)
                $selected = ' selected="selected"';
            else $selected = "";
            echo "<option value=\"$code\"$selected>$currency</option>";
        }
    echo '</select><label for="pc:tender_gr:hasCurrency',$tid,'">Currency</label></span>';
    if ($ppvalues["private"] != array())
        echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/privatespace.png\" class=\"sourceico\" alt=\"in private data space\" title=\"in private data space\"  />";
    if ($ppvalues["public"] != array())
        echo "<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace.png\" class=\"sourceico\" alt=\"in public data space\" title=\"in public data space\"  />";
    if ($canEdit && $canPublish) {
        $aopen = '<a href="?page=tender_award&amp;turi='.urlencode($uri).'">';
        echo "| $aopen<img src=\"{$GLOBALS["cBaseUri"]}/img/publicspace_publish.png\" class=\"sourceico\" alt=\"publish to public data space\" title=\"publish to public data space\" /></a>{$aopen}Award tender</a>";
    }
    //echo '<p class="guidelines" id="guide_4t_',$tid,'"><small>pc:offeredPrice : Property for price offered by a supplier.<br />pc:supplier : Property for supplier submitting the tender.</small></p>'; 
    echo '<div><input type="text" id="pc:tender_pc:supplier',$tid,'" name="pc:tender_pc:supplier',$tid,'" value="',$supplierVal[0],'" class="element text large" /><label for="pc:supplier">Supplier</label></div>';
    echo '</div>';
}

function getFullPredicate($predicate)
{
    $pred = $predicate;
    $pos = strpos($pred,":");
    if ($pos !== false) {
        $pref = substr($predicate,0,$pos);
        $pred = $GLOBALS["ns_var"][$pref].substr($pred,$pos+1);
    }
    return $pred;
}
function getValuesForPredicate($values,$predicate)
{
    $pred = getFullPredicate($predicate);
    $vals = array();
    if (isset($values[$pred]))
        foreach ($values[$pred] as $value)
            if (empty($value["content"]))
                $vals[] = $value["uri"];
            else
                $vals[] = $value["content"];
    return $vals;
}

/*function getTenders($model, $conuri) {
    $queryten = 'SELECT ?t ?p WHERE {
        <'.$conuri.'> <'.$GLOBALS["ns_var"]["pc"].'tender> ?t . 
        OPTIONAL {
        ?t <'.$GLOBALS["ns_var"]["pc"].'offeredPrice> ?p . }}';
    $tendersuri = $model->sparqlQuery($queryten);
    $turis = array();
    foreach ($tendersuri as $turi)
        $turis[] = $turi["t"];
    return $turis;
}
function getHighestTenderId($tenders) {
    $max = 0;
    $count = count($tenders);
    foreach ($tenders as $tender) {
        $val = substr($tender,strrpos($tender,"/")+1);
        if (!is_numeric($val))
            return $count;
        if ($val > $max)
            $max = $val;
    }
    return $max
}*/

//= OTHER FUNCTIONS ============================================================
function getUriFromQuery($query,$publicmodel,$privatemodel,$r="r")
{
    $uri = $publicmodel->sparqlQuery($query);
    if ($uri === array() && $privatemodel !== false) {
        $uri = $privatemodel->sparqlQuery($query);
    }
    if (isset($uri[0][$r]))
        return $uri[0][$r];
    return false;
}
function getPPValues($store,$publicmodel,$privatemodel,$uri)
{
    $publicresource = new OntoWiki_Model_Resource($store,$publicmodel,$uri);
    $publicvalues = $publicresource->getValues();
    if (isset($publicvalues[$publicmodel->getModelIri()]))
        $publicvalues = $publicvalues[$publicmodel->getModelIri()];
    $privatevalues = array();
    if ($privatemodel !== false) {
        $privateresource =  new OntoWiki_Model_Resource($store,$privatemodel,$uri);
        $privatevalues = $privateresource->getValues();
        if (isset($privatevalues[$privatemodel->getModelIri()]))
            $privatevalues = $privatevalues[$privatemodel->getModelIri()];
    }
    return array("private" => $privatevalues, "public" => $publicvalues);
}
function deleteResource($store,$modelIri,$resource)
{
    $stmtArray = array();

    // query for all triples to delete them
    $sparqlQuery = new Erfurt_Sparql_SimpleQuery();
    $sparqlQuery->setProloguePart('SELECT ?p, ?o');
    $sparqlQuery->addFrom($modelIri);
    $sparqlQuery->setWherePart('{ <' . $resource . '> ?p ?o . }');

    $result = $store->sparqlQuery($sparqlQuery,array('result_format'=>'extended'));
    // transform them to statement array to be compatible with store methods
    foreach ($result['results']['bindings'] as $stmt) {
        $stmtArray[$resource][$stmt['p']['value']][] = $stmt['o'];
    }

    $store->deleteMultipleStatements($modelIri, $stmtArray);
}


//edit processing
function literalEdit($store,$model,$subject,$predicate,$newobj,$oldobj)
{
    if ($newobj != $oldobj) {
        $store->addStatement($model->getModelUri(),
            $subject,
            $predicate,
            array('value' => $newobj, 'type'  => 'literal' ));
        if ($oldobj !== false)
            $store->deleteMatchingStatements($model->getModelUri(),
                $subject,
                $predicate,
                array('value' => $oldobj, 'type'  => 'literal'));
    }
}
function uriEdit($store,$model,$subject,$predicate,$newuri,$olduri)
{
    if ($newuri != $olduri) {
        $store->addStatement($model->getModelUri(),
            $subject,
            $predicate,
            array('value' => $newuri, 'type'  => 'uri' ));
        if ($olduri !== false)
            $store->deleteMatchingStatements($model->getModelUri(),
                $subject,
                $predicate,
                array('value' => $olduri, 'type'  => 'uri'));
    }
}

function dateEdit($store,$model,$subject,$predicate,$newobjY,$newobjM,$newobjD,$oldobj)
{
    if (!is_numeric($newobjY)) $newobjY = "0000";
    if (!is_numeric($newobjM)) $newobjM = "00";
    if (!is_numeric($newobjD)) $newobjD = "00";
    while (strlen((string)$newobjY) < 4)
        $newobjY = "0".$newobjY;
    while (strlen((string)$newobjM) < 2)
        $newobjM = "0".$newobjM;
    while (strlen((string)$newobjD) < 2)
        $newobjD = "0".$newobjD;
    $newobj = $newobjY."-".$newobjM."-".$newobjD;
    $xsdp = $model->getNamespacePrefix('http://www.w3.org/2001/XMLSchema#');
    if ($newobj != $oldobj) {
        $store->addStatement($model->getModelUri(),
            $subject,
            $predicate,
            array('value' => $newobj, 'type'  => 'literal', 'datetype' => $xsdp.":date" ));
        if ($oldobj !== false)
            $store->deleteMatchingStatements($model->getModelUri(),
                $subject,
                $predicate,
                array('value' => $oldobj, 'type'  => 'literal', 'datetype' => $xsdp.":date" ));
    }
}