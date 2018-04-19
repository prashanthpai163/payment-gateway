'use strict'; 
// Listen on the form's 'submit' handler...
  document.forms[0].addEventListener('submit', function(e) {
	  alert(1);
    e.preventDefault();
    createCharge();
    return false;
  });

  
  function createCharge() {
	  var cardNum = $("#card-number").val();
	  var cardExpiry = $("#card-expiry").val();
	  var cardCVV = $("#card-cvv").val();
	  
	  var object = {
		  card: cardNum,
		  expiry: cardExpiry,
		  cvv: cardCVV
	  };
	  
	  $.ajax({
		  url: "/pay",
		  method: "POST",
		  data:object,
		  success: function(response) {
			  console.log(object);
			  alert("Your reference number is this: "+ response.reference);
			  
		  },
		  error: function(response) {
			  console.log(response);
			  alert("request failed");
		  }
	  });
  }