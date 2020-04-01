let user;
window.onload = event => {
    setNavLinkActive('Home');
    getUserJson().then(userJson => {
        user = userJson;
        loadHomeTables(user);
        addListenerToModal();
    });
};

function loadHomeTables(user) {
    debug("loading home page tables");
    loadItemsTable(user.id, 'offered', 'tbody2');
    loadItemsTable(user.id, 'requested', 'tbody1');
}


function loadItemsTable(userId, type, tbodyId) {

    const url = new URL(`${getOrigin()}/items`); // GET items?userId=id&type={type};
    url.searchParams.append('type', type);
    url.searchParams.append('userId', userId);

    const tbody = getElementById(tbodyId);

    const onNextHandler = event => {
        const item = JSON.parse(event.data);
        clearTableBody(tbody, 'Loading...');
        const row = tbody.insertRow();
        row.insertCell().innerText = tbody.rows.length;
        row.insertCell().innerText = item.name;
        row.insertCell().innerText = item.quantity;
        row.insertCell().innerText = item.status;
        if (type === 'requested') {
            if (item.matchCount) {
                row.insertCell().innerHTML = createSearchLink(item);
            } else {
                row.insertCell().innerHTML = `no match`;
            }
        }
    };

    createEventSource(url, 'home-page-items', onNextHandler, streamCompleteHandler(tbody));
}

function streamCompleteHandler(tbody) {
    return event => {
        clearTableBody(tbody, 'Loading...');
        if (tbody.rows.length == 0) {
            const row = tbody.insertRow();
            row.innerHTML = `<td class="text-info text-center" colspan="5">No data available</td>`;
        }
    }
}

function createSearchLink(item) {
    return `<button type='button' class='btn btn-link btn-sm text-success' data-toggle='modal' data-target='#searchModal' 
        data-item='${toJson(item)}' >${item.matchCount} matches</button>`;
}

function addListenerToModal() {
    $('#searchModal').on('show.bs.modal', event => {
            const clickedButton = $(event.relatedTarget);
            const item = clickedButton.data('item');
            const tbody = getElementById('modalTbody1');
            createSpinnerRow(tbody);

            // create event source for search results
            const url = createItemSearchUrl('offered', item);
            const onNextHandler = event => {
                loadMatchingItemsTable(fromJson(event.data), getElementById('modalTbody1'));
            };
            createEventSource(url, 'search-items', onNextHandler);
        }
    ).on('hidden.bs.modal', event => {
            const tbody = getElementById('modalTbody1');
            clearTableBody(tbody);
        }
    )
}

function clearTableBody(tbody, text) {
    if (text) {
        if (tbody.innerText !== text) {
            return;
        }
    }
    tbody.innerHTML = '';
}


function createItemSearchUrl(type, item) {
    //make call to api : GET items?type=offered&name=item.name&qty=item.quantity&status=item.status&limit=100&sort=createdOnDesc;
    const url = new URL(`${getOrigin()}/items`);
    url.searchParams.append("type", type);
    url.searchParams.append("ne-userId", user.id);
    url.searchParams.append("name", item.name);
    url.searchParams.append("quantity", item.quantity);
    return url;
}

function loadMatchingItemsTable(item, tbody) {
    clearTableBody(tbody, 'Loading...');
    let index = tbody.rows.length + 1;
    const row = tbody.insertRow();
    row.innerHTML = `<input type='hidden' name='itemID' value='${item.id}' />`;
    row.insertCell().innerText = index;
    row.insertCell().innerText = item.name;
    row.insertCell().innerText = item.quantity;
    row.insertCell().innerText = item.userDTO.city;
    row.insertCell().innerHTML = `<button  type='button' class='btn btn-link btn-sm' onclick='submitOfferedItemRequest()'>apply</button>`;
}

function submitOfferedItemRequest() {
    const row = event.currentTarget.parentElement.parentElement;
    const itemId = row.getElementsByTagName('input')[0].value;
    const urlString = `${getOrigin()}/users/offered_items/requests`;
    const url = new URL(urlString);
    const request = {
        'userId': user.id,
        'offeredItemId': itemId
    };
    debug('sending demand to url : %o | request : %o', url, request);
    sendPostReq(url, request).then(
        response => {
            alert('request submitted api status : ' + response.status);
            $('#searchModal').modal('hide')
        }
    );
}

function createSpinnerRow(tbody) {
    clearTableBody(tbody);
    const row = tbody.insertRow();
    row.innerHTML =
        `<tr>
                    <td colspan="5" style="text-align: center">
                        <div class="spinner-border spinner-border-sm text-primary" role="status">
    <span class="sr-only">Loading...</span>
</div>
                    </td>
                </tr>`;
}


