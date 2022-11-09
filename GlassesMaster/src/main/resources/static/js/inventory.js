// Global list of all popups
const popups = document.getElementsByClassName("popup");


// Function to show the popup when the filter is clicked
function showPopup(id) {
	var popup = document.getElementById(id);
  
	for (var i = 0; i < popups.length; i++) {
		var currPopup = popups[i];
		if (currPopup.style.display != "none") {
			currPopup.style.display = "none";
		}
	}
  
 	 if (popup.style.display == "none") {
		popup.style.display = "block";
	 }
  
  	 event.stopPropagation();
}


// Function to hide any open popups when clicked outside of them
window.onclick = event => {
	for (var i = 0; i < popups.length; i++) {
		var popup = popups[i];
		if (event.target != popup) {
			popup.style.display = "none";
		}
	}
  
	event.stopPropagation();
}


// Function to hide eyeglasses that don't match with selected filters (with helper functions)
function applyFilters() {
	var colors = getCheckedBoxes("color_filter");
	var styles = getCheckedBoxes("style_filter");
	var brands = getCheckedBoxes("brand_filter");
	
	var colorAttributes = document.getElementsByClassName("color_attribute");
	var styleAttributes = document.getElementsByClassName("style_attribute");
	var brandAttributes = document.getElementsByClassName("brand_attribute");
	
	for (var i = 0; i < colorAttributes.length; i++) {
		if ((!colors.length || colors.includes(colorAttributes[i].id)) && 
				(!styles.length || styles.includes(styleAttributes[i].id)) &&
				(!brands.length || brands.includes(brandAttributes[i].id))) {
			colorAttributes[i].parentElement.parentElement.parentElement.style.display = "block";
		}
		else {
			colorAttributes[i].parentElement.parentElement.parentElement.style.display = "none";
		}
	}
}

function getCheckedBoxes(name) {
	var checkboxes = document.getElementsByClassName(name);
	var checkedBoxes = [];
	
	for (var i = 0; i < checkboxes.length; i++) {
		if (checkboxes[i].checked) {
			checkedBoxes.push(checkboxes[i].name);
		}
	}
	
	return checkedBoxes;
}


// Function to clear filters
function clearFilters() {
	clearCheckboxes("color_filter");
	clearCheckboxes("style_filter");
	clearCheckboxes("brand_filter");
	
	applyFilters();
}

function clearCheckboxes(name) {
	var checkboxes = document.getElementsByClassName(name);
	
	for (var i = 0; i < checkboxes.length; i++) {
		checkboxes[i].checked = false;
	}
}


// Function to select/deselect a pair of glasses
function invertCheckbox(id) {
	var checkbox = document.getElementById(id + "_checkbox");
	checkbox.checked = !checkbox.checked;
	
	var element = document.getElementById(id);
	
	if (checkbox.checked) {
		element.style.border = "3px solid skyblue";
	} else if (!checkbox.checked) {
		element.style.border = "1px solid rgba(0, 0, 0, 0.3)";
	}
}