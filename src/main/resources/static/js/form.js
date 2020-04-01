const windowPath = window.location.pathname;
let user;
window.onload = () => {
    getUserJson().then(json => {
        user = json
    });
    setNavLinkActive('form');
};

function requestFormSelected() {
    return getElementById('formType').value === 'request';
}

function showToast(text) {
    const toast = getFirstElementByClassName("toast-body");
    toast.innerText = "Something went wrong ! " + text;
    $('#toast').toast('show');
}

function submitForm(event) {
    const button = getElementById("button1");
    const spinner = getFirstElementByClassName("submitSpinner");
    removeClass(spinner, 'd-none');
    button.disabled = true;
    const items = convertItemTableToArray(getElementById('tbody1'));
    const type = requestFormSelected() ? 'requested' : 'offered';
    const url = new URL(`${getOrigin()}/items/${type}`);
    sendPostReq(url, items).then(response => {
        if (response.status === 200) {
            alert("Request Submitted");
            goHome();
        } else {
            addClass(spinner, 'd-none');
            button.disabled = false;
            response.text().then(text => showToast(text));
        }
    });
}

function convertItemTableToArray(table) {
    // get data from form
    const rows = table.rows;
    const items = [];
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const itemName = row.getElementsByClassName('itemName')[0];
        const quantity = row.getElementsByClassName('itemQuantity')[0];
        const item = {};
        const userDTO = {id: user.id};
        item['userDTO'] = userDTO;
        item['name'] = itemName.value;
        item['quantity'] = quantity.valueAsNumber;
        items.push(item);
    }
    return items;
}

function addRowToForm() {
    const tbody1 = getElementById("tbody1");
    const defaultRow = tbody1.rows[0];
    const cloneRow = defaultRow.cloneNode(true);
    const inputs = cloneRow.getElementsByTagName('input');
    for (const element of inputs) {
        element.value = '';
    }
    const removeButton = cloneRow.getElementsByClassName('removeButton')[0];
    removeClass(removeButton, 'invisible');
    tbody1.appendChild(cloneRow);
}

function removeRow(link) {
    info("got remove row event %o", link);
    const tr = link.parentNode.parentElement;
    const tbody1 = getElementById("tbody1");
    tbody1.removeChild(tr);
}
