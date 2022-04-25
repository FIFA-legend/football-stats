const countriesUrl = '/queue/countries/return';

const prefix = '/websockets';
const firstPageUrl = '/countries/start';
const nextPageUrl = '/countries/next';
const previousPageUrl = '/countries/previous';
const deletePageUrl = '/countries/delete/';
const updatePageUrl = '/country/update/';

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe('/user' + countriesUrl, processCountries);
});

setTimeout(function () {
    let previousButton = document.getElementById("previous_button");
    let nextButton = document.getElementById("next_button");
    clearTableBody();

    stomp.send(prefix + firstPageUrl, {});
    previousButton.addEventListener("click", function () {
        stomp.send(prefix + previousPageUrl, {});
    });
    nextButton.addEventListener("click", function () {
        stomp.send(prefix + nextPageUrl, {});
    });
}, 500);

function processCountries(income) {
    clearTableBody();
    let countries = JSON.parse(income.body);
    let tbody = document.getElementsByTagName("tbody")[0];
    for (let i = 0; i < countries.length; i++) {
        let tr = document.createElement("tr");
        addLineInTable(countries[i], tr);
        tbody.appendChild(tr);
    }
}

let clearTableBody = function () {
    let tbody = document.getElementsByTagName("tbody")[0];
    while (tbody.lastChild) {
        tbody.removeChild(tbody.lastChild);
    }
}

let addLineInTable = function (country, tr) {
    let countryName = document.createElement("td");
    let countryCode = document.createElement("td");
    let countryContinent = document.createElement("td");
    let upd = document.createElement("td");
    let del = document.createElement("td");

    tr.appendChild(countryName);
    tr.appendChild(countryCode);
    tr.appendChild(countryContinent);
    tr.appendChild(upd);
    tr.appendChild(del);

    countryName.innerHTML = country["name"];
    countryCode.innerHTML = country["code"];
    countryContinent.innerHTML = country["continent"];

    let updButton = document.createElement("button");
    updButton.classList.add("btn", "btn-outline-warning");
    upd.appendChild(updButton);
    updButton.innerText = "Update";
    updButton.onclick = function () {
        location.href = updatePageUrl + country["id"];
    };

    let delButton = document.createElement("button");
    delButton.classList.add("btn", "btn-outline-danger");
    del.appendChild(delButton);
    delButton.innerText = "Delete";
    delButton.addEventListener("click", function () {
        stomp.send(prefix + deletePageUrl + country["id"], {});
    });
}
