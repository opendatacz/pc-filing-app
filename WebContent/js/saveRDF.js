function checkRequired(){
	var result = true;
	
	$('input').each(function() {
	    if($(this).prop('required') && $(this).val() == ""){

	        alert("Please specify all required properties.");
	        $(this).focus();
	        result = false;
	        return false;
	    }
	});
	
	return result;
}

function generateRDF() {
	
	if(checkRequired()!=false){
	// Simple helper function creates a new element from a name, so you don't
	// have to add the brackets etc.
	$.createElement = function(name) {
		return $('<' + name + ' />');
	};

	/*
	 * xml root element - because html() does not include the root element and
	 * we want to include <rdf:RDF /> in the output. There may be a better way
	 * to do this.
	 */
	var $root = $('<XMLDocument />');
	var $rootAttr = $('#RDFroot').get(0).attributes;
	var $rootAttrString = "";
	for ( var i = 0; i < $rootAttr.length; i++) {
		if ($rootAttr[i].name.indexOf("xmlns") == 0)
			$rootAttrString += " " + $rootAttr[i].name + '="'
					+ $rootAttr[i].value + '"';
	}
	
	
	$rootAttrString +=' xmlns:co="'+tData.ontologyURI+'#"';

	$root.append($('<?xml version="1.0" encoding="utf-8"? />'));
	$root.append($("'<rdf:RDF " + $rootAttrString + " />'"));
	if ($('#RDFroot').children('form').length != 0
			&& $('#RDFroot').children('form').children('div').length != 0) {
		$rootTree = $root.children();
		$mainEntity = $('#RDFroot').children('form');
	}
	

	function splitCases($tempStringArray) {
		if (/[A-Z]/.test($tempStringArray[0][0]))
			$tempString = "*" + $tempStringArray[0];
		else
			$tempString = $tempStringArray[0];
		for ( var cnt = 1; cnt < $tempStringArray.length; cnt++) {
			$tempString += "*" + $tempStringArray[cnt];
		}

		return $tempString;
	}
	
	function divRepeat(mainEntity, referEntity) {

		if (mainEntity.children('input').length != 0) {
			for ( var j = 0; j < mainEntity.children('input').length; j++) {
				if (!mainEntity.children('input').eq(j).hasClass('btn')
						&& mainEntity.children('input').eq(j).attr('type') != 'hidden') {
					$foo = mainEntity.children('input').eq(j);
					$tempStringArray = $foo.attr('property').split("#")[1]
							.match(/[A-Z]?[a-z]+|[0-9]+/g);
					$tempEntity = $("<gr:" + splitCases($tempStringArray) + ">")
							.text($foo.val());
					$tempEntity.attr('rdf:datatype', $foo.attr('datatype'));
					referEntity.append($tempEntity);
				}
			}
		}

		if (mainEntity.children('textarea').length != 0) {
			for ( var j = 0; j < mainEntity.children('textarea').length; j++) {
				$foo = mainEntity.children('textarea').eq(j);
				$tempStringArray = $foo.attr('property').split("#")[1]
						.match(/[A-Z]?[a-z]+|[0-9]+/g);
				$tempEntity = $("<gr:" + splitCases($tempStringArray) + ">")
						.text($foo.val());
				$tempEntity.attr('rdf:datatype', $foo.attr('datatype'));
				referEntity.append($tempEntity);
			}
		}

		if (mainEntity.children('div').length != 0) {
			for ( var jj = 0; jj < mainEntity.children('div').length; jj++) {
				$foo = mainEntity.children('div').eq(jj);
				if ($foo.attr('rel') != null || $foo.attr('rel') != undefined) {
					$tempStringArray = $foo.attr('rel').split("#")[1]
							.match(/[A-Z]?[a-z]+|[0-9]+/g);
					$tempEntity = $("<gr:" + splitCases($tempStringArray) + ">");
					$tempEntity.attr('rdf:resource', $foo.children('div').attr(
							'about'));
					referEntity.append($tempEntity);
				} else {
					$tempStringArray = $foo.attr('typeof').split("#")[1]
							.match(/[A-Z]?[a-z]+|[0-9]+/g);
					if($foo.attr('typeof').indexOf(tData.ontologyURI) != -1)
						$tempEntity = $("<co:" + splitCases($tempStringArray) + ">");						
					else
						$tempEntity = $("<gr:" + splitCases($tempStringArray) + ">");
					$tempEntity.attr('rdf:about', $foo.attr('about'));
					$tempEntity.attr('rdf:datatype', $foo.attr('datatype'));
					$rootTree.append($tempEntity);
				}

				divRepeat($foo, $tempEntity);
			}
		}

	}

	divRepeat($mainEntity, $rootTree);

	$.post("FormApp", {
		action : "addSpecification",
		contractURI : $('#contractURI').val(),
		root : $root.html(),
		cpv : $('input#cpv').val(),
		ontologyURI : tData.ontologyURI
	}, function(data) {
		//console.log(data);
		window.location = "buyer-prepared.html";
	});
	}
};