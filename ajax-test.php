<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title>Autocomplete - Test (jQuery UI)</title>
    
    <link rel="stylesheet" type="text/css" href="<?php echo $this->themeUrlBase ?>/styles/default.css" media="screen" />
    <!--<link rel="stylesheet" type="text/css" href="<?php echo $this->themeUrlBase ?>/css/jquery-ui-1.8.24.custom.css" media="screen" />-->
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/main.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/elements.css" media="screen" />
    <link rel="stylesheet" type="text/css" href="<?php echo $this->baseUri ?>/css/form.css" media="screen" />
    
    <script type="text/javascript" src="<?php echo $this->themeUrlBase ?>scripts/libraries/jquery.js"></script>
    <script type="text/javascript" src="<?php echo $this->themeUrlBase ?>scripts/libraries/jquery-ui.js"></script>
    <!--<script type="text/javascript" src="<?php echo $this->baseUri ?>/js/jquery-ui-1.8.24.custom.min.js"></script>-->
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/calendar.js"></script>
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/view.js"></script>
    <script type="text/javascript" src="<?php echo $this->baseUri ?>/js/custom.js"></script>
    
	<style>
	.ui-autocomplete-loading { background: white url('<?php echo $this->baseUri ?>/img/loader.gif') right center no-repeat; }
	</style>

</head>
<body>

<div class="demo">

	<script>
	$(function() {
		function log( message ) {
            $( "#pc_mainObject0" ).val(message);
			$( "<div/>" ).text( message ).prependTo( "#log" );
			$( "#log" ).scrollTop( 0 );
		}

		$( "#pc_mainObjectX" ).autocomplete({
			source: "http://lod2.vse.cz/ontowiki/?autocomplete=cpv",
			minLength: 2,
			select: function( event, ui ) {
				log( ui.item ?
					"Selected: " + ui.item.value + " aka " + ui.item.id :
					"Nothing selected, input was " + this.value );
			}
		});
	});
	</script>

<div class="ui-widget">
	<label for="birds">CPV/Business: </label>
	<input id="pc_mainObjectX" />
</div>

<div class="ui-widget" style="margin-top:2em; font-family:Arial">
	Result:
    <input type="input" name="pc_mainObject0" id="pc_mainObject0" />
	<div id="log" style="height: 200px; width: 300px; overflow: auto;" class="ui-widget-content"></div>
</div>

</div><!-- End demo -->



<div class="demo-description">
<p>The Autocomplete widgets provides suggestions while you type into the field. Here the suggestions are bird names, displayed when at least two characters are entered into the field.</p>
<p>The datasource is a server-side script which returns JSON data, specified via a simple URL for the source-option. In addition, the minLength-option is set to 2 to avoid queries that would return too many results and the select-event is used to display some feedback.</p>
</div><!-- End demo-description -->

</body>
</html>




<?php
