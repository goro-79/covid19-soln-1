window.onload = e => {
    addValidationListeners();
    addValidationForEmail();
    addValidationForPassword();
    setCity('city');
};

let geo;

function setCity(id) {
    const url = new URL(getOrigin() + "/data/cities");
    const select = getElementById(id);
    const onNext = event => {
        const data = fromJson(event.data);
        geo = data;
        getElementById(id).value = data.city;
    };
    createEventSource(url, 'city', onNext);
}


function addValidationListeners() {
    let inputs = getElementsByTagName('input');
    for (const element of inputs) {
        if (element !== getEmail())
            element.addEventListener('input', e => showValidation(e));
    }
}

function addValidationForEmail() {
    const email = getEmail();
    const tooltip = getElementById("emailToolTip");
    email.addEventListener('input', e => {
        const valid = email.checkValidity();
        if (valid) {
            removeClass(email, 'is-invalid');
        } else {
            tooltip.innerText = email.validationMessage;
            addInValidClass(email);
        }
    });

    email.addEventListener('blur', e => checkIfEmailAlreadyExists(e));
}

function addValidationForPassword() {
    const password = getElementById("password");
    const confirmPassword = getElementById("confirmPassword");
    const passwordToolTip = getElementById("passwordToolTip");
    confirmPassword.addEventListener('input', e => {
        if (confirmPassword.value !== password.value) {
            passwordToolTip.innerText = 'Passwords do not match.';
            addInValidClass(confirmPassword);
        }
    })
}

function checkIfEmailAlreadyExists(event) {
    const email = getEmail();
    const tooltip = getElementById("emailToolTip");
    const msg = 'User with this email already exists.';

    sendHeadReq("/users?email=" + email.value).then(
        response => {
            if (response.status === 200) {
                tooltip.innerText = msg;
                addInValidClass(email)
            } else if (response.status === 404) {
                switchClass(email, 'is-valid', 'is-invalid');
            }
        }
    )
}


function submitForm(event) {
    const button = getElementById("button");
    button.disabled = true;
    button.textContent = "Please wait ...";
    const form = getElementById('form');
    const formData = new FormData(form);
    const body = toJsonFromForm(formData);
    body.country = geo.country;
    body.zip = geo.zip;

    sendPostReq("/users", body)
        .then(response => {
            info("api response received : %o", response);
            if (response.status === 201) {
                alert("Sign up complete. Click to go to login page.");
                redirect("/");
            } else {
                response.json().then(json => {
                    warn("sign up failed due to %o", json);
                    alert("Sign up failed due to " + json);
                    button.disabled = false;
                    button.textContent = "Try again";
                });
            }
        });
}

function getEmail() {
    return getElementById("email");
}

