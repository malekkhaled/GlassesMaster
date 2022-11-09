const form = document.querySelector('form');
const submitButton = form.querySelector('[type="submit"]');
const inputFile = form.querySelector('[type="file"]');

inputFile.addEventListener('change', () => {
	// Enable button
	if (inputFile.files.length) {
		submitButton.disabled = false;
	}
});

// Loading cursor when file submitted
function changeCursor() {
	document.querySelector('html').style.cursor = "wait";
}