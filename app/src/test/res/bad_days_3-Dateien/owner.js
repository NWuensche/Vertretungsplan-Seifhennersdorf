$(document).ready(function(){
		$(".teaserboxen a").colorbox({
			ajax:true, 
			opacity: 0.8, 
			innerWidth:980, 
			innerHeight:"550px", 
			href: $(".teaserboxen a").attr('href') + " .popup-all" 
			//onOpen:function(){$('body').removeClass('box-info').addClass('box-teaser');} 
			});
			
			

		  $('#slider').flexslider({
			animation: "slide",
			directionNav: false,
			controlNav:true
		  });

});