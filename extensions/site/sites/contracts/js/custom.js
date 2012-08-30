/*  Custom JS */
function showExactOrBounds()
{
    var r1 = document.getElementById('priceradio_1');
    var r2 = document.getElementById('priceradio_2');
    var b = document.getElementById('li_4b');
    var c = document.getElementById('li_4c');
    var d = document.getElementById('li_4d');
    if (r1.checked) {
        b.style.display = "block";
        c.style.display = "none";
        d.style.display = "none";
    }
    if (r2.checked) {
        b.style.display = "none";
        c.style.display = "block";
        d.style.display = "block";
    }
}

function addTender() {
    var container_elem = document.getElementById('li_4t');
    var num_elem = document.getElementById('tenderCounter');
    var num = (document.getElementById('tenderCounter').value -1)+ 2;
    num_elem.value = num;
    var newdiv = document.createElement('div');
    var divIdName = 'div_4t_'+num;
    newdiv.setAttribute('id',divIdName);
    newdiv.innerHTML =
          '<label class="description" for="pc:tender">Tender '+num
        + '<small> <a href="javascript:;" onclick="removeTender(\''+divIdName+'\')">(Remove tender)</a></small>'
        + '</label>'
        + '<span><input id="pc:tender_gr:hasCurrencyValue'+num+'" name="pc:tender_gr:hasCurrencyValue'+num+'" class="element text currency" size="10" value="" type="text" /><label for="pc:tender_gr:hasCurrencyValue'+num+'">Price</label></span>'
        + '<span><select class="element select" id="pc:tender_gr:hasCurrency'+num+'" name="pc:tender_gr:hasCurrency'+num+'"><option value="" selected="selected"></option><option value="CZK">CZK</option><option value="EUR">EUR</option><option value="GBP">GBP</option></select><label for="pc:tender_gr:hasCurrency'+num+'">Currency</label></span>'
        + '<p class="guidelines" id="guide_4t_'+num+'"><small>pc:offeredPrice : Property for price offered by a supplier.<br />pc:supplier : Property for supplier submitting the tender.</small></p>' 
        + '<div><input type="text" id="pc:tender_pc:supplier'+num+'" name="pc:tender_pc:supplier'+num+'" value="" class="element text large" /><label for="pc:supplier">Supplier</label></div>';
    container_elem.appendChild(newdiv);
}

function removeTender(tenderId) {
  var container_elem = document.getElementById('li_4t');
  var old_tender = document.getElementById(tenderId);
  container_elem.removeChild(old_tender);
}