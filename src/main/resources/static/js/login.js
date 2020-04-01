window.onload = e => {
    showErrorMessageIfAny();
};

function showErrorMessageIfAny() {
    const urlParams = new URLSearchParams(window.location.search);
    const error = urlParams.get('error');
    if (error) {
        alert(error);
    }
}

