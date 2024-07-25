console.log("okay");


const toggleSidebar = () => {

	if ($(".sidebar").is(":visible")) {

		// if visible then hide

		$(".sidebar").css("display", "none");
		$(".contant").css("margin-left", "0%");

	} else {

		$(".sidebar").css("display", "block");
		$(".contant").css("margin-left", "25%");

	}

};


function deleteContact(cid) {

	Swal.fire({
		title: "Are you sure?",
		text: "You want to delete this contact..",
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#3085d6",
		cancelButtonColor: "#d33",
		confirmButtonText: "Yes, delete it!"
	}).then((result) => {
		if (result.isConfirmed) {

			window.location = "/user/delete/" + cid;

			Swal.fire({
				title: "Deleted!",
				text: "Your Contact has been deleted.",
				icon: "success"
			});
		}
	});

};

// search bar

const search = () => {
	//	console.log("searching...");

	let query = $("#search-input").val();

	if (query == "") {

		$(".search-result").hide();

	} else {

		console.log(query);

		// sending request to server

		let url = `http://localhost:8082/search/${query}`;

		fetch(url).then((response) => {
			return response.json();
		}).then((data) => {
			//data.
			console.log(data);

			let text = `<div class='list-group'>`;

			data.forEach((contact) => {

				text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-action'> ${contact.name} </a>`

			});

			text += `</div>`;

			$(".search-result").html(text);
			 $(".search-result").show();
			
		});
    
	}
};
