<?php
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<base href="http://www.ajaxdaddy.com/media/demos/play/1/jquery-autocomplete/autocomplete/" />

	<title>j</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

    <!--<link rel="stylesheet" type="text/css" href="<?php echo $this->themeUrlBase ?>/styles/default.css" media="screen" />-->
    <!--
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/default-edited.css" media="screen" />
    -->
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/main.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/elements.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/form.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/jquery.autocomplete.css" media="screen" />

    <!-- jQuery -->
    <script type="text/javascript" src="<?php echo $this->themeUrlBase ?>scripts/libraries/jquery.js"></script>
    <script type="text/javascript" src="<?php echo $this->themeUrlBase ?>scripts/libraries/jquery-ui.js"></script>

    <!--script type="text/javascript" src="<?php echo $this->baseUri ?>/js/jquery-fluid16.js"></script-->
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/jquery.autocomplete.js"></script>
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/calendar.js"></script>
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/view.js"></script>
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/custom.js"></script>

    <!--
    <script type="text/javascript" src="<?php echo $this->themeUrlBase ?>scripts/libraries/jquery.js"></script>
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/jquery.autocomplete.js"></script>
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/jquery.autocomplete.css" media="screen" />
    -->
</head>
<body>

<form action="">
	<p>
		Ajax City Autocomplete: (try a few examples like: 'Little Grebe', 'Black-crowned Night Heron', 'Kentish Plover')<br />
		<input type="text" style="width: 200px;" value="" id="pc_mainObject" />
		<!--<input type="button" onclick="lookupAjax2();" value="Get Value"/>-->
	</p>
    <ul>
    <li>maliny</li>
    <li>jahody</li>
    </ul>
</form>

<script type="text/javascript">
    function findValue(li) {
        if( li == null ) return alert("No match!");
        if( !!li.extra ) var sValue = li.extra[0];
        else var sValue = li.selectValue;
        alert("The value you selected was: " + sValue);
    }
    function selectItem(li) {
        findValue(li);
    }
    function formatItem(row) {
        return row[0] + " (id: " + row[1] + ")";
    }
    function lookupAjax2(){
        var oSuggest = $("#pc_mainObject")[0].autocompleter;
        oSuggest.findValue();
        return false;
    }
    var extra = new Array()
    extra["autocomplete"] = "cpv";
    $("#pc_mainObject").autocomplete(
        "http://lod2.vse.cz/ontowiki/",
        {
            delay:10,
            minChars:2,
            matchSubset:1,
            matchContains:1,
            cacheLength:10,
            onItemSelect:selectItem,
            onFindValue:findValue,
            formatItem:formatItem,
            autoFill:true,
            extraParams:extra
        }
    );
</script>
</body>
</html>